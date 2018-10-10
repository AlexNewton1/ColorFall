package com.softwareoverflow.colorfall.free_trial;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.softwareoverflow.colorfall.BuildConfig;
import com.softwareoverflow.colorfall.activities.GameActivity;

public class AdvertHandler {

    private static AdView gameBannerAd;

    private static final int AD_FREQUENCY = 3; //only show ads every x times
    private static int quitGameInterstitialNum = 0, endGameInterstitialNum = 0;


    public void setupGameBanner(final Context context) {
        gameBannerAd = new AdView(context);
        gameBannerAd.setAdSize(AdSize.SMART_BANNER);
        gameBannerAd.setAdUnitId(BuildConfig.game_banner_ad_id);
        gameBannerAd.loadAd(new AdRequest.Builder().build());
        gameBannerAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (!GameActivity.isGameRunning)
                    gameBannerAd.pause();
            }
        });
    }

    public AdView getGameBannerAd() {
        return gameBannerAd;
    }

    public InterstitialAd createEndGameInterstitialAd(Context context) {
        endGameInterstitialNum++;
        if(endGameInterstitialNum % AD_FREQUENCY == 0)
            return createInterstitial(context, BuildConfig.end_game_interstitial_ad);
        else
            return null;
    }

    public InterstitialAd createQuitGameInterstitialAd(Context context) {
        quitGameInterstitialNum++;
        if(quitGameInterstitialNum % AD_FREQUENCY == 0)
            return createInterstitial(context, BuildConfig.quit_game_interstitial_ad);
        else
            return null;
    }

    private InterstitialAd createInterstitial(Context context, String adUnitId) {
        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(adUnitId);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        return interstitialAd;
    }

}
