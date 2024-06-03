package com.bytecodesolutions.ugvclrecovery.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bytecodesolutions.ugvclrecovery.BuildConfig;
import com.bytecodesolutions.ugvclrecovery.R;
import com.bytecodesolutions.ugvclrecovery.util.AdsHelper;
import com.bytecodesolutions.ugvclrecovery.util.Tools;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NavigationActivity extends AppCompatActivity {
    AppUpdateManager appUpdateManager;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    String TAG = "NavigationActivity";
    AdsHelper adsHelper;
    ActivityResultLauncher<Intent> updateLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        adsHelper = new AdsHelper(this);
        if (Tools.isNetworkAvailable(this)) {
            processInAppUpdate();

        }
        if (adsHelper.canUpdateFBData()) {
            Tools.readFirebaseAds(this);
        }


        findViewById(R.id.card_rec_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(NavigationActivity.this, MainActivity.class));


            }
        });
        findViewById(R.id.card_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NavigationActivity.this, FileUploadActivity.class));
            }
        });
        findViewById(R.id.card_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NavigationActivity.this, ExportDataActivity.class));
            }
        });
        findViewById(R.id.card_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NavigationActivity.this, AboutAppActivity.class));
            }
        });
    }


    private void processInAppUpdate() {
        ActivityResultLauncher activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // handle callback

                        if (result.getResultCode() != RESULT_OK) {

                            // If the update is cancelled or fails,
                            // you can request to start the update again.
                        }
                    }
                });
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                // Request the update


                appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // an activity result launcher registered via registerForActivityResult
                        activityResultLauncher,
                        // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                        // flexible updates.
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                                .setAllowAssetPackDeletion(true)
                                .build());
                InstallStateUpdatedListener listener = state -> {
                    // (Optional) Provide a download progress bar.
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                        // Implement progress bar.
                    }
                    // Log state or install the update.
                };
                appUpdateManager.registerListener(listener);
            }

        });

    }
//    private void inAppUpdate(){
//        appUpdateManager = AppUpdateManagerFactory.create(this);
//
//        updateLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK) {
//                        // The update was downloaded and installed successfully.
//                    } else if (result.getResultCode() == RESULT_CANCELED) {
//                        // The update was not downloaded or was canceled by the user.
//                    } else {
//                        // An error occurred during the update.
//                    }
//                });
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
//                // Request the update
//                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, 1001);
//            }
//        });
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == updateLauncher.) {
//            updateLauncher.unregister();
//        }
//    }
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.activity_navigation),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());

        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(appUpdateManager!=null){
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {

                        // If the update is downloaded but not installed,
                        // notify the user to complete the update.
                        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                            popupSnackbarForCompleteUpdate();
                        }
                    });
        }

    }



}
