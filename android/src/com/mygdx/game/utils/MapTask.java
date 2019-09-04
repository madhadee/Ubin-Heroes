package com.mygdx.game.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.activities.MapActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;

import java.util.List;
import java.util.Locale;

/**
 * Running an Async Task for the Google Map
 */
public class MapTask extends AsyncTask<GoogleMap, Void, String> {
    private WeakReference<Activity> wr_act;
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mQRBinsRef;
    private GoogleMap mMap;

    /**
     * Creating a weak reference
     * @param act MapActivity
     */
    public MapTask(Activity act) {
        wr_act = new WeakReference<>(act);
    }

    /**
     * Adding UI elements of Ubins markers with postal code and scan information as a background task
     * @param googleMaps The map itself
     */
    @Override
    protected String doInBackground(GoogleMap... googleMaps) {
        mMap = googleMaps[0];
        MarkerOptions binMarker = new MarkerOptions();
        Bitmap mapIcon = MapActivity.resizeDrawable(wr_act.get(),R.drawable.ic_map_bin,80,80);
        binMarker.icon(BitmapDescriptorFactory.fromBitmap(mapIcon));
        mQRBinsRef = mRootReference.child(Constants.DB_REF_QR_BINS);
        mQRBinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    LatLng binLocasi = geoCoder(userSnapshot.child("location").getValue().toString());
                    if (binLocasi != null) {
                        binMarker.position(binLocasi);
                        binMarker.title("Postal Code: " + userSnapshot.child("location").getValue().toString());
                        binMarker.snippet("Number of times items recycled here: " + userSnapshot.child("scanCount").getValue().toString());
                        mMap.addMarker(binMarker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return null;
    }

    /**
     * To convert postal code into readable coordinates of long and lat
     * @param postalCode Desired postal code
     * @return coordinates
     */
    private LatLng geoCoder(String postalCode){
        Geocoder geocoder = new Geocoder(wr_act.get(), Locale.getDefault());
        LatLng latLng;
        try {
            List<Address>addresses = geocoder.getFromLocationName(postalCode, 1, 1.2276, 103.5970, 1.4754, 104.0254);
            if (addresses.size() > 0)
            {
                latLng = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
                return latLng;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
