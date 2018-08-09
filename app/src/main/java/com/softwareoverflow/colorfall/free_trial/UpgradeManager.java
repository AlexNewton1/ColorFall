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

    private final BillingClient billingClient;
    private Context context;
    private String upgrade_sku = "colorfall_pro_upgrade";

    private static boolean hasUserUpgraded = false;
    private boolean isConnected = false;
    //TODO - this will be the class where all upgrades and interaction with google services are handled

    private String upgradePrice;

    public UpgradeManager(Context context){
        this.context = context;

        // create new Person
        billingClient = BillingClient.newBuilder(context).setListener(this).build();
        billingClient.startConnection(this);
    }

        @Override
        public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
            isConnected = true;

            if (billingResponseCode == BillingClient.BillingResponse.OK) {
                // The billing client is ready. You can query purchases here.

                //check if the user has made any purchases
                checkUserPurchases();


                List<String> skuList = new ArrayList<>();
                skuList.add(upgrade_sku);
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                if (responseCode == BillingClient.BillingResponse.OK
                                        && skuDetailsList != null) {
                                    for (SkuDetails skuDetails : skuDetailsList) {
                                        String sku = skuDetails.getSku();
                                        String price = skuDetails.getPrice();

                                        if(sku.equals(upgrade_sku)){
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
            Log.d("debug2", "DISCONNECTED!");
        }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                if(purchase.getSku().equals(upgrade_sku)){
                    hasUserUpgraded = true;
                    break;
                }
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // TODO - Handle an error caused by a user cancelling the purchase flow.
        } else {
            //TODO -  Handle any other error codes.
        }
    }

    public void upgrade(){
        //TODO -- show error if not connected

        if(isConnected){
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku("SKU_ID_GOES_HERE!") //TODO - get the sku id
                    .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                    .build();
            int responseCode = billingClient.launchBillingFlow( (Activity) context, flowParams);
        } else {
            Log.d("debug2", "NOT CONNECTED!");
        }
    }


    //TODO -- call this method onResume of whichever activities activate purcahse flows
    private void checkUserPurchases(){
        if(!isConnected){
            //TODO -- show error if not connected
            Log.d("debug2", "NOT CONNECTED!");
        }

        Purchase.PurchasesResult purchasesResult =
                billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if(purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK){
            for(Purchase purchase : purchasesResult.getPurchasesList()){
                if(purchase.getSku().equals(upgrade_sku)){
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
