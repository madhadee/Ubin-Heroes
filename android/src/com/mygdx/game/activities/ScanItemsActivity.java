package com.mygdx.game.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// cloud vision imports
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// local imports
import com.mygdx.game.models.RecyclableObject;
import com.mygdx.game.utils.*;
import com.mygdx.game.R;

// java util imports
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//Worker imports
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class ScanItemsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mImageDetails;
    private Button mDone;
    private Button mScanMore;
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_items);

        //Initialize UI
        initUI();
        mAuth = FirebaseAuth.getInstance();
        startCamera();
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnScanMore:
                startCamera();
                break;
            case R.id.btnDone:
                //return to ScanOptionsActivity
                finish();
                break;
        }
    }

    /**
     * Initialize UI components
     */
    private void initUI(){
        mImageDetails = findViewById(R.id.tvImageDetails);
        //Hide loading panel
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        //Buttons init
        mScanMore = findViewById(R.id.btnScanMore);
        mDone = findViewById(R.id.btnDone);

        //Button listener
        findViewById(R.id.btnScanMore).setOnClickListener(this);
        findViewById(R.id.btnDone).setOnClickListener(this);
    }

    /**
     *This function allows the user to launch the hardware camera
     * Permissions are needed to be checked before the camera is able to launch. if = true
     * The camera will launch
     */
    private void startCamera() {
        if (PermissionUtils.requestPermission(this, Constants.CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // here receives the results of the camera image
            startActivityForResult(intent, Constants.CAMERA_IMAGE_REQUEST);
        }
    }

    /**
     *this function will return the picture directory from the camera and is called in onActivityResults
     * @return the file directory
     */
    private File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, Constants.FILE_NAME);
    }

    /**
     *once the image is returned from the camera will launch uploadImage
     * @param requestCode check if result is from camera
     * @param resultCode return result did not fail
     * @param data intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        } else {
            // case when user presses back on camera and goes back to previous activity
            finish();
        }
    }

    /**
     * this function sends the image to scaleBitmapDown that scales down the image
     * before it sends the image to cloudVision for object detection
     * and some validations
     * @param uri image file location
     */
    private void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                Constants.MAX_DIMENSION);

                callCloudVision(bitmap);
            } catch (IOException e) {
                Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.msg_image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.msg_image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Function courtesy of Google Cloud Vision for object detection
     * call in Asynchronous task
     * @param bitmap image to send to google
     * @return objected that gets converted to string
     * @throws IOException when there is an exception
     */
    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(Constants.CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(Constants.ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(Constants.ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            //Add the image
            Image base64EncodedImage = new Image();
            //Convert the bitmap to a JPEG
            //Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            //Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            //Add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(Constants.MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            //Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
        //Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    /**
     * Asynchronous task that launches the object detection
     */
    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<ScanItemsActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(ScanItemsActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API Request Failed. Check Internet Connection";
        }

        /**
         * after the successful execution of the object detection
         * function StoreRecyclableItems is called to store the objects into firebase
         *
         * @param result string that returns to the UI
         */
        protected void onPostExecute(String result) {
            ScanItemsActivity activity = mActivityWeakReference.get();

            if (activity != null && !activity.isFinishing()) {
                // initialize UI Variables, as its a weak reference to the activity
                TextView imageDetail = activity.findViewById(R.id.tvImageDetails);
                ImageView imageView =activity.findViewById(R.id.ivObjScanned);
                // to split results parts[0] stores the object realised, and parts[1] stores the object detected
                String parts[] = result.split(";");
                //UI buttons enabled
                activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                activity.findViewById(R.id.btnScanMore).setEnabled(true);
                activity.findViewById(R.id.btnDone).setEnabled(true);

                // check string valid
                if (parts[0].length() !=0 && parts.length > 1) {
                    activity.findViewById(R.id.tvObjDetails).setVisibility(View.VISIBLE);
                    activity.storeRecyclableItems(parts[0]); // calls firebase

                    String text = activity.getString(R.string.msg_item_received, parts[1], parts[0]);
                    // display on UI
                    if (parts[0].equalsIgnoreCase("Paper")){
                        imageDetail.setText(text);
                        imageView.setImageResource(R.drawable.ic_paper);
                    } else if (parts[0].equalsIgnoreCase("Metal")){
                        imageDetail.setText(text);
                        imageView.setImageResource(R.drawable.ic_metal);
                    } else if (parts[0].equalsIgnoreCase("Plastic")){
                        imageDetail.setText(text);
                        imageView.setImageResource(R.drawable.ic_plastic);
                    }

                } else {
                    // case where object can't be identified to a paper, metal or plastic
                    String text =activity.getString(R.string.msg_item_received_error, parts[1]);
                    activity.findViewById(R.id.tvObjDetails).setVisibility(View.GONE);
                    imageView.setImageResource(0);
                    imageDetail.setText(text);
                }
            }
        }
    }

    /**
     * this function will create an object and store in the users FireBase
     * @param result is the string that the object is
     */
    private void storeRecyclableItems(String result) {
        final FirebaseUser userFirebase = mAuth.getCurrentUser();
        Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, userFirebase.getUid());
        Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "Material is " + result);
        Date date = new Date();
        String uniqueID = "R" + String.valueOf(date.getTime());

        if (result.equals("Paper")){
            RecyclableObject recyclableObject = new RecyclableObject(Constants.DB_REF_PAPER, date, 0);
            mRootReference.child(Constants.DB_REF_RECYCLABLE_OBJECT).child(userFirebase.getUid()).child(uniqueID).setValue(recyclableObject);
            sendNotifications();
        }
        if (result.equals("Metal")){
            RecyclableObject recyclableObject = new RecyclableObject(Constants.DB_REF_METAL, date, 0);
            mRootReference.child(Constants.DB_REF_RECYCLABLE_OBJECT).child(userFirebase.getUid()).child(uniqueID).setValue(recyclableObject);
            sendNotifications();
        }
        if (result.equals("Plastic")){
            RecyclableObject recyclableObject = new RecyclableObject(Constants.DB_REF_PLASTIC, date, 0);
            mRootReference.child(Constants.DB_REF_RECYCLABLE_OBJECT).child(userFirebase.getUid()).child(uniqueID).setValue(recyclableObject);
            sendNotifications();
        }
    }

    /**
     * function will call a Worker to send notifcations to the user
     * when the item is going to expire
     * Time sets are at 30Mins for first notification and 59 Mins second notification
     */
    private void sendNotifications(){
        // add some constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        // data builder to differenciate the notifications
        Data myData = new Data.Builder()
                .putString(Constants.KEY_X_ARG, "one")
                .build();

        Data myData2 = new Data.Builder()
                .putString(Constants.KEY_X_ARG, "two")
                .build();

        //first notification that fires after 30 minutes
        OneTimeWorkRequest firstNotification =
                new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setConstraints(constraints)
                        .addTag(Constants.WORKER_TAG)
                        .setInputData(myData)
                        .setInitialDelay(30, TimeUnit.MINUTES) //first notification fires in 15mins
                        .build();

        // second notification that fires after 59 mins when item about to expire
        OneTimeWorkRequest secondNotification =
                new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setConstraints(constraints)
                        .setInputData(myData2)
                        .addTag(Constants.WORKER_TAG)
                        .setInitialDelay(59, TimeUnit.MINUTES) // warning notification fires after 30mins from the launch of the worker
                        .build();

        // chaining work
        WorkManager.getInstance()
                .beginWith(firstNotification)
                .then(secondNotification)
                .enqueue();
    }

    /**
     * this is called in the uploadImage function
     *
     * @param bitmap after the scale down of image the asynctask can be launched
     */
    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        mImageDetails.setText(R.string.msg_object_detection);
        // display loading image
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        // disable UI buttons
        mScanMore.setEnabled(false);
        mDone.setEnabled(false);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    /**
     * this function scales down the image resolution and returns back
     * @param bitmap the image to parse in
     * @param maxDimension the dimension set for the scale down
     * @return new scaled down image
     */
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    /**
     * converting the response to strings
     * gets strings from google cloud vision after object detection
     * presets some plastic, metal and paper items in array to match incoming objects
     * @param response object from google cloud vision
     * @return a string
     */
    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        // returns the type of object
        StringBuilder message = new StringBuilder("");
        String PLASTIC_ITEMS[] = {"bottle", "cup", "plastic", "water bottle", "plastic bag"};
        String METAL_ITEMS[] = {"tin", "can", "metal", "aluminum can", "beverage can","electronics","gadget", "watch"};
        String PAPER_ITEMS[] = {"paper", "book", "writing", "paper product","text", "advertising"};

        // stores the actual object detected
        StringBuilder items = new StringBuilder("");

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                // check if array has the items
                boolean containsPlastic = Arrays.asList(PLASTIC_ITEMS).contains(label.getDescription().toLowerCase(Locale.ENGLISH));
                boolean containsMetal = Arrays.asList(METAL_ITEMS).contains(label.getDescription().toLowerCase(Locale.ENGLISH));
                boolean containsPaper = Arrays.asList(PAPER_ITEMS).contains(label.getDescription().toLowerCase(Locale.ENGLISH));
                Log.d(Constants.TAG_SCAN_ITEMS_ACTIVITY,"ITEMS : " + label.getDescription());
                String res = label.getDescription() +";";

                items.append(res);
                if (containsPlastic) {
                    message.append("Plastic");
                    items.setLength(0);
                    items.append(label.getDescription());
                    break;
                }
                if (containsMetal) {
                    message.append("Metal");
                    items.setLength(0);
                    items.append(label.getDescription());
                    break;
                }
                if (containsPaper) {
                    message.append("Paper");
                    items.setLength(0);
                    items.append(label.getDescription());
                    break;
                }
            }
        } else {
            // if no object can be detected return nil value
            items.append("NIL");
        }
        return message.toString() +";"+ items.toString();
    }

    /**
     * permissions request overrride
     * @param requestCode returned
     * @param permissions returned
     * @param grantResults results returned
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, Constants.CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
        }
    }
}