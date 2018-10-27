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
    private static InterstitialAd endGameInterstitialAd, quitGameInterstitialAd;

    private static final int AD_FREQUENCY = 3; //only show ads every x times
    private static int quitGameInterstitialNum = 2, endGameInterstitialNum = 2;


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

    public InterstitialAd getEndGameInterstitialAd(Context context) {
        return endGameInterstitialAd;
    }

    public InterstitialAd getQuitGameInterstitialAd(Context context) {
        return quitGameInterstitialAd;
    }

    public void setupEndGameInterstitial(Context context) {
        endGameInterstitialNum++;
        if (endGameInterstitialNum % AD_FREQUENCY == 0)
            endGameInterstitialAd = createInterstitial(context, BuildConfig.end_game_interstitial_ad);
        else
            endGameInterstitialAd = null;
    }

    public void setupQuitGameInterstitial(Context context) {
        quitGameInterstitialNum++;
        if (quitGameInterstitialNum % AD_FREQUENCY == 0)
            quitGameInterstitialAd = createInterstitial(context, BuildConfig.quit_game_interstitial_ad);
        else
            quitGameInterstitialAd = null;
    }

    private InterstitialAd createInterstitial(Context context, String adUnitId) {
        InterstitialAd interstitialAd = new InterstitialAd(context.getApplicationContext());
        interstitialAd.setAdUnitId(adUnitId);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        return interstitialAd;
    }

}
