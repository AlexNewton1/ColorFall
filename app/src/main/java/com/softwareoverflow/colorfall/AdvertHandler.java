package com.softwareoverflow.colorfall;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdvertHandler {

    private static AdView gameBannerAd;

    public void setupGameBanner(Context context){
        Log.d("debug2", "Set up game banner");
        gameBannerAd = new AdView(context);
        gameBannerAd.setAdSize(AdSize.SMART_BANNER);
        gameBannerAd.setAdUnitId(context.getString(R.string.game_banner_ad_id));
        gameBannerAd.loadAd(new AdRequest.Builder().build());
        gameBannerAd.setAdListener( new AdListener(){
            @Override
            public void onAdLoaded() {
                gameBannerAd.pause();
            }
        });
    }

    public AdView getGameBannerAd(){
        return gameBannerAd;
    }

    public InterstitialAd createEndGameInterstitialAd(Context context){
        return createInterstitial(context, context.getString(R.string.end_game_interstitial_ad));
    }

    public InterstitialAd createQuitGameInterstitialAd(Context context){
        return createInterstitial(context, context.getString(R.string.quit_game_interstitial_ad));
    }

    private InterstitialAd createInterstitial(Context context, String adUnitId){
        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(adUnitId);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        return interstitialAd;
    }

}
