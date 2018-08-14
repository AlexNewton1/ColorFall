package com.softwareoverflow.colorfall;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdvertHandler {

    private static AdView gameBannerAd;

    public void setupGameBanner(Context context){
        gameBannerAd = new AdView(context);
        gameBannerAd.setAdSize(AdSize.SMART_BANNER);
        gameBannerAd.setAdUnitId(BuildConfig.game_banner_ad_id);
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
        return createInterstitial(context, BuildConfig.end_game_interstitial_ad);
    }

    public InterstitialAd createQuitGameInterstitialAd(Context context){
        return createInterstitial(context, BuildConfig.quit_game_interstitial_ad);
    }

    private InterstitialAd createInterstitial(Context context, String adUnitId){
        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(adUnitId);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        return interstitialAd;
    }

}
