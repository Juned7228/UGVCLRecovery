package com.bytecodesolutions.ugvclrecovery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bytecodesolutions.ugvclrecovery.DatabaseHelper;
import com.bytecodesolutions.ugvclrecovery.R;
import com.bytecodesolutions.ugvclrecovery.adapter.ConsumerAdapter;
import com.bytecodesolutions.ugvclrecovery.model.Consumer;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv_con_list;
    List<Consumer> consumerList;
    ConsumerAdapter consumerAdapter;

    AppCompatAutoCompleteTextView ac_tarif,ac_order_by,ac_con_type;
    List<String> tariflist,orders,contypelist;
    ArrayAdapter adapter;
    DatabaseHelper databaseHelper;
    AdsHelper adsHelper;
    int clickCount=0;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper=new DatabaseHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adsHelper=new AdsHelper(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        if(Tools.isNetworkAvailable(this)){
            loadIntAD();
        }

        /*filterwindow=findViewById(R.id.filter_window);
        filterButton=findViewById(R.id.btn_filter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterwindow.setVisibility(View.VISIBLE);
            }
        });*/
       setTarifList();
       adapter=new ArrayAdapter(
               MainActivity.this,
               android.R.layout.simple_list_item_1,
               tariflist
       );
        ac_tarif=findViewById(R.id.ac_tarif);
        ac_tarif.setText("ALL");
        ac_tarif.setAdapter(adapter);
       setOrders();
        adapter=new ArrayAdapter(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                orders
        );
        ac_order_by=findViewById(R.id.ac_order_by);
        ac_order_by.setText("Amount  A->Z");
        ac_order_by.setAdapter(adapter);
        setConTypeList();
        adapter=new ArrayAdapter(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                contypelist
        );
        ac_con_type=findViewById(R.id.ac_con_type);
        ac_con_type.setText("ALL");
        ac_con_type.setAdapter(adapter);


        findViewById(R.id.btn_filter_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.ns_view).setVisibility(View.GONE);
                findViewById(R.id.ll_main).setVisibility(View.GONE);
                setConsumerList();
                consumerAdapter.setConsumerList(consumerList);

            }
        });
        findViewById(R.id.ll_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setConsumerList();
        rv_con_list=findViewById(R.id.rv_con_list);
        consumerAdapter=new ConsumerAdapter(consumerList);
        rv_con_list.setLayoutManager(new LinearLayoutManager(this));
        rv_con_list.setAdapter(consumerAdapter);
        consumerAdapter.setOnListItemClickListener(new ConsumerAdapter.OnListItemClickListener() {
            @Override
            public void onListItemClick(int id) {
                if(mInterstitialAd!=null){
                    showIntAd(id);
                }
                else{
                    Intent intent=new Intent(MainActivity.this, ShowConsumerActivity.class);
                    intent.putExtra("id",id);

                    MainActivity.this.startActivity(intent);
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.ns_view).getVisibility()==View.VISIBLE){
            findViewById(R.id.ns_view).setVisibility(View.GONE);
            findViewById(R.id.ll_main).setVisibility(View.GONE);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                findViewById(R.id.ns_view).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_main).setVisibility(View.VISIBLE);

                break;
            case android.R.id.home: // Handle the "Up" button click event
                super.onBackPressed(); // Optionally, you can use this to mimic the back button behavior
                break;
        }
        return true;
    }

    private void setTarifList() {
        tariflist=new ArrayList<>();
        tariflist.add("ALL");
        tariflist.add("A1");
        tariflist.add("A2");
        tariflist.add("A3");
        tariflist.add("A5");
        tariflist.add("RGPR");
        tariflist.add("RGPU");
        tariflist.add("NRGP");
        tariflist.add("GLP");
        tariflist.add("LTMD");
        tariflist.add("SL.PR");
        tariflist.add("SL.PU");
        tariflist.add("WW.GP");
        tariflist.add("WW.MU");
        tariflist.add("WW.PR");
        tariflist.add("TMP");
    }
    private void setOrders(){
        orders=new ArrayList<>();
        orders.add("Amount  A->Z");
        orders.add("Amount  Z->A");
        orders.add("Rootcode  A->Z");
        orders.add("Rootcode  Z->A");
    }
    private void setConTypeList(){
        contypelist=new ArrayList<>();
        contypelist.add("ALL");
        contypelist.add("NORMAL");
        contypelist.add("PDC");
        contypelist.add("NC");
    }

    public void setConsumerList(){
      //  System.out.println("amount from "+((EditText)findViewById(R.id.ed_amt_from)).getText().toString());
        DatabaseHelper dbhelper=new DatabaseHelper(MainActivity.this);
        consumerList=dbhelper.getAllItemsByFilter(
                ((EditText)findViewById(R.id.ed_conno)).getText().toString(),
                ((EditText)findViewById(R.id.ed_name)).getText().toString(),
                ((AppCompatAutoCompleteTextView)findViewById(R.id.ac_tarif)).getText().toString().replace("ALL",""),
                ((EditText)findViewById(R.id.ed_meter)).getText().toString(),
                ((EditText)findViewById(R.id.ed_amt_from)).getText().toString().length()==0? ((EditText)findViewById(R.id.ed_amt_from)).getText().toString().replace("","1"):((EditText)findViewById(R.id.ed_amt_from)).getText().toString(),
                ((EditText)findViewById(R.id.ed_amt_to)).getText().toString().length()==0? ((EditText)findViewById(R.id.ed_amt_to)).getText().toString().replace("","9999999"):((EditText)findViewById(R.id.ed_amt_to)).getText().toString(),
                ((AppCompatAutoCompleteTextView)findViewById(R.id.ac_con_type)).getText().toString().replace("ALL",""),
                ((AppCompatAutoCompleteTextView)findViewById(R.id.ac_order_by)).getText().toString()

        );
        String value=dbhelper.getAllItemsAbstractByFilter(
                ((EditText)findViewById(R.id.ed_conno)).getText().toString(),
                ((EditText)findViewById(R.id.ed_name)).getText().toString(),
                ((AppCompatAutoCompleteTextView)findViewById(R.id.ac_tarif)).getText().toString().replace("ALL",""),
                ((EditText)findViewById(R.id.ed_meter)).getText().toString(),
                ((EditText)findViewById(R.id.ed_amt_from)).getText().toString().length()==0? ((EditText)findViewById(R.id.ed_amt_from)).getText().toString().replace("","1"):((EditText)findViewById(R.id.ed_amt_from)).getText().toString(),
                ((EditText)findViewById(R.id.ed_amt_to)).getText().toString().length()==0? ((EditText)findViewById(R.id.ed_amt_to)).getText().toString().replace("","9999999"):((EditText)findViewById(R.id.ed_amt_to)).getText().toString(),
                ((AppCompatAutoCompleteTextView)findViewById(R.id.ac_con_type)).getText().toString().replace("ALL",""),
                ((AppCompatAutoCompleteTextView)findViewById(R.id.ac_order_by)).getText().toString()

        );
        ((TextView)findViewById(R.id.tv_abstract)).setText(value);
        //Toast.makeText(MainActivity.this,"Total itemss "+consumerList.size(),Toast.LENGTH_LONG).show();


    }

    private void getAndProcessFilter(){

    }

    private void loadIntAD(){
        if(adsHelper.canShowAd()){

            if(adsHelper.canIntAdShow()){
                if(adsHelper.isMaxClick()){
                    adsHelper.resetClickCounter();
                }
                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(this,"ca-app-pub-4587274520235293/2072408886", adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                mInterstitialAd = interstitialAd;

                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error

                                mInterstitialAd = null;
                            }
                        });
            }

        }

    }
    public void showIntAd(int id){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                    Intent intent=new Intent(MainActivity.this, ShowConsumerActivity.class);
                    intent.putExtra("id",id);

                    MainActivity.this.startActivity(intent);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                    Intent intent=new Intent(MainActivity.this, ShowConsumerActivity.class);
                    intent.putExtra("id",id);

                    MainActivity.this.startActivity(intent);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                }

                @Override
                public void onAdClicked() {
                    adsHelper.incrClickConter();

                    if(adsHelper.isMaxClick()){
                        adsHelper.setNoAdTimeNormal();
                    }

                }

                @Override
                public void onAdImpression() {
                    adsHelper.resetBannerCounter();
                }
            });

        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
}