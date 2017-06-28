package djs.game.ball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.util.Locale;

public class ActivityCredits extends Activity {
    // constants
    private static final String TAG = ActivityCredits.class.toString();

    // variables
    private AdView m_ad_view;

    // functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        // super
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_activity_credits);

        // ads
        this.m_ad_view = (AdView) (this.findViewById(R.id.credits_ad_banner));
        if (ActivityMainMenu.check_for_no_ads_file(this) == false) {
            // ads are NOT disabled
            // so start it up
            AdRequest ad_reqeust = new AdRequest.Builder()
                    .addTestDevice(this.getResources().getString(R.string.admob_test_device))
                    .build();
            this.m_ad_view.loadAd(ad_reqeust);
        }
        else {
            // ads are disabled to remove its view
            LinearLayout ll = (LinearLayout)(this.findViewById(R.id.credits_layout));
            ll.removeView(this.m_ad_view);
            this.m_ad_view = null;
        }
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
}
