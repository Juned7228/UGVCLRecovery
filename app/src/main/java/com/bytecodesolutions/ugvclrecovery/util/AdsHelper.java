package com.bytecodesolutions.ugvclrecovery.util;

import android.content.Context;

import com.bytecodesolutions.ugvclrecovery.constants.Ads;
import com.bytecodesolutions.ugvclrecovery.constants.Firebase;

import java.util.concurrent.TimeUnit;

public class AdsHelper {
    PrefManager adsPrefManager,fbPrefManager;

    public AdsHelper(Context context) {
        this.adsPrefManager = new PrefManager(context,Ads.ADS_PREF);
        fbPrefManager=new PrefManager(context, Firebase.PREF_FB);
    }
    public void setNoAdTimeNormal(){
        adsPrefManager.putLongValue(Ads.NO_AD_TIME,System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(fbPrefManager.getIntValue(Firebase.NO_AD_DURATION)));
    }
    public void setNoAdTimeFraud(){
        adsPrefManager.putLongValue(Ads.NO_AD_TIME,System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(fbPrefManager.getIntValue(Firebase.FRAUD_NO_AD_DURATION)));
    }

    public void incrBannerConter(){
        adsPrefManager.putIntValue(Ads.BANNER_COUNTER,adsPrefManager.getIntValue(Ads.BANNER_COUNTER)+1);

    }
    public void incrClickConter(){
        adsPrefManager.putIntValue(Ads.CLICK_COUNTER,adsPrefManager.getIntValue(Ads.CLICK_COUNTER)+1);
    }
    public void resetBannerCounter(){
        adsPrefManager.putIntValue(Ads.BANNER_COUNTER,0);

    }
    public void resetClickCounter(){
        adsPrefManager.putIntValue(Ads.CLICK_COUNTER,0);
    }
    public void setClickCounterMax(){
        adsPrefManager.putLongValue(Ads.CLICK_COUNTER,fbPrefManager.getIntValue(Firebase.MAX_CLICK));
    }
    public boolean canShowAd(){

        return System.currentTimeMillis()>adsPrefManager.getLongValue(Ads.NO_AD_TIME) && fbPrefManager.getIntValue(Firebase.AD_FLAG)==1;
    }
    public boolean canBannerAdShow(){
        return adsPrefManager.getIntValue(Ads.BANNER_COUNTER)<fbPrefManager.getIntValue(Firebase.BANNER_MAX);
    }
    public boolean canIntAdShow(){

        return adsPrefManager.getIntValue(Ads.BANNER_COUNTER)>=fbPrefManager.getIntValue(Firebase.BANNER_MAX);
    }
    public boolean isMaxClick(){

        return adsPrefManager.getIntValue(Ads.CLICK_COUNTER)>=fbPrefManager.getIntValue(Firebase.MAX_CLICK);
    }

    public void setFBUpdateTime(){
        adsPrefManager.putLongValue(Ads.FIREBASE_UPDATE_TIME,System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(fbPrefManager.getIntValue(Firebase.FB_UPDATE_DURATION)));
    }
    public boolean canUpdateFBData(){
        return System.currentTimeMillis()>adsPrefManager.getLongValue(Ads.FIREBASE_UPDATE_TIME);
    }


}
