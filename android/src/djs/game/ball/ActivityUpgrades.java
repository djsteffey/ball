package djs.game.ball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class ActivityUpgrades extends Activity {
    // constants
    private static final String TAG = ActivityUpgrades.class.toString();

    // variables
    private AdView m_ad_view;
    private CPlayer m_player;

    // functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        // super
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_activity_upgrades);

        // ads
        this.m_ad_view = (AdView) (this.findViewById(R.id.shop_upgrade_ad_banner));
        if (ActivityMainMenu.check_for_no_ads_file(this) == false) {
            // ads are NOT disabled
            // so start it up
            AdRequest ad_reqeust = new AdRequest.Builder()
//                    .addTestDevice(this.getResources().getString(R.string.admob_test_device))
                    .build();
            this.m_ad_view.loadAd(ad_reqeust);
        }
        else {
            // ads are disabled to remove its view
            LinearLayout ll = (LinearLayout)(this.findViewById(R.id.shop_upgrade_layout));
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

        // load the player
        this.m_player = ActivityMainMenu.load_player(this);

        // load the levels
        this.load_levels();

        // load the costs
        this.load_costs();

        // load current information
        this.load_current_info();
    }

    @Override
    protected void onPause(){
        Log.v(TAG, "onPause()");

        // stop ads
        if (this.m_ad_view != null){
            this.m_ad_view.pause();
        }

        // save the player
        ActivityMainMenu.save_player(this, this.m_player);

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

    // buy more coins from google play
    public void on_button_click_shop_upgrade_cart(View view){
        Intent intent = new Intent(this, ActivityPurchase.class);
        this.startActivity(intent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // button clicks for purchasing upgrades
    public void on_button_click_shop_upgrade_balls_per_level_cost(View view){
        // get the upgrade
        CPlayer.CUpgrade upgrade = this.m_player.get_balls_per_level_upgrade();

        // first remove the points from our player
        this.m_player.adjust_current_points(-(upgrade.get_points_needed_for_upgrade()));

        // now add the level
        upgrade.set_level(upgrade.get_level() + 1);

        // recalc all costs which will update our ui
        this.load_levels();
        this.load_costs();
        this.load_current_info();
    }
    public void on_button_click_shop_upgrade_bomb_chance_cost(View view){
        // get the upgrade
        CPlayer.CUpgrade upgrade = this.m_player.get_bomb_chance_upgrade();

        // first remove the points from our player
        this.m_player.adjust_current_points(-(upgrade.get_points_needed_for_upgrade()));

        // now add the level
        upgrade.set_level(upgrade.get_level() + 1);

        // recalc all costs which will update our ui
        this.load_levels();
        this.load_costs();
        this.load_current_info();
    }
    public void on_button_click_shop_upgrade_points_chance_cost(View view){
        // get the upgrade
        CPlayer.CUpgrade upgrade = this.m_player.get_point_chance_upgrade();

        // first remove the points from our player
        this.m_player.adjust_current_points(-(upgrade.get_points_needed_for_upgrade()));

        // now add the level
        upgrade.set_level(upgrade.get_level() + 1);

        // recalc all costs which will update our ui
        this.load_levels();
        this.load_costs();
        this.load_current_info();
    }
    public void on_button_click_shop_upgrade_aimer_length_cost(View view){
        // get the upgrade
        CPlayer.CUpgrade upgrade = this.m_player.get_aimer_length_upgrade();

        // first remove the points from our player
        this.m_player.adjust_current_points(-(upgrade.get_points_needed_for_upgrade()));

        // now add the level
        upgrade.set_level(upgrade.get_level() + 1);

        // recalc all costs which will update our ui
        this.load_levels();
        this.load_costs();
        this.load_current_info();
    }

    // button clicks for help
    public void on_button_click_shop_upgrade_balls_per_level_help(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Balls Per Level Upgrade")
                .setMessage("Increases balls received per level" +
                        "\nBase:      0.50 balls per level" +
                        "\nMax:       1.00 balls per level" +
                        "\nCurrent:  " +
                        String.format(Locale.getDefault(), "%.3f balls per level", 0.50f + this.m_player.get_balls_per_level_upgrade().get_boost_percent()))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
        float val = 0.50f + this.m_player.get_balls_per_level_upgrade().get_boost_percent();
    }
    public void on_button_click_shop_upgrade_bomb_chance_help(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bomb Block Chance Upgrade")
                .setMessage("Increases chance of a Bomb Block" +
                        "\nBase:      4.0% Chance" +
                        "\nMax:       8.0% Chance" +
                        "\nCurrent:  " +
                        String.format(Locale.getDefault(), "%.3f%% Chance", (0.040f + this.m_player.get_bomb_chance_upgrade().get_boost_percent()) * 100))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
    public void on_button_click_shop_upgrade_points_chance_help(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Coin Block Chance Upgrade")
                .setMessage("Increases chance of a Coin Block" +
                        "\nBase:      4.0% Chance" +
                        "\nMax:       8.0% Chance" +
                        "\nCurrent:  " +
                        String.format(Locale.getDefault(), "%.3f%% Chance", (0.040f + this.m_player.get_point_chance_upgrade().get_boost_percent()) * 100))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
    public void on_button_click_shop_upgrade_aimer_length_help(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Launcher Aim Length Upgrade")
                .setMessage("Increases aim length of the Launcher" +
                        "\nBase:      300 units" +
                        "\nMax:       900 units" +
                        "\nCurrent:  " +
                        String.format(Locale.getDefault(), "%.3f units", (1.0f + this.m_player.get_aimer_length_upgrade().get_boost_percent()) * 300.0f))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    // misc loading and setup
    private void load_levels(){
        Resources res = this.getResources();

        int level = this.m_player.get_balls_per_level_upgrade().get_level();
        for (int i = 1; i <= level; ++i){
            int id = res.getIdentifier("shop_upgrade_balls_per_level_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFF00FF00);
        }
        for (int i = level + 1; i <= 10; ++i){
            int id = res.getIdentifier("shop_upgrade_balls_per_level_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFFFF0000);
        }

        level = this.m_player.get_bomb_chance_upgrade().get_level();
        for (int i = 1; i <= level; ++i){
            int id = res.getIdentifier("shop_upgrade_bomb_chance_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFF00FF00);
        }
        for (int i = level + 1; i <= 10; ++i){
            int id = res.getIdentifier("shop_upgrade_bomb_chance_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFFFF0000);
        }

        level = this.m_player.get_point_chance_upgrade().get_level();
        for (int i = 1; i <= level; ++i){
            int id = res.getIdentifier("shop_upgrade_points_chance_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFF00FF00);
        }
        for (int i = level + 1; i <= 10; ++i){
            int id = res.getIdentifier("shop_upgrade_points_chance_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFFFF0000);
        }

        level = this.m_player.get_aimer_length_upgrade().get_level();
        for (int i = 1; i <= level; ++i){
            int id = res.getIdentifier("shop_upgrade_aimer_length_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFF00FF00);
        }
        for (int i = level + 1; i <= 10; ++i){
            int id = res.getIdentifier("shop_upgrade_aimer_length_" + i, "id", this.getBaseContext().getPackageName());
            this.findViewById(id).setBackgroundColor(0xFFFF0000);
        }
    }
    private void load_costs(){
        Button buttons[] = {
                (Button)(this.findViewById(R.id.shop_upgrade_balls_per_level_cost)),
                (Button)(this.findViewById(R.id.shop_upgrade_bomb_chance_cost)),
                (Button)(this.findViewById(R.id.shop_upgrade_points_chance_cost)),
                (Button)(this.findViewById(R.id.shop_upgrade_aimer_length_cost))};
        CPlayer.CUpgrade upgrades[] = {
                this.m_player.get_balls_per_level_upgrade(),
                this.m_player.get_bomb_chance_upgrade(),
                this.m_player.get_point_chance_upgrade(),
                this.m_player.get_aimer_length_upgrade()};

        for (int i = 0; i < 4; ++i) {
            // get the button
            Button button = buttons[i];

            // get the upgrade
            CPlayer.CUpgrade upgrade = upgrades[i];

            // start the spinning coin animation
            ((AnimationDrawable) button.getCompoundDrawables()[2]).start();

            // check our level versus the max
            if (upgrade.get_level() < 10) {
                // below the max so we can upgrade; calculate the cost
                int cost = upgrade.get_points_needed_for_upgrade();

                // set that cost on the button
                button.setText(String.format(Locale.getDefault(), "%d", cost));

                // check if we can afford it
                if (cost <= this.m_player.get_current_points()) {
                    // we can so enable the button
                    button.setEnabled(true);
                } else {
                    // we cant so disable the button
                    button.setEnabled(false);
                }
            } else {
                // we are at the max upgrade level so cant purchase anymore
                button.setText(R.string.upgrades_cost_max_value);
                button.setEnabled(false);
            }
        }
    }
    private void load_current_info(){
        // put the player points
        ((TextView)this.findViewById(R.id.shop_upgrade_current_points_amount)).setText(String.format(Locale.getDefault(), "%d", this.m_player.get_current_points()));

        // spin the coin
        ImageView view = (ImageView) (this.findViewById(R.id.shop_upgrade_current_points_coin));
        ((AnimationDrawable)view.getDrawable()).start();
    }
}
