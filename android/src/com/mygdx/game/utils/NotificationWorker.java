package com.mygdx.game.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.activities.ScanBinActivity;

import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;

    public NotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        //fetch args from activity
        String x = getInputData().getString(Constants.KEY_X_ARG);

        // if String x is not null send a notifcation. based on the activity that sends out this key. 
        if(getInputData().getString(Constants.KEY_X_ARG) !=null){
            addNotification(x);

        }
        // get the "uniqueID" from activitty in key y args
        if (getInputData().getString(Constants.KEY_Y_ARG) != null) {
            deleteFromFirebase(getInputData().getString(Constants.KEY_Y_ARG));
        }
        return Result.success();
    }

    private void deleteFromFirebase(String uniqueID){
        cancelWork();
        final FirebaseUser userFirebase = mAuth.getCurrentUser();
        //remove object from firebase
        mRootReference.child(Constants.DB_REF_RECYCLABLE_OBJECT).child(userFirebase.getUid()).child("R"+uniqueID).child("verified").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // data available in snapshot.value()
                try {
                    long verified = Long.parseLong(snapshot.getValue().toString());

                    if (verified == 1 || verified == 0) {
                        mRootReference.child(Constants.DB_REF_RECYCLABLE_OBJECT).child(userFirebase.getUid()).child("R" + uniqueID).removeValue();
                    }
             } catch (NullPointerException f){
                    f.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    private void cancelWork(){
        WorkManager.getInstance().cancelAllWorkByTag("TimerJob1");
    }

    private void addNotification(String notiID) {
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "UbinHeroes";
        String Description = "This is my channel";

        String BigText = "";
        int NOTIFICATION_ID = 0;

        if (notiID.equals("one")) {
            BigText = "You have only 15 Mins left to Scan your QR to claim your items. Scan them now !";
            NOTIFICATION_ID = 234;
        } else {
            BigText = "You did not scan a QR to verify, Your items will be deleted :( Please Re-scan them !";
            NOTIFICATION_ID = 235;
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 100});
            mChannel.setShowBadge(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        Intent resultIntent = new Intent(getApplicationContext(), ScanBinActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("TrashMaster")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(BigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_delete_black_24dp)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_foreground, "Scan QR Now", resultPendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}