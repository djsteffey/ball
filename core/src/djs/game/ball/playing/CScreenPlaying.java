package djs.game.ball.playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.Random;
import djs.game.ball.CGame;
import djs.game.ball.CScreen;
import djs.game.ball.CUiShapeButton;
import djs.game.ball.CUiShapeTextButton;
import djs.game.ball.playing.object.CObjectBall;
import djs.game.ball.playing.object.CObjectBlock;
import djs.game.ball.playing.object.CObjectBlockBomb;
import djs.game.ball.playing.object.CObjectBlockPoint;
import djs.game.ball.playing.object.CObjectManager;
import djs.game.ball.playing.object.CObjectParticle;
import djs.game.ball.playing.object.CObjectWall;

// TODO ability to save and restore state because of interruption
public class CScreenPlaying extends CScreen implements InputProcessor {
    // enums
    public enum EState { LOADING, ANGLE_SELECTION_START, ANGLE_SELECTION, LAUNCHING_BALLS, WAITING_BALLS_FINISH,
        WAITING_LAUNCHER_MOVEMENT, GAME_OVER, ASK_QUIT, NOT_SET }

    // constants
    private static final String TAG = CScreenPlaying.class.toString();
    private static final int GAMES_PER_ADD = 3;
    public static final int LEVELS_PER_POINT = 10;
    private static final int BUTTON_SPEED_WIDTH = 120;
    private static final int BUTTON_SPEED_HEIGHT = 120;
    private static final int BALL_RADIUS = 14;
    private static final int PARTICLE_SIZE = 12;
    private static final int BLOCK_SIZE = 90;
    private static final int NUM_BLOCKS_HIGH = 12;
    private static final int NUM_BLOCKS_WIDE = 8;
    public static final float BORDER_WALL_SIZE = 15.0f;
    private static final float BLOCK_SPAWN_CONSISTENCY = 0.75f;
    private static final float BLOCK_SPAWN_CHANCE = 0.75f;
    private static final float BLOCK_SPAWN_BOMB_CHANCE_BASE = 0.040f;
    private static final float BLOCK_SPAWN_POINT_CHANCE_BASE = 0.040f;
    private static final float BALL_QUANTITY_OF_LEVEL_BASE = 0.50f;
    public static final String ID_OBJECT_VERTICAL_WALL = "screenplaying_object_vertical_wall";
    public static final String ID_OBJECT_HORIZONTAL_WALL = "screenplaying_object_horizontal_wall";
    public static final String ID_OBJECT_PARTICLE = "screenplaying_particle-normal";
    private static final float SOUND_LEVEL = 0.25f;

    // variables
    private EState m_state;
    private CGame m_game;
    private Random m_random;
    private Stage m_stage;
    private SpriteBatch m_sprite_batch;
    private CLauncher m_launcher;
    private CObjectManager m_object_manager;
    private InputMultiplexer m_input_multiplexor;
    private float m_speed_multiplier;
    private Label m_debug_label;
    private CStageGameOver m_stage_game_over;
    private CStageQuit m_stage_quit;
    private CUiShapeTextButton m_speed_button;
    private CUiShapeTextButton m_points_button;
    private Sound m_sound_block_explode;
    private Sound m_sound_collet_coin;
    private Sound m_sound_game_over;
    private Sound m_sound_launch_ball;



    // constructor
    public CScreenPlaying(CGame game){
        Gdx.app.log(TAG, "CScreenPlaying()");

        // state
        this.m_state = EState.LOADING;

        // save the game
        this.m_game = game;

        // rand
        this.m_random = new Random();

        // load all resources for the game
        this.load_assets();

        // sprite batch
        this.m_sprite_batch = new SpriteBatch();

        // stage
        this.m_stage = new Stage(new FitViewport(750, 1334));

        // create the style for the labels
        Label.LabelStyle label_style = this.create_label_style();

        // level label
        Label label = new Label("0", label_style);
        label.setAlignment(Align.center);
        label.setPosition((750.0f - label.getWidth()) / 2.0f, (174.0f - label.getHeight()) / 2.0f);
        label.setName("label_level");
        this.m_stage.addActor(label);

        // speed button
        this.m_speed_button = new CUiShapeTextButton(750.0f - BUTTON_SPEED_WIDTH - 10, 0.0f + 10.0f,
                BUTTON_SPEED_WIDTH, BUTTON_SPEED_HEIGHT, false,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        if (CScreenPlaying.this.m_speed_multiplier == 1.0f){
                            CScreenPlaying.this.m_speed_multiplier = 2.0f;
                            CScreenPlaying.this.m_speed_button.setText("2x");
                        }
                        else if (CScreenPlaying.this.m_speed_multiplier == 2.0f){
                            CScreenPlaying.this.m_speed_multiplier = 4.0f;
                            CScreenPlaying.this.m_speed_button.setText("4x");
                        }
                        else if (CScreenPlaying.this.m_speed_multiplier == 4.0f){
                            CScreenPlaying.this.m_speed_multiplier = 1.0f;
                            CScreenPlaying.this.m_speed_button.setText("1x");
                        }
                    }
                },
                this.m_game.get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_MEDIUM), "1x",
                this.m_game.get_asset_manager().get_texture_region(CGame.GRAPHICS_ID_FAST_FORWARD));
        this.m_speed_button.set_texture_region_color(Color.RED);
        this.m_speed_button.setColorStrategy(
                new Color(0x003F00FF),
                new Color(0x008800FF),
                new Color(0x3F3F3FFF));
        this.m_speed_button.setBorderColorStrategy(
                new Color(0x00FF00FF),
                new Color(0x00FF00FF),
                new Color(0x888888FF));
        this.m_stage.addActor(this.m_speed_button);

        // store button
        this.m_points_button = new CUiShapeTextButton(0.0f + 10.0f, 0.0f + 10.0f,
                BUTTON_SPEED_WIDTH, BUTTON_SPEED_HEIGHT, false,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        // TODO add in game boosts?
                    }
                }, this.m_game.get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_MEDIUM),
                Long.toString(this.m_game.get_player().get_current_points()),
                this.m_game.get_asset_manager().get_texture_region(CGame.GRAPHICS_ID_BLOCK_COIN));
        this.m_points_button.set_disable_click(true);
        this.m_points_button.set_border_size(0.0f);
        this.m_stage.addActor(this.m_points_button);

        // launcher
        this.m_launcher = new CLauncher(this.m_game.get_asset_manager(), this.m_game.get_player().get_aimer_length_upgrade().get_boost_percent());

        // objects
        this.m_object_manager = new CObjectManager(this);

        // setup input
        this.m_input_multiplexor = new InputMultiplexer();

        // normal speed
        this.m_speed_multiplier = 1.0f;

        // start it up
        this.restart();

        // debug label
        this.m_debug_label = new Label("Debug", this.create_debug_label_style());
        this.m_debug_label.setPosition(0.0f, 1334.0f - this.m_debug_label.getHeight());
        this.m_stage.addActor(this.m_debug_label);

        // setup the game over stage for when it comes to that
        this.m_stage_game_over = new CStageGameOver(this, new FitViewport(750, 1334), new CStageGameOver.IListener() {
            @Override
            public void on_show_complete() {
            }

            @Override
            public void on_hide_complete(CStageGameOver.ESelection selection) {
                switch (selection){
                    case NOT_SET:
                        break;
                    case CONTINUE:
                        // take away our points
                        CScreenPlaying.this.m_game.get_player().adjust_current_points(-(CScreenPlaying.this.m_stage_game_over.get_continue_cost()));
                        CScreenPlaying.this.m_points_button.setText(Long.toString(CScreenPlaying.this.m_game.get_player().get_current_points()));

                        // save the player
                        CScreenPlaying.this.m_game.request_save_player();

                        // destroy last three rows
                        for (int i = 0; i < 3; ++i){
                            CScreenPlaying.this.m_object_manager.destroy_blocks_row(NUM_BLOCKS_HIGH - 1 - i);
                        }

                        // give input back to us
                        Gdx.input.setInputProcessor(CScreenPlaying.this.m_input_multiplexor);

                        // go to selecting angle
                        CScreenPlaying.this.m_state = EState.ANGLE_SELECTION_START;
                        break;
                    case RESTART:
                        // playing another game
                        CScreenPlaying.this.m_game.get_player().adjust_total_games(1);

                        // check for highscores...etc...
                        CScreenPlaying.this.m_game.get_player().execute_game_over_calculations();

                        // save the player to local storage
                        CScreenPlaying.this.m_game.request_save_player();

                        // check if we have played the required games to show an ad
                        if ((CScreenPlaying.this.m_game.get_player().get_total_games() % GAMES_PER_ADD) == 0){
                            CScreenPlaying.this.m_game.request_show_ad();
                        }

                        // restart the game
                        CScreenPlaying.this.restart();

                        // give input back to us
                        Gdx.input.setInputProcessor(CScreenPlaying.this.m_input_multiplexor);

                        // go to selecting angle
                        CScreenPlaying.this.m_state = EState.ANGLE_SELECTION_START;

                        break;
                    case QUIT:
                        // take away input
                        Gdx.input.setInputProcessor(null);

                        // update stats (local, google play, facebook)
                        CScreenPlaying.this.m_game.get_player().execute_game_over_calculations();
                        CScreenPlaying.this.m_game.request_save_player();

                        // change the game screen
                        CScreenPlaying.this.m_game.set_next_screen(CGame.EScreen.QUIT);
                        break;
                }
            }
        });

        // setup the quit stage for when it comes to that
        this.m_stage_quit = new CStageQuit(this, new FitViewport(750, 1334), new CStageQuit.IListener() {
            @Override
            public void on_show_complete() {

            }

            @Override
            public void on_hide_complete(CStageQuit.ESelection selection) {
                switch (selection){
                    case NOT_SET:
                        break;
                    case YES:
                        // player has elected to quit the game
                        // take away input
                        Gdx.input.setInputProcessor(null);

                        // update stats (local, google play, facebook)
                        CScreenPlaying.this.m_game.get_player().execute_game_over_calculations();
                        CScreenPlaying.this.m_game.request_save_player();

                        // change the game screen
                        CScreenPlaying.this.m_game.set_next_screen(CGame.EScreen.QUIT);
                        break;
                    case NO:
                        // player does not wish to quit
                        // give input back to us
                        Gdx.input.setInputProcessor(CScreenPlaying.this.m_input_multiplexor);

                        // reset the state to what it was prior
                        CScreenPlaying.this.m_state = CScreenPlaying.this.m_stage_quit.get_screen_playing_previous_state();
                        break;
                }
            }
        });

        // get the sounds
        this.m_sound_block_explode = this.m_game.get_asset_manager().get_sound(CGame.SOUND_ID_BLOCK_EXPLODE);
        this.m_sound_collet_coin = this.m_game.get_asset_manager().get_sound(CGame.SOUND_ID_COLLECT_COIN);
        this.m_sound_game_over = this.m_game.get_asset_manager().get_sound(CGame.SOUND_ID_GAME_OVER);
        this.m_sound_launch_ball = this.m_game.get_asset_manager().get_sound(CGame.SOUND_ID_LAUNCH_BALL);

        // lets go
        this.m_state = EState.ANGLE_SELECTION_START;
    }

    // screen functions
    @Override
    public void dispose() {
        Gdx.app.log(TAG, "dispose()");

        this.m_object_manager.dispose();
        this.m_sprite_batch.dispose();
        this.m_stage.dispose();
    }
    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");

        Gdx.input.setInputProcessor(this.m_input_multiplexor);
    }
    @Override
    public void resume() {
        Gdx.app.log(TAG, "resume()");

        if (this.m_state == EState.GAME_OVER){
            Gdx.input.setInputProcessor(this.m_stage_game_over);
        }
        else {
            Gdx.input.setInputProcessor(this.m_input_multiplexor);
        }
    }
    @Override
    public void hide() {
        Gdx.app.log(TAG, "hide()");

        Gdx.input.setInputProcessor(null);
    }
    @Override
    public void pause() {
        Gdx.app.log(TAG, "pause()");

        Gdx.input.setInputProcessor(null);
    }
    @Override
    public void resize(int width, int height) {
        Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
    }
    @Override
    public void transition_in_complete() {
        Gdx.app.log(TAG, "transition_in_complete()");

        // we have now started a new game
        this.m_game.get_player().adjust_total_games(1);

        // lets take input
        this.m_input_multiplexor.addProcessor(this.m_stage);
        this.m_input_multiplexor.addProcessor(this);
    }
    @Override
    public void render(float delta) {
        // unmodified delta
        float unmodified_delta = delta;

        // adjust for speed
        delta *= this.m_speed_multiplier;

        // updates
        this.m_stage.act(delta);
        this.m_launcher.update(delta, this);
        this.m_object_manager.update(delta, this.m_speed_multiplier, this);
        switch (this.m_state){
            case LOADING:
                break;
            case ANGLE_SELECTION_START:
                int quantity = Math.max(1, (int)((this.m_game.get_player().get_current_game_levels_completed()) * (0.5f + this.m_game.get_player().get_balls_per_level_upgrade().get_boost_percent())));
                this.m_launcher.set_balls_label(quantity);
                break;
            case ANGLE_SELECTION:
                break;
            case LAUNCHING_BALLS:
                // see if we are done launching
                if (this.m_launcher.get_num_balls_to_launch() == 0){
                    // we are done launching...now we are waiting
                    this.m_state = EState.WAITING_BALLS_FINISH;
                }
                break;
            case WAITING_BALLS_FINISH:
                // see if the balls are all gone
                if (this.m_object_manager.get_num_active_balls() == 0){
                    // all balls are gone
                    // move the launcher to the last impact location
                    this.m_launcher.set_position(new Vector2(this.m_object_manager.get_last_active_ball_position_x(),
                            this.m_launcher.get_position().y));
                    // move the blocks down a row
                    this.m_object_manager.advance_all_blocks();
                    // create a new row at the top
                    this.create_row(1);
                    // check for game over
                    if (this.m_object_manager.get_is_blocks_on_row(NUM_BLOCKS_HIGH - 1)){
                        // some blocks made it to the bottom row
                        // so game is now over
                        this.m_state = EState.GAME_OVER;
                        this.m_stage_game_over.show((int)(Math.max(1, (this.m_game.get_player().get_current_game_levels_completed() / LEVELS_PER_POINT) * 2)));
                        Gdx.input.setInputProcessor(null);
                        this.m_sound_game_over.play(SOUND_LEVEL);
                    }
                    else{
                        // no block on bottom row
                        // we have now gained a level
                        this.level_was_completed();
                        // update the state
                        this.m_state = EState.WAITING_LAUNCHER_MOVEMENT;
                    }
                }
                break;
            case WAITING_LAUNCHER_MOVEMENT:
                // see if the launcher is done moving
                if (this.m_launcher.get_is_moving() == false){
                    // done moving....can now go back to selecting angle start
                    this.m_state = EState.ANGLE_SELECTION_START;
                }
                break;
            case GAME_OVER:
                this.m_stage_game_over.act(unmodified_delta);
                break;
            case ASK_QUIT:
                this.m_stage_quit.act(unmodified_delta);
                break;
            case NOT_SET:
                break;
        }
        this.m_debug_label.setText("Balls:" + this.m_object_manager.get_num_active_balls() +
            " Particles:" + this.m_object_manager.get_num_active_particles() +
            " State:" + this.m_state.toString());
        CScreenPlaying.this.m_points_button.setText("" + this.m_game.get_player().get_current_points());


        // draw the entire screen
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.m_sprite_batch.setProjectionMatrix(this.m_stage.getCamera().combined);
        this.m_sprite_batch.begin();
        this.m_object_manager.draw(this.m_sprite_batch);
        this.m_launcher.draw(this.m_sprite_batch);
        this.m_sprite_batch.end();
        this.m_stage.draw();
        if (this.m_state == EState.GAME_OVER){
            this.m_stage_game_over.draw();
        }
        else if (this.m_state == EState.ASK_QUIT){
            this.m_stage_quit.draw();
        }
    }
    // end screen functions

    // input processor functions
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK){
            // bring up a 'quit game' stage
            // but only if we are NOT in the game over loading states
            if ((this.m_state != EState.GAME_OVER) && (this.m_state != EState.LOADING)){
                this.m_stage_quit.show(this.m_state);
                Gdx.input.setInputProcessor(null);
                this.m_state = EState.ASK_QUIT;
            }
        }
        return true;
    }
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch(this.m_state){
            case LOADING:
                break;
            case ANGLE_SELECTION_START:
                this.m_state = EState.ANGLE_SELECTION;
                CScreenPlaying.this.m_launcher.touch_start(
                        CScreenPlaying.this.m_stage.getViewport().unproject(new Vector2(screenX, screenY)));
                break;
            case ANGLE_SELECTION:
                break;
            case LAUNCHING_BALLS:
                break;
            case WAITING_BALLS_FINISH:
                break;
            case WAITING_LAUNCHER_MOVEMENT:
                break;
            case GAME_OVER:
                break;
        }
        return true;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (this.m_state){
            case LOADING:
                break;
            case ANGLE_SELECTION_START:
                break;
            case ANGLE_SELECTION:
                this.m_state = EState.LAUNCHING_BALLS;
                CScreenPlaying.this.m_launcher.touch_end(
                        CScreenPlaying.this.m_stage.getViewport().unproject(new Vector2(screenX, screenY)));
                if (this.m_launcher.get_is_aiming_angle_valid()) {
                    // quantity to fire is the number levels complete divided by 2; minimum of 1
                    int quantity = Math.max(1, (int)((this.m_game.get_player().get_current_game_levels_completed()) * (BALL_QUANTITY_OF_LEVEL_BASE + this.m_game.get_player().get_balls_per_level_upgrade().get_boost_percent())));
                    this.m_launcher.set_num_balls_to_launch(quantity);
                }
                else {
                    this.m_state = EState.ANGLE_SELECTION_START;
                }
                break;
            case LAUNCHING_BALLS:
                break;
            case WAITING_BALLS_FINISH:
                break;
            case WAITING_LAUNCHER_MOVEMENT:
                break;
            case GAME_OVER:
                break;
        }
        return true;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        switch (this.m_state){
            case LOADING:
                break;
            case ANGLE_SELECTION_START:
                break;
            case ANGLE_SELECTION:
                CScreenPlaying.this.m_launcher.touch_update(
                        CScreenPlaying.this.m_stage.getViewport().unproject(new Vector2(screenX, screenY)));
                break;
            case LAUNCHING_BALLS:
                break;
            case WAITING_BALLS_FINISH:
                break;
            case WAITING_LAUNCHER_MOVEMENT:
                break;
            case GAME_OVER:
                break;
        }
        return true;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    // end input processor functions

    // getters
    public CGame get_game(){
        return this.m_game;
    }
    public Random get_random(){
        return this.m_random;
    }
    // end getters

    // assets functions
    private void load_assets(){
        // objects
        // vertical wall
        if (this.m_game.get_asset_manager().contains_texture_region(ID_OBJECT_VERTICAL_WALL) == false) {
            Pixmap pm = new Pixmap(15, 1080, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            this.m_game.get_asset_manager().add_texture(ID_OBJECT_VERTICAL_WALL, new Texture(pm));
            this.m_game.get_asset_manager().add_texture_region(ID_OBJECT_VERTICAL_WALL,
                    new TextureRegion(this.m_game.get_asset_manager().get_texture(ID_OBJECT_VERTICAL_WALL)));
            pm.dispose();
        }
        // horizontal wall
        if (this.m_game.get_asset_manager().contains_texture_region(ID_OBJECT_HORIZONTAL_WALL) == false) {
            Pixmap pm = new Pixmap(750, 15, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            this.m_game.get_asset_manager().add_texture(ID_OBJECT_HORIZONTAL_WALL, new Texture(pm));
            this.m_game.get_asset_manager().add_texture_region(ID_OBJECT_HORIZONTAL_WALL,
                    new TextureRegion(this.m_game.get_asset_manager().get_texture(ID_OBJECT_HORIZONTAL_WALL)));
            pm.dispose();
        }

        // particle
        if (this.m_game.get_asset_manager().contains_texture_region(ID_OBJECT_PARTICLE) == false) {
            Pixmap pm = new Pixmap(PARTICLE_SIZE, PARTICLE_SIZE, Pixmap.Format.RGBA8888);
            for (int i = 0; i < PARTICLE_SIZE / 4; ++i) {
                pm.setColor(1.0f, 1.0f, 1.0f, 1.0f - ((i / 2) * 0.1f));
                pm.drawRectangle(i, i, PARTICLE_SIZE - 1 - i * 2, PARTICLE_SIZE - 1 - i * 2);
            }
            this.m_game.get_asset_manager().add_texture(ID_OBJECT_PARTICLE, new Texture(pm));
            this.m_game.get_asset_manager().add_texture_region(ID_OBJECT_PARTICLE,
                    new TextureRegion(this.m_game.get_asset_manager().get_texture(ID_OBJECT_PARTICLE)));
            pm.dispose();
        }
    }
    private Label.LabelStyle create_label_style(){
        BitmapFont font = this.m_game.get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_EXTRA_LARGE);

        return new Label.LabelStyle(font, Color.WHITE);
    }
    private Label.LabelStyle create_debug_label_style(){
        BitmapFont font = this.m_game.get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_SMALL);

        return new Label.LabelStyle(font, Color.WHITE);
    }
    // end assets functions

    // stats tracking
    public void block_was_destroyed(){
        this.m_game.get_player().adjust_current_game_blocks_destroyed(1);
        this.m_game.get_player().adjust_total_blocks_destroyed(1);
        this.m_sound_block_explode.play(SOUND_LEVEL);
    }
    private void ball_was_launched(){
        this.m_game.get_player().adjust_current_game_balls_launched(1);
        this.m_game.get_player().adjust_total_balls_launched(1);
        this.m_sound_launch_ball.play(SOUND_LEVEL);
    }
    public void point_was_earned(){
        this.m_game.get_player().adjust_current_game_points_earned(1);
        this.m_game.get_player().adjust_current_points(1);
        this.m_game.get_player().adjust_total_points_earned(1);
        CScreenPlaying.this.m_points_button.setText("" + this.m_game.get_player().get_current_points());
        this.m_sound_collet_coin.play(SOUND_LEVEL);
    }
    private void level_was_completed(){
        this.m_game.get_player().adjust_current_game_levels_completed(1);
        this.m_game.get_player().adjust_total_levels_completed(1);
        Label ll = CScreenPlaying.this.m_stage.getRoot().findActor("label_level");
        ll.setText(Long.toString(this.m_game.get_player().get_current_game_levels_completed()));
        ll.setPosition((this.m_stage.getWidth() - ll.getWidth()) / 2.0f, ll.getY());

        // see if we need to add a point
        if (this.m_game.get_player().get_current_game_levels_completed() % LEVELS_PER_POINT == 0){
            this.point_was_earned();
        }
    }
    // end stats tracking

    // creation functions
    public void restart(){
        // clear all objects
        this.m_object_manager.clear_all();

        // create the walls
        this.create_walls();

        // create some blocks...2 rows to start
        for (int row = 1; row <= 2; ++row){
            this.create_row(row);
        }

        // put the launcher in the middle
        this.m_launcher.set_position(new Vector2(750.0f / 2.0f, this.m_launcher.get_position().y));

        // update the ui
        Label ll = CScreenPlaying.this.m_stage.getRoot().findActor("label_level");
        ll.setText(Long.toString(this.m_game.get_player().get_current_game_levels_completed()));
        ll.setPosition((this.m_stage.getWidth() - ll.getWidth()) / 2.0f, ll.getY());
    }
    private void create_walls(){
        this.m_object_manager.add(new CObjectWall(this.m_game.get_asset_manager(), this.m_game.get_asset_manager().get_texture_region(ID_OBJECT_VERTICAL_WALL), CObjectWall.EWallLocation.LEFT, BORDER_WALL_SIZE));
        this.m_object_manager.add(new CObjectWall(this.m_game.get_asset_manager(), this.m_game.get_asset_manager().get_texture_region(ID_OBJECT_VERTICAL_WALL), CObjectWall.EWallLocation.RIGHT, BORDER_WALL_SIZE));
        this.m_object_manager.add(new CObjectWall(this.m_game.get_asset_manager(), this.m_game.get_asset_manager().get_texture_region(ID_OBJECT_HORIZONTAL_WALL), CObjectWall.EWallLocation.TOP, BORDER_WALL_SIZE));
        this.m_object_manager.add(new CObjectWall(this.m_game.get_asset_manager(), this.m_game.get_asset_manager().get_texture_region(ID_OBJECT_HORIZONTAL_WALL), CObjectWall.EWallLocation.BOTTOM, BORDER_WALL_SIZE));
    }
    public void create_ball(Vector2 position, Vector2 direction, float impulse){
        // create a new ball
        this.m_object_manager.add(new CObjectBall(this.m_game.get_asset_manager(),
                this.m_game.get_asset_manager().get_texture_region(CGame.GRAPHICS_ID_BALL_00),
                position.cpy(), new Vector2(BALL_RADIUS * 2.0f, BALL_RADIUS * 2.0f), direction.scl(impulse)));
        this.ball_was_launched();
    }
    private void create_row(int row){
        // lets try to make some kind of consistency from one block
        // to the next instead of completely random
        // so choose the first one on 50/50
        boolean filled = (this.m_random.nextFloat() < BLOCK_SPAWN_CHANCE) ? true : false;
        // now loop through all the spots
        for (int col = 0; col < NUM_BLOCKS_WIDE; ++col){
            // make the first one if it is filled
            if (filled){
                this.create_block(row, col);
            }
            // now determine the next one
            if (this.m_random.nextFloat() > BLOCK_SPAWN_CONSISTENCY){
                // change
                filled = !filled;
            }
        }
    }
    private void create_block(int row, int column){
        // hit count
        int hit_count = (int)(this.get_game().get_player().get_current_game_levels_completed() + 1);
        // create a new block...which kind?
        if (this.m_random.nextFloat() <= (BLOCK_SPAWN_BOMB_CHANCE_BASE + this.m_game.get_player().get_bomb_chance_upgrade().get_boost_percent())){
            this.m_object_manager.add(new CObjectBlockBomb(this.m_game.get_asset_manager(),
                    this.m_game.get_asset_manager().get_texture_region(CGame.GRAPHICS_ID_BLOCK_RED),
                    new Vector2(15.0f + column * BLOCK_SIZE + BLOCK_SIZE / 2.0f, 189.0f + (11 - row) * BLOCK_SIZE + BLOCK_SIZE / 2.0f),
                    new Vector2(BLOCK_SIZE, BLOCK_SIZE), hit_count, row, column));
        }
        else if (this.m_random.nextFloat() <= (BLOCK_SPAWN_POINT_CHANCE_BASE + this.m_game.get_player().get_point_chance_upgrade().get_boost_percent())){
            this.m_object_manager.add(new CObjectBlockPoint(this.m_game.get_asset_manager(),
                    this.m_game.get_asset_manager().get_texture_region(CGame.GRAPHICS_ID_BLOCK_BLUE),
                    new Vector2(15.0f + column * BLOCK_SIZE + BLOCK_SIZE / 2.0f, 189.0f + (11 - row) * BLOCK_SIZE + BLOCK_SIZE / 2.0f),
                    new Vector2(BLOCK_SIZE, BLOCK_SIZE), hit_count, row, column));
        }
        else {
            this.m_object_manager.add(new CObjectBlock(this.m_game.get_asset_manager(),
                    this.m_game.get_asset_manager().get_texture_region(CGame.GRAPHICS_ID_BLOCK_GREEN),
                    new Vector2(15.0f + column * BLOCK_SIZE + BLOCK_SIZE / 2.0f, 189.0f + (11 - row) * BLOCK_SIZE + BLOCK_SIZE / 2.0f),
                    new Vector2(BLOCK_SIZE, BLOCK_SIZE), hit_count, row, column));
        }
    }
    public void create_particle(Vector2 position, Vector2 impulse, Color color, float spin_rate){
        this.m_object_manager.add(new CObjectParticle(this.m_game.get_asset_manager(),
                this.m_game.get_asset_manager().get_texture_region(ID_OBJECT_PARTICLE),
                position, new Vector2(PARTICLE_SIZE, PARTICLE_SIZE), impulse, color, spin_rate));
    }
    // end creation functions
}
