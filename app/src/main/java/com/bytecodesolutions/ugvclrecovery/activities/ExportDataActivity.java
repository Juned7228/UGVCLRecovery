package com.bytecodesolutions.ugvclrecovery.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bytecodesolutions.ugvclrecovery.CSVImporter;
import com.bytecodesolutions.ugvclrecovery.DatabaseHelper;
import com.bytecodesolutions.ugvclrecovery.R;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExportDataActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    DatabaseHelper databaseHelper;
    ProgressDialog progressDialog ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_data_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        databaseHelper=new DatabaseHelper(this);
        findViewById(R.id.btn_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportConsumers();
            }
        });
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
    public void exportConsumers(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        } else {
            Executor executor = Executors.newSingleThreadExecutor();
            showProgress();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    CSVImporter.ExporttoExcel(ExportDataActivity.this,databaseHelper.getAllItems());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            //File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            File exportDir=getExternalFilesDir(null);
                            File file = new File(exportDir, "recovery.xlsx");
                            shareExcelFile(ExportDataActivity.this,file);
                        }
                    });
                }
            });


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportConsumers();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private static void shareExcelFile(Context context, File file) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        Uri contentUri = FileProvider.getUriForFile(context, "com.bytecodesolutions.ugvclrecovery.fileprovider", file);


        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Raw File");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this raw file!");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        context.startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
    }

    private void showProgress(){
        progressDialog.setMessage("Loading..."); // Set a message to display to the user.
        progressDialog.setCancelable(false); // Set whether the user can cancel the dialog with the back button.
        progressDialog.show();
    }
    private void hideProgress(){
        progressDialog.cancel();
    }
}
