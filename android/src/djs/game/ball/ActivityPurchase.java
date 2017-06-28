package djs.game.ball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.util.ArrayList;
import java.util.List;

public class ActivityPurchase extends Activity implements PurchasesUpdatedListener, BillingClientStateListener{
    // constants
    private static final String TAG = ActivityPurchase.class.toString();
    private static final int IAB_REQUEST_CODE_REMOVE_ADS = 1;
    private static final int IAB_REQUEST_CODE_COINS_250 = 250;
    private static final int IAB_REQUEST_CODE_COINS_500 = 500;
    private static final int IAB_REQUEST_CODE_COINS_1000 = 1000;

    // variables
    private AdView m_ad_view;
    private BillingClient m_billing_client;

    // functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        // super
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_activity_purchase);

        // ads
        this.m_ad_view = (AdView) (this.findViewById(R.id.purchase_banner_ad));
        if (ActivityMainMenu.check_for_no_ads_file(this) == false) {
            // ads are NOT disabled
            // so start it up
            AdRequest ad_reqeust = new AdRequest.Builder()
                    .addTestDevice(this.getResources().getString(R.string.admob_test_device))
                    .build();
            this.m_ad_view.loadAd(ad_reqeust);
        }
        else {
            // ads are disabled so remove its view
            LinearLayout ll = (LinearLayout)(this.findViewById(R.id.purchase_layout));
            ll.removeView(this.m_ad_view);
            this.m_ad_view = null;

            // and disable that purchase button
            Button button = (Button)(this.findViewById(R.id.purchase_remove_ads));
            button.setText(this.getResources().getString(R.string.remove_ads_purchased_cost));
            button.setEnabled(false);
        }

        // disable all buttons until we get IAB going
        this.findViewById(R.id.purchase_remove_ads).setEnabled(false);
        this.findViewById(R.id.purchase_coins_250).setEnabled(false);
        this.findViewById(R.id.purchase_coins_500).setEnabled(false);
        this.findViewById(R.id.purchase_coins_1000).setEnabled(false);

        // start the billing client
        this.m_billing_client = new BillingClient.Builder(this).setListener(this).build();
        this.m_billing_client.startConnection(this);
    }

    @Override
    protected void onResume(){
        Log.v(TAG, "onResume()");

        // super
        super.onResume();

        // start ads
        if (this.m_ad_view != null){
            this.m_ad_view.resume();
        }

        CPlayer player = ActivityMainMenu.load_player(this);
        ((TextView)this.findViewById(R.id.purchase_current_coins)).setText(Long.toString(player.get_current_points()));
    }

    @Override
    protected void onPause(){
        Log.v(TAG, "onPause()");

        // stop ads
        if (this.m_ad_view != null){
            this.m_ad_view.pause();
        }

        // super
        super.onPause();
    }

    @Override
    public void onDestroy(){
        Log.v(TAG, "onDestroy()");

        // destroy ads
        if (this.m_ad_view != null){
            this.m_ad_view.destroy();
        }

        // super
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        Log.v(TAG, "onBackPressed()");

        // close this upgrades activity
        this.finish();

        // set up the transition
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void on_button_click_remove_ads(View view){
        Log.v(TAG, "on_button_click_remove_ads()");

        BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                .setSku(this.getResources().getString(R.string.iab_sku_remove_ads))
                .setType(BillingClient.SkuType.INAPP);
        int response_code = this.m_billing_client.launchBillingFlow(this, builder.build());
        if (response_code == BillingClient.BillingResponse.OK){
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): OK");
        }
        else{
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): Failed: " + response_code);
        }
    }

    public void on_button_click_coins_250(View view){
        Log.v(TAG, "on_button_click_coins_250()");

        BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                .setSku(this.getResources().getString(R.string.iab_sku_coins_250))
                .setType(BillingClient.SkuType.INAPP);
        int response_code = this.m_billing_client.launchBillingFlow(this, builder.build());
        if (response_code == BillingClient.BillingResponse.OK){
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): OK");
        }
        else{
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): Failed: " + response_code);
        }
    }

    public void on_button_click_coins_500(View view){
        Log.v(TAG, "on_button_click_coins_500()");

        BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                .setSku(this.getResources().getString(R.string.iab_sku_coins_500))
                .setType(BillingClient.SkuType.INAPP);
        int response_code = this.m_billing_client.launchBillingFlow(this, builder.build());
        if (response_code == BillingClient.BillingResponse.OK){
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): OK");
        }
        else{
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): Failed: " + response_code);
        }
    }

    public void on_button_click_coins_1000(View view){
        Log.v(TAG, "on_button_click_coins_1000()");

        BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                .setSku(this.getResources().getString(R.string.iab_sku_coins_1000))
                .setType(BillingClient.SkuType.INAPP);
        int response_code = this.m_billing_client.launchBillingFlow(this, builder.build());
        if (response_code == BillingClient.BillingResponse.OK){
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): OK");
        }
        else{
            Log.v(TAG, "on_button_click_remove_ads(): launchBillingFlow(): Failed: " + response_code);
        }
    }

    // PurchasesUpdatedListener
    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK){
            Log.v(TAG, "onPurchasesUpdated(): OK");
            if (purchases != null){
                for (Purchase purchase : purchases){
                    Log.v(TAG, "onPurchasesUpdated(): " + purchase.toString());
                    if (purchase.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_remove_ads))){
                        // write that ads are disabled
                        ActivityMainMenu.write_no_ads_file(ActivityPurchase.this, true);

                        // disable the button for it
                        ActivityPurchase.this.findViewById(R.id.purchase_remove_ads).setEnabled(false);
                        ((Button)ActivityPurchase.this.findViewById(R.id.purchase_remove_ads)).setText("Owned");
                    }
                    else if (purchase.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_coins_250))){
                        this.consume_coins(250, purchase.getPurchaseToken());
                    }
                    else if (purchase.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_coins_500))){
                        this.consume_coins(500, purchase.getPurchaseToken());
                    }
                    else if (purchase.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_coins_1000))){
                        this.consume_coins(1000, purchase.getPurchaseToken());
                    }
                }
            }
        }
        else if (responseCode == BillingClient.BillingResponse.USER_CANCELED){
            Log.v(TAG, "onPurchasesUpdated(): User Canceled");
        }
        else{
            Log.v(TAG, "onPurchasesUpdated(): Error");

            alert("Updating Purchases Error", "Unable to update purchases: " + responseCode);
        }
    }
    // PurchasesUpdatedListener end

    // BillingClientStateListener
    @Override
    public void onBillingSetupFinished(int resultCode) {
        if (resultCode == BillingClient.BillingResponse.OK){
            // connected and can make requests
            Log.v(TAG, "onBillingSetupFinished(): Success");

            // now query the 4 purchases to get their prices
            this.query_prices();

            // see if remove ads is purchased already
            this.check_for_remove_ads_purchased();
        }
        else{
            // connection failed
            Log.v(TAG, "onBillingSetupFinished(): Failed");

            this.alert("Google Play IAB", "Unable to connect to Google Play IAB");
        }
    }
    @Override
    public void onBillingServiceDisconnected() {
        Log.v(TAG, "onBillingServiceDisconnected()");
    }
    // end BillingClientStateListener

    private void consume_coins(final int quantity, String purchase_token){
        Log.v(TAG, "consume_coins()");

        this.m_billing_client.consumeAsync(purchase_token, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(String purchaseToken, int resultCode) {
                if (resultCode == BillingClient.BillingResponse.OK){
                    Log.v(TAG, "consume_coins():onConsumeResponse(): Success");

                    // give the player the coins
                    CPlayer player = ActivityMainMenu.load_player(ActivityPurchase.this);
                    player.adjust_current_points(quantity);
                    ActivityMainMenu.save_player(ActivityPurchase.this, player);

                    // update the current coins textview
                    ((TextView)ActivityPurchase.this.findViewById(R.id.purchase_current_coins))
                            .setText(Long.toString(player.get_current_points()) + " Current Coins");
                }
                else{
                    Log.v(TAG, "consume_coins():onConsumeResponse(): Failed");
                    ActivityPurchase.this.alert("Consume Coin Failed", "Unable to consume the coins");
                }
            }
        });
    }

    private void query_prices(){
        Log.v(TAG, "query_prices()");

        // create the list to request
        List<String> sku_list = new ArrayList<>();
        sku_list.add(this.getResources().getString(R.string.iab_sku_remove_ads));
        sku_list.add(this.getResources().getString(R.string.iab_sku_coins_250));
        sku_list.add(this.getResources().getString(R.string.iab_sku_coins_500));
        sku_list.add(this.getResources().getString(R.string.iab_sku_coins_1000));

        // do the request
        this.m_billing_client.querySkuDetailsAsync(BillingClient.SkuType.INAPP, sku_list,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(SkuDetails.SkuDetailsResult result) {
                        if (result.getResponseCode() == BillingClient.BillingResponse.OK){
                            Log.v(TAG, "query_prices(): Success");

                            // TODO Log.v each price
                            // go through the list, set the price, and enable the button to make that purchase
                            for (SkuDetails detail : result.getSkuDetailsList()){
                                if (detail.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_remove_ads))){
                                    if (ActivityMainMenu.check_for_no_ads_file(ActivityPurchase.this) == false){
                                        ((Button)ActivityPurchase.this.findViewById(R.id.purchase_remove_ads)).setText(detail.getPrice());
                                    }
                                    // for remove ads, we let the check_for_remove_ads determine if it was purchased or not and enable the button
                                    Log.v(TAG, "Got price for Remove Ads: " + detail.getPrice());
                                }
                                else if (detail.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_coins_250))){
                                    ((Button)ActivityPurchase.this.findViewById(R.id.purchase_coins_250)).setText(detail.getPrice());
                                    ActivityPurchase.this.findViewById(R.id.purchase_coins_250).setEnabled(true);

                                    Log.v(TAG, "Got price for 250 Coins: " + detail.getPrice());
                                }
                                else if (detail.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_coins_500))){
                                    ((Button)ActivityPurchase.this.findViewById(R.id.purchase_coins_500)).setText(detail.getPrice());
                                    ActivityPurchase.this.findViewById(R.id.purchase_coins_500).setEnabled(true);

                                    Log.v(TAG, "Got price for 500 Coins: " + detail.getPrice());
                                }
                                else if (detail.getSku().equals(ActivityPurchase.this.getResources().getString(R.string.iab_sku_coins_1000))){
                                    ((Button)ActivityPurchase.this.findViewById(R.id.purchase_coins_1000)).setText(detail.getPrice());
                                    ActivityPurchase.this.findViewById(R.id.purchase_coins_1000).setEnabled(true);

                                    Log.v(TAG, "Got price for 1000 Coins: " + detail.getPrice());
                                }
                            }
                        }
                        else{
                            Log.v(TAG, "query_prices(): Failed");

                            ActivityPurchase.this.alert("Google Play Query", "Unable to query Google Play for IAB List");
                        }
                    }
                });
    }

    private void check_for_remove_ads_purchased(){
        // perform a query to see if remove ads is purchased
        Purchase.PurchasesResult purchase_result = this.m_billing_client.queryPurchases(BillingClient.SkuType.INAPP);
        if (purchase_result.getResponseCode() == BillingClient.BillingResponse.OK){
            Log.v(TAG, "onBillingSetupFinished():queryPurchases(): Successful");

            for (Purchase purchase : purchase_result.getPurchasesList()){
                Log.v(TAG, "onBillingSetupFinished():queryPurchases():Purchase: " + purchase.toString());
                if (purchase.getSku().equals(this.getResources().getString(R.string.iab_sku_remove_ads))){
                    // remove ads has been purchased
                    ActivityMainMenu.write_no_ads_file(this, true);
                }
            }

            // now if ads are disabled then disable the purchase button for it
            if (ActivityMainMenu.check_for_no_ads_file(this) == true){
                // ads are disabled
                Log.v(TAG, "onBillingSetupFinished():queryPurchases():No Ads: True");

                this.findViewById(R.id.purchase_remove_ads).setEnabled(false);
                ((Button)ActivityPurchase.this.findViewById(R.id.purchase_remove_ads)).setText("Owned");
            }
            else{
                // ads are NOT disabled
                Log.v(TAG, "onBillingSetupFinished():queryPurchases():No Ads: False");

                this.findViewById(R.id.purchase_remove_ads).setEnabled(true);
            }
        }
        else{
            Log.v(TAG, "onBillingSetupFinished():queryPurchases(): Failed: " + purchase_result.getResponseCode());
        }
    }

    private void alert(String title, String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
}
