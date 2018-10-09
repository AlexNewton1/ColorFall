package com.softwareoverflow.colorfall.free_trial;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class UpgradeManager implements PurchasesUpdatedListener, BillingClientStateListener{

    private static UpgradeManager upgradeManager;

    private static BillingClient billingClient;
    private static final String UPGRADE_SKU = "colorfall_pro_upgrade";

    private static boolean hasUserUpgraded = false;

    public static void setup(Context context){
        if(upgradeManager == null){
           upgradeManager = new UpgradeManager(context.getApplicationContext());
        }
    }

    private UpgradeManager(Context context){
        billingClient = BillingClient.newBuilder(context).setListener(this).build();
        billingClient.startConnection(this);

        checkUserPurchases(context);
    }

        @Override
        public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {

            if (billingResponseCode == BillingClient.BillingResponse.OK) {
                List<String> skuList = new ArrayList<>();
                skuList.add(UPGRADE_SKU);
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                if (responseCode == BillingClient.BillingResponse.OK
                                        && skuDetailsList != null) {
                                    for (SkuDetails skuDetails : skuDetailsList) {
                                        Log.d("debug2", "querySkuDetailsAsync: " + skuDetails.toString());
                                        String sku = skuDetails.getSku();
                                        String price = skuDetails.getPrice();

                                        if(sku.equals(UPGRADE_SKU)){
                                            Log.d("debug2", "UPGRADE PRICE: " + price);
                                        }

                                    }
                                }
                            }
                        });
            }
        }

    @Override
    public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                if(purchase.getSku().equals(UPGRADE_SKU)){
                    hasUserUpgraded = true;
                    break;
                }
            }
        }
    }

    public static void upgrade(Context context){
         BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(UPGRADE_SKU)
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
         billingClient.launchBillingFlow( (Activity) context, flowParams);
    }


    public static void checkUserPurchases(Context context){
        if(billingClient == null){
            upgradeManager = new UpgradeManager(context);
            return;
        }

        Purchase.PurchasesResult purchasesResult =
                billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if(purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK){
            for(Purchase purchase : purchasesResult.getPurchasesList()){
                if(purchase.getSku().equals(UPGRADE_SKU)){
                    hasUserUpgraded = true;
                    break;
                }
            }
        }
    }

    public static boolean isFreeUser(){
        return !hasUserUpgraded;
    }
}
