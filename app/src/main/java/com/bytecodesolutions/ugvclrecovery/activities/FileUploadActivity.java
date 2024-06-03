package com.bytecodesolutions.ugvclrecovery.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bytecodesolutions.ugvclrecovery.CSVImporter;
import com.bytecodesolutions.ugvclrecovery.DatabaseHelper;
import com.bytecodesolutions.ugvclrecovery.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FileUploadActivity extends AppCompatActivity {
Button btn_import;
ActivityResultLauncher<Intent> someActivityResultLauncher;
ProgressDialog progressDialog ;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        btn_import=findViewById(R.id.btn_import);
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");  // You can specify a specific MIME type here if needed


                someActivityResultLauncher.launch(intent);
            }
        });
       findViewById(R.id.tv_sample_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    copySampleFile();
                    shareSampleFile();
                    /* Uri uri = Uri.parse("android.resource://"+getPackageName()+"/raw/dc.xlsx");
                    //   Uri uri=Uri.parse("file:///android_res/raw/dc.xlsx");*/




            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            if (data != null) {
                                // Get the URI of the selected file
                                Uri uri = data.getData();
                               // Toast.makeText(FileUploadActivity.this,"do operation "+uri.getPath()+""+uri.getPath(),Toast.LENGTH_LONG).show();

                                // Read the file's content using the ContentResolver
                                //ContentResolver contentResolver = getContentResolver();
                                try {
                                  //  InputStream inputStream = contentResolver.openInputStream(uri);
                                    if (uri != null) {
                                        processImport(uri);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });

    }

    private void shareSampleFile() {
       // Uri contentUri = FileProvider.getUriForFile(FileUploadActivity.this, "com.bytecodesolutions.ugvclrecovery.fileprovider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "recovery_sample.xlsx"));
        Uri contentUri = FileProvider.getUriForFile(FileUploadActivity.this, "com.bytecodesolutions.ugvclrecovery.fileprovider", new File(getExternalFilesDir(null), "recovery_sample.xlsx"));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // You can set the MIME type according to the file type you're sharing
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

// Optionally, you can add a subject and text to the sharing message
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Raw File");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this raw file!");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

// Start the sharing activity21547/
        /*List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }*/

      /*  List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }*/
        startActivity(Intent.createChooser(shareIntent, "Share File"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: // Handle the "Up" button click event
                super.onBackPressed(); // Optionally, you can use this to mimic the back button behavior
                break;
        }
        return true;
    }
    private void processImport(Uri uri){
        // Create a single-threaded executor
        showProgress();
        Executor executor = Executors.newSingleThreadExecutor();

        // Execute a background task using the executor
        executor.execute(new Runnable() {
            @Override
            public void run() {

                // Perform your background task here
                // This code will run on a separate thread managed by the executor
                // For example, you could make a network call, perform database operations, etc.
                try {
                    CSVImporter.importCSV(FileUploadActivity.this,uri);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update the UI based on the background task's result
                           // SystemClock.sleep(5000);
                            hideProgress();
                            //Toast.makeText(FileUploadActivity.this,"Operation Completed ",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(FileUploadActivity.this,MainActivity.class));
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            new DatabaseHelper(FileUploadActivity.this).deleteAllDataFromTable();
                            showAlertDialog();
                           // Toast.makeText(FileUploadActivity.this,"Invalid File",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                // Update UI on the main thread if needed

            }
        });
    }

    private void showProgress(){
        progressDialog.setMessage("Loading..."); // Set a message to display to the user.
        progressDialog.setCancelable(false); // Set whether the user can cancel the dialog with the back button.
        progressDialog.show();
    }

    private void hideProgress(){
        progressDialog.dismiss();
    }


    private void showAlertDialog(){
        // Create an AlertDialog.Builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

// Set the title and message of the AlertDialog
        alertDialogBuilder.setTitle("Invalid");
        alertDialogBuilder.setMessage("Invalid File , Select Appropriate Formated File");

// Add a positive button
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform actions when the "OK" button is clicked.
                dialog.dismiss();
            }
        });

// Create the AlertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();

// Show the AlertDialog
        alertDialog.show();

    }

    private void copySampleFile(){
    try {


        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        boolean fileCopied = sharedPreferences.getBoolean("fileCopied", false);
        // Copy the resource raw file to the file directory here.
        // You can use FileInputStream and FileOutputStream or any other suitable method.
        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // File exportDir =context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(getExternalFilesDir(null), "recovery_sample.xlsx");
       // File file = new File(exportDir, "recovery_sample.xlsx");


        if (!fileCopied) {

            try {

                InputStream inputStream = getResources().openRawResource(R.raw.dc);
                //File outputFile = new File(getFilesDir(), "recovery_sample.xlsx");

                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
                // After copying, update the SharedPreferences to mark that the file has been copied.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("fileCopied", true);
                editor.apply();
                Toast.makeText(FileUploadActivity.this, "File saved at " + file.getPath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();

            }


        }
    }catch (Exception e){
        e.printStackTrace();
    }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                copySampleFile();
                shareSampleFile();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}