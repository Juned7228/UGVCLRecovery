package com.bytecodesolutions.ugvclrecovery.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bytecodesolutions.ugvclrecovery.DatabaseHelper;
import com.bytecodesolutions.ugvclrecovery.R;
import com.bytecodesolutions.ugvclrecovery.model.Consumer;
import com.bytecodesolutions.ugvclrecovery.util.AdsHelper;
import com.bytecodesolutions.ugvclrecovery.util.Tools;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.net.URLEncoder;

public class ShowConsumerActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    private static final int REQUEST_PHONE_CALL = 1;
    Consumer consumer;
    int banClickCount=0;
    AdsHelper adsHelper;
    AdView mAdView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_consumer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adsHelper=new AdsHelper(this);
        if(Tools.isNetworkAvailable(this)){
            load_banner();
        }
        int id=getIntent().getIntExtra("id",1);
        databaseHelper=new DatabaseHelper(this);
         consumer=databaseHelper.getConsumerById(id);
        ((TextView)findViewById(R.id.tv_show_num)).setText(consumer.getNum());
        ((TextView)findViewById(R.id.tv_show_name)).setText(consumer.getName());
        ((TextView)findViewById(R.id.tv_show_add1)).setText(consumer.getAddress1());
        ((TextView)findViewById(R.id.tv_show_tarif)).setText(consumer.getTarif());
        ((TextView)findViewById(R.id.tv_show_meter)).setText(consumer.getMeterno());
        ((TextView)findViewById(R.id.tv_show_root)).setText(consumer.getRootcode());
        ((TextView)findViewById(R.id.tv_show_amount)).setText("Rs."+consumer.getAmount());
        ((TextView)findViewById(R.id.tv_show_type)).setText(consumer.getType());
        if(consumer.getType().equals("NORMAL")){
            findViewById(R.id.btn_wa).setVisibility(View.GONE);
        }
        findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });
        findViewById(R.id.btn_wa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsAppWithMessage(consumer.getMobile(),"");
            }
        });
        ((EditText)findViewById(R.id.ed_show_remarks)).setText(consumer.getRemarks());
        findViewById(R.id.btn_save_remarks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               int i= databaseHelper.updateRemarks(id,((EditText)findViewById(R.id.ed_show_remarks)).getText().toString());
               if(i==-1){

               }
               else{
                   Toast.makeText(ShowConsumerActivity.this,"Remarks Updated",Toast.LENGTH_LONG).show();
               }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Handle the "Up" button click event
                onBackPressed(); // Optionally, you can use this to mimic the back button behavior
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makePhoneCall() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+91"+consumer.getMobile()));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            if(consumer.getMobile()=="" || consumer.getMobile().length()!=10){
                Toast.makeText(ShowConsumerActivity.this,"Invalid Mobile Number",Toast.LENGTH_LONG).show();
            }
            else{
                startActivity(callIntent);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openWhatsAppWithMessage(String phoneNumber, String message) {
        try {
            if(consumer.getMobile()=="" || consumer.getMobile().length()!=10) {
                Toast.makeText(ShowConsumerActivity.this,"Invalid Mobile Number",Toast.LENGTH_LONG).show();
            }

            else{
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+91" + phoneNumber + "&text=" + URLEncoder.encode(message, "UTF-8"));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(intent,"Open Whatsapp"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, like if WhatsApp is not installed on the device
        }
    }

    private void load_banner(){
        if(adsHelper.canShowAd()){

            if(adsHelper.canBannerAdShow()){
                FrameLayout ll=findViewById(R.id.frame_banner);
                mAdView = new AdView(this);
                ll.addView(mAdView);
                AdSize adSize = getAdSize();
                // Set the adaptive ad size to the ad view.
                mAdView.setAdUnitId("ca-app-pub-4587274520235293/3385490558");
                mAdView.setAdSize(adSize);
                AdRequest adRequest = new AdRequest.Builder().build();

                mAdView.loadAd(adRequest);
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdImpression() {
                        adsHelper.incrBannerConter();
                        //Toast.makeText(BillPayDetails.this,"bill banner loaded",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAdOpened() {



                        adsHelper.incrClickConter();
                        if(adsHelper.isMaxClick()){

                            adsHelper.setNoAdTimeNormal();
                        }



                        //Toast.makeText(BillPayDetails.this,"Ad clicked "+adCounter+" times",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                    }
                });
            }
        }

    }

    private AdSize getAdSize() {
        //Determine the screen width to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        //you can also pass your selected width here in dp
        int adWidth = (int) (widthPixels / density);

        //return the optimal size depends on your orientation (landscape or portrait)
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}
