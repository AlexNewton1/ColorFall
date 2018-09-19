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

    private static boolean hasUserUpgraded = true;
    private static boolean isConnected = false;
    //TODO - this will be the class where all upgrades and interaction with google services are handled

    private String upgradePrice;

    public static UpgradeManager setup(Context context){
        if(upgradeManager == null){
           new UpgradeManager(context.getApplicationContext());
        }

        return upgradeManager;
    }

    private UpgradeManager(Context context){
        billingClient = BillingClient.newBuilder(context).setListener(this).build();
        billingClient.startConnection(this);

        checkUserPurchases(context);
    }

        @Override
        public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
            isConnected = true;

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
                                            upgradePrice = price;
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
            isConnected = false;
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
        } else {
            //TODO -  Handle any other error codes.
        }
    }

    public static void upgrade(Context context){
         BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(UPGRADE_SKU)
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
         billingClient.launchBillingFlow( (Activity) context, flowParams);
    }


    //TODO -- call this method onResume of whichever activities activate purchase flows
    public static void checkUserPurchases(Context context){
        if(billingClient == null){
            upgradeManager = new UpgradeManager(context);
            return;
        }

        Purchase.PurchasesResult purchasesResult =
                billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if(purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK){
            for(Purchase purchase : purchasesResult.getPurchasesList()){
                Log.d("debug2", "Checking against purchase: " + purchase);
                if(purchase.getSku().equals(UPGRADE_SKU)){
                    Log.d("debug2", "Purchase recognized as UPGRADE_SKU!\nSignature: " + purchase.getSignature() + "\n" + purchase.getOriginalJson());
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
