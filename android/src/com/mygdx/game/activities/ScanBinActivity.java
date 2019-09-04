package com.mygdx.game.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.utils.Constants;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ScanBinActivity extends BaseActivity {
    private SurfaceView cameraPreview;
    private boolean qrFlag;
    private double userLongitude, userLatitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private DatabaseReference mUseableRef;

    /**
     * Permissions to be set and used
     */
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bin);

        // Initialize UI
        initUI();
        createCameraSource();

        // Request permissions if user has denied previously
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.PERMISSION_ALL);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Initialise UI elements from XML
     */
    private void initUI() {
        cameraPreview = findViewById(R.id.textureCamera);
    }

    /**
     * Check constantly for user current location or when user is on the move
     * @param location Current user location
     */
    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        Log.d(Constants.TAG_SCAN_BIN_ACTIVITY, msg);
        // You can now create a LatLng Object for use with maps
        userLongitude = location.getLongitude();
        userLatitude = location.getLatitude();
        String newMsg = "Updated Location Setting local variable: " +
                Double.toString(userLongitude) + "," +
                Double.toString(userLatitude);
        Log.d(Constants.TAG_SCAN_BIN_ACTIVITY, newMsg);
    }

    /**
     * Check for permission already or not
     * @param context Current Activity
     * @param permissions Permissions to check
     * @return Status ok
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void fetchLocation() {
            // Permission has already been granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

            } else {
            // get the users location continuosly
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_ALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                startActivity(getIntent());
            } else {
                //permission denied
            }
        }
    }

    // create the camera texture on the UI
    private void createCameraSource() {
        BarcodeDetector bd = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, bd)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1080)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                // check permissions first, but should have been grandted
                if (ActivityCompat.checkSelfPermission(ScanBinActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        bd.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeArray = detections.getDetectedItems();
                fetchLocation();
                if (barcodeArray.size()> 0 && (userLatitude != 0 || userLongitude != 0) && !qrFlag) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            //Prevent another scan detection
                            qrFlag = true;

                            //Get qr Value, check if valid
                            Pair<String,Integer> qrValue = validateQR(barcodeArray.valueAt(0).displayValue);

                            //If qr code is not ubinHeroes QR/ not in db
                            if (!qrValue.first.equals("")) {
                                Intent intent = new Intent(ScanBinActivity.this, ScanBinResultActivity.class);
                                intent.putExtra("barcode_id", barcodeArray.valueAt(0).displayValue);
                                intent.putExtra("barcode_postalCode", qrValue.first);
                                intent.putExtra("barcode_scan_number", qrValue.second);
                                intent.putExtra("longitude",userLongitude);
                                intent.putExtra("latitude",userLatitude);
                                startActivityForResult(intent,1);
                                finish();
                            } else {
                                //to allow more qr scanning since qrInvalid
                                qrFlag = false;
                            }
                        }
                    };
                    thread.start();
                }
            }
        });
    }

    public Pair<String, Integer> validateQR(String qrCode) {
        mUseableRef = mRootReference.child("qrCode");
        Task<Pair<String,Integer>> qrValue = findValidQR(qrCode);
        Pair<String, Integer> result = new Pair<>("",0);
        try {
            Tasks.await(qrValue);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (qrValue.isSuccessful()) {
            result = qrValue.getResult();
        }
        Log.d("TEST", "retrieveQRValue: "+ result);
        return result;
    }

    public Task<Pair<String,Integer>> findValidQR(String qrcode){
        final TaskCompletionSource<Pair<String,Integer>> tcs = new TaskCompletionSource();
        mUseableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Pair<String, Integer> qrValue = new Pair<>("",0);
                for (DataSnapshot qrObject : dataSnapshot.getChildren()) {
                    if(qrcode.equals(qrObject.getKey())) {
                        qrValue = new Pair<>(qrObject.child("location").getValue().toString(), Integer.valueOf(qrObject.child("scanCount").getValue().toString()));
                        break;
                    }
                }
                tcs.setResult(qrValue);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });
        return tcs.getTask();
    }
}