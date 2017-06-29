package djs.game.ball;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ActivityPlay extends AndroidApplication {
    // constants
    private static final String TAG = ActivityPlay.class.toString();


    // variables
    private AdView m_ad_view;

    // functions
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create a layout
        final RelativeLayout layout = new RelativeLayout(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // hide virtual buttons
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        CGame game = new CGame(new CGame.IListener() {
            @Override
            public void on_quit(CGame game) {
                ActivityPlay.this.finish();
                ActivityPlay.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            @Override
            public void save_player_request(CPlayer player) {
                ActivityMainMenu.save_player(ActivityPlay.this, player);
            }
            @Override
            public CPlayer load_player_request() {
                return ActivityMainMenu.load_player(ActivityPlay.this);
            }
            @Override
            public void show_ad() {
                if (ActivityMainMenu.check_for_no_ads_file(ActivityPlay.this) == false) {
                    // ads are NOT disabled
                    // interstitial ad
                    final InterstitialAd ad = new InterstitialAd(ActivityPlay.this);
                    ad.setAdUnitId(ActivityPlay.this.getResources().getString(R.string.admob_interstitial_ad));
                    final AdRequest ad_reqeust = new AdRequest.Builder()
//                            .addTestDevice(ActivityPlay.this.getResources().getString(R.string.admob_test_device))
                            .build();
                    ad.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            ad.show();
                        }

                        @Override
                        public void onAdClosed() {
                            ActivityPlay.this.getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                        }
                    });
                    ActivityPlay.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad.loadAd(ad_reqeust);
                        }
                    });
                }
            }

            @Override
            public void show_purchases() {
                Intent intent = new Intent(ActivityPlay.this, ActivityPurchase.class);
                ActivityPlay.this.startActivity(intent);
                ActivityPlay.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View libgdx_view = ActivityPlay.this.initializeForView(game, config);
        layout.addView(libgdx_view);

        // create the banner ad view
        if (ActivityMainMenu.check_for_no_ads_file(this) == false) {
            // ads are NOT disabled
            // so start it up
            ActivityPlay.this.m_ad_view = new AdView(ActivityPlay.this);
            ActivityPlay.this.m_ad_view.setAdSize(AdSize.BANNER);
            ActivityPlay.this.m_ad_view.setAdUnitId(ActivityPlay.this.getResources().getString(R.string.admob_banner_ad));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            ActivityPlay.this.m_ad_view.setLayoutParams(params);
            ActivityPlay.this.m_ad_view.setBackgroundColor(Color.BLACK);
            layout.addView(ActivityPlay.this.m_ad_view);
        }
        else {
            // ads are disabled to remove its view
            this.m_ad_view = null;
        }

        // set the layout to this activity
        ActivityPlay.this.setContentView(layout);


        // start ads
        if (ActivityMainMenu.check_for_no_ads_file(this) == false) {
            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice(ActivityPlay.this.getResources().getString(R.string.admob_test_device))
                    .build();
            ActivityPlay.this.m_ad_view.loadAd(adRequest);
        }
    }

    @Override
    public void onDestroy(){
        if (this.m_ad_view != null){
            this.m_ad_view.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPause(){
        if (this.m_ad_view != null){
            this.m_ad_view.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

        if (this.m_ad_view != null){
            this.m_ad_view.resume();
        }

        // hide virtual buttons
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
