package djs.game.ball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.example.games.basegameutils.GameHelper;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ActivityMainMenu extends Activity {
    // constants
    private static final String TAG = ActivityMainMenu.class.toString();
    public static final String PLAYER_STORAGE_FILENAME = "player_storage";
    public static final String ADS_DISABLED_FILENAME = "ads_disabled";
    private static final int GOOGLE_API_REQUEST_CODE_LEADERBOARDS = 1;

    // variables
    private AdView m_ad_view;
    private GameHelper m_game_helper;
    private boolean m_waiting_to_show_leaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        // create the view
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_activity_main_menu);

        // ads
        this.m_ad_view = (AdView) (this.findViewById(R.id.main_menu_banner_ad));
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
            LinearLayout ll = (LinearLayout)(this.findViewById(R.id.main_menu_layout));
            ll.removeView(this.m_ad_view);
            this.m_ad_view = null;
        }

        // google api client
        this.m_waiting_to_show_leaderboard = false;
        this.m_game_helper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        this.m_game_helper.enableDebugLog(false);
        GameHelper.GameHelperListener game_helper_listener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
                Log.v(TAG, "GameHelper.onSignInFailed()");

                // this is a failed sign-in for the leaderboards
                // just re-enable all our buttons
                ActivityMainMenu.this.enable_all_buttons(true);
            }
            @Override
            public void onSignInSucceeded() {
                Log.v(TAG, "GameHelper.onSignInSucceeded()");

                // no matter when the sign in happens...on app startup OR clicking leaderboards
                // then push our stats
                ActivityMainMenu.this.push_stats_to_leaderboards();

                // this is a sign-in success for the leaderboards
                if (ActivityMainMenu.this.m_waiting_to_show_leaderboard == true) {
                    ActivityMainMenu.this.m_waiting_to_show_leaderboard = false;
                    ActivityMainMenu.this.show_leaderboards();
                }
            }
        };
        this.m_game_helper.setup(game_helper_listener);


        // start new plater
/*        CPlayer player = ActivityMainMenu.load_player(this);
        player.adjust_current_points(5000);
        ActivityMainMenu.save_player(this, player);*/
    }

    @Override
    public void onStart(){
        Log.v(TAG, "onStart()");

        // super
        super.onStart();

        // pass to game helper
        this.m_game_helper.onStart(this);
    }

    @Override
    public void onStop(){
        Log.v(TAG, "onStop()");

        // super
        super.onStop();

        // pass to game helper
        this.m_game_helper.onStop();
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

        // enable all the buttons
        this.enable_all_buttons(true);
        ((TextView)this.findViewById(R.id.main_menu_textview_title)).setText(this.getResources().getString(R.string.app_name));
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

        // effectively the same as quitting
        this.on_button_click_quit(null);
    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent data){
        Log.v(TAG, "onActivityResult()");

        // super
        super.onActivityResult(request_code, result_code, data);

        // pass to game helper
        if (result_code == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            this.m_game_helper.disconnect();
        }
        else{
            this.m_game_helper.onActivityResult(request_code, result_code, data);
        }
    }

    // button clicks starting other activities
    public void on_button_click_play(View v){
        // disable all buttons
        this.enable_all_buttons(false);

        // lets put up a loading view
        ((TextView)this.findViewById(R.id.main_menu_textview_title)).setText(this.getResources().getString(R.string.loading));

        // start playing activity
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityMainMenu.this, ActivityPlay.class);
                ActivityMainMenu.this.startActivity(intent);

                // setup the transition
                ActivityMainMenu.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }).start();
    }

    public void on_button_click_upgrades(View v){
        // start upgrades activity
        Intent intent = new Intent(this, ActivityUpgrades.class);
        this.startActivity(intent);

        // setup the transition
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void on_button_click_leaderboards(View v){
        // lets disable the other buttons while we do this work
        this.findViewById(R.id.main_menu_button_play).setEnabled(false);
        this.findViewById(R.id.main_menu_button_upgrades).setEnabled(false);
        this.findViewById(R.id.main_menu_button_leaderboards).setEnabled(false);
        this.findViewById(R.id.main_menu_button_credits).setEnabled(false);
        this.findViewById(R.id.main_menu_button_quit).setEnabled(false);

        // sign in google api if not already signed in
        if (this.m_game_helper.isSignedIn() == false){
            // not signed in so initiate a sign-in
            this.m_waiting_to_show_leaderboard = true;
            this.m_game_helper.beginUserInitiatedSignIn();
        }
        else{
            // already signed in
            this.push_stats_to_leaderboards();
            this.show_leaderboards();
        }
    }

    public void on_button_click_purchases(View v){
        // start upgrades activity
        Intent intent = new Intent(this, ActivityPurchase.class);
        this.startActivity(intent);

        // setup the transition
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void on_button_click_credits(View v){
        // start credits
        Intent intent = new Intent(this, ActivityCredits.class);
        this.startActivity(intent);

        // setup the transition
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void on_button_click_quit(View v){
        // quit
        this.finish();
    }

    // misc loading and setup
    private void push_stats_to_leaderboards(){
        // load our player and update their name
        CPlayer player = ActivityMainMenu.load_player(this);
        player.set_name(Games.Players.getCurrentPlayerId(this.m_game_helper.getApiClient()));
        ActivityMainMenu.save_player(this, player);

        // push their stats and info into the leaderboards
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_total_coins_earned), player.get_total_points_earned());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_total_blocks_destroyed), player.get_total_blocks_destroyed());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_total_balls_launched), player.get_total_balls_launched());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_total_levels_completed), player.get_total_levels_completed());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_total_number_games), player.get_total_games());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_coins_earned_single_game), player.get_highest_game_points_earned());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_blocks_destroyed_single_game), player.get_highest_game_blocks_destroyed());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_balls_launched_single_game), player.get_highest_game_balls_launched());
        Games.Leaderboards.submitScore(this.m_game_helper.getApiClient(), this.getString(R.string.leaderboard_levels_completed_single_game), player.get_highest_game_levels_completed());
    }

    private void show_leaderboards(){
        // now show the leaderboards
        this.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(
                this.m_game_helper.getApiClient()), GOOGLE_API_REQUEST_CODE_LEADERBOARDS);

        // re-enable the buttons
        this.enable_all_buttons(true);
    }

    private void enable_all_buttons(boolean enable){
        this.findViewById(R.id.main_menu_button_play).setEnabled(enable);
        this.findViewById(R.id.main_menu_button_upgrades).setEnabled(enable);
        this.findViewById(R.id.main_menu_button_leaderboards).setEnabled(enable);
        this.findViewById(R.id.main_menu_button_purchases).setEnabled(enable);
        this.findViewById(R.id.main_menu_button_credits).setEnabled(enable);
        this.findViewById(R.id.main_menu_button_quit).setEnabled(enable);
    }

    protected static CPlayer load_player(Activity activity){
        CPlayer player = null;
        try {
            FileInputStream fis = activity.openFileInput(ActivityMainMenu.PLAYER_STORAGE_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            player = (CPlayer) ois.readObject();
            ois.close();
            fis.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // first time?
        if (player == null){
            player = new CPlayer("djsteffey");
        }
        return player;
    }

    protected static void save_player(Activity activity, CPlayer player){
        try {
            FileOutputStream fos = activity.openFileOutput(ActivityMainMenu.PLAYER_STORAGE_FILENAME, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(player);
            oos.close();
            fos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    protected static boolean check_for_no_ads_file(Activity activity){
        return false;
        /*
        try {
            FileInputStream fis = activity.openFileInput(ActivityMainMenu.ADS_DISABLED_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            boolean value = ois.readBoolean();
            ois.close();
            fis.close();
            return value;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }*/
    }
}
