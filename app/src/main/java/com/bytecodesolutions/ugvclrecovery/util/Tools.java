package com.bytecodesolutions.ugvclrecovery.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bytecodesolutions.ugvclrecovery.constants.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Tools {
    public interface FBDataLoader{
        void onDataLoaded();
    }
    public interface LocationListener{
        void onLocationFind(Location location);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void readFirebaseAds(Context context){
        if(isNetworkAvailable(context)){
            System.out.println("Reading FB data");
            AdsHelper adsHelper=new AdsHelper(context);
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference myRef = database.child("ads");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String,Object> map = snapshot.getValue(genericTypeIndicator);
                    PrefManager prefManager=new PrefManager(context, Firebase.PREF_FB);
                    prefManager.putIntValue(Firebase.AD_FLAG,((Long)map.get(Firebase.AD_FLAG)).intValue());
                    prefManager.putIntValue(Firebase.BANNER_MAX,((Long)map.get(Firebase.BANNER_MAX)).intValue());
                    prefManager.putIntValue(Firebase.FB_UPDATE_DURATION,((Long)map.get(Firebase.FB_UPDATE_DURATION)).intValue());
                    prefManager.putIntValue(Firebase.FRAUD_NO_AD_DURATION,((Long)map.get(Firebase.FRAUD_NO_AD_DURATION)).intValue());
                    prefManager.putIntValue(Firebase.MAX_CLICK,((Long)map.get(Firebase.MAX_CLICK)).intValue());
                    prefManager.putIntValue(Firebase.NO_AD_DURATION,((Long)map.get(Firebase.NO_AD_DURATION)).intValue());
                    adsHelper.setFBUpdateTime();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

//    public static void getlocation(AppCompatActivity activity) {
//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//        }
//        else{
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//// Do something with the location.
//                                double latitude = location.getLatitude();
//                                double longitude = location.getLongitude();
//                                System.out.println(latitude);
//                                System.out.println(longitude);
//                                Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latitude+","+longitude);
//                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                                mapIntent.setPackage("com.google.android.apps.maps");
//                                if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
//                                    activity.startActivity(mapIntent);
//                                }
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(Exception e) {
//// Handle the error.
//                        }
//                    });
//        }
//    }

}



