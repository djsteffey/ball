package djs.game.ball.playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import djs.game.ball.CAssetManager;
import djs.game.ball.CGame;
import djs.game.ball.CUiShapeButton;
import djs.game.ball.CUiShapeTextButton;

public class CStageGameOver extends Stage {
    // enums
    public enum ESelection { NOT_SET, CONTINUE, RESTART, QUIT }

    // inner
    public interface IListener{
        void on_show_complete();
        void on_hide_complete(ESelection selection);
    }
    private class CUiContinueButton extends CUiShapeTextButton{
        // constants


        // variables
        private TextureRegion m_coin_texture_region;
        private float m_coin_x;
        private float m_coin_size;

        // functions
        public CUiContinueButton(float x, float y, float width, float height, boolean round_corner, IListener listener, BitmapFont font, String text, TextureRegion tr, CAssetManager am) {
            super(x, y, width, height, round_corner, listener, font, text, tr);
            this.m_coin_texture_region = am.get_texture_region(CGame.GRAPHICS_ID_BLOCK_COIN);
            this.m_coin_x = 0.0f;
            this.m_coin_size = this.getHeight() * 0.5f;
        }

        @Override
        public void draw(Batch batch, float parent_alpha){
            super.draw(batch, parent_alpha);

            batch.draw(this.m_coin_texture_region,
                    this.m_coin_x,
                    this.m_label.getY() + (this.m_label.getHeight() - this.m_coin_size) / 2.0f,
                    this.m_coin_size, this.m_coin_size);
        }

        public void calculate_coin_x(){
            float text_width = new GlyphLayout(this.m_font, this.m_label.getText()).width;
            this.m_coin_x = this.getX() + this.getWidth() - ((this.getWidth() - text_width) / 2.0f) - this.m_coin_size * 0.65f;
        }
    }

    // constants
    private static final int BUTTON_WIDTH = 650;
    private static final int BUTTON_HEIGHT = 150;
    private static final int BUTTON_SEPARATION = 50;
    private static final float ACTION_DURATION_SHORT = 0.25f;
    private static final float ACTION_DURATION_LONG = 0.5f;

    // variables
    private CScreenPlaying m_screen_playing;
    private IListener m_listener;
    private ESelection m_selection;
    private Texture m_background_texture;
    private Actor m_background_actor;
    private Label m_label_game_over;
    private CUiContinueButton m_button_continue;
    private CUiShapeTextButton m_button_restart;
    private CUiShapeTextButton m_button_quit;
    private int m_continue_cost;
    private CUiShapeButton m_cart_button;


    // functions
    public CStageGameOver(final CScreenPlaying screen_playing, Viewport viewport, IListener listener){
        super(viewport);

        this.m_screen_playing = screen_playing;

        // save listener
        this.m_listener = listener;

        // cost
        this.m_continue_cost = 0;

        // no selection yet
        this.m_selection = ESelection.NOT_SET;

        // load our assets
        this.load_assets(screen_playing);

        // background
        this.m_background_actor = new Actor(){
            @Override
            public void draw(Batch batch, float parent_alpha) {
                batch.setColor(this.getColor());
                batch.draw(CStageGameOver.this.m_background_texture, 0.0f, 0.0f);
                batch.setColor(Color.WHITE);
            }
        };
        this.m_background_actor.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        this.addActor(this.m_background_actor);

        // game over label
        this.m_label_game_over = new Label("Game Over", this.create_label_style(screen_playing));
        this.addActor(this.m_label_game_over);

        // setup our buttons
        this.m_button_continue = new CUiContinueButton(0.0f, 0.0f, BUTTON_WIDTH, BUTTON_HEIGHT, true,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        // hide game over
                        CStageGameOver.this.m_selection = ESelection.CONTINUE;
                        CStageGameOver.this.hide();
                    }
                },
                screen_playing.get_game().get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_LARGE), "Continue",
                null, screen_playing.get_game().get_asset_manager());
        this.m_button_continue.setColorStrategy(
                new Color(0x003F00FF),
                new Color(0x008800FF),
                new Color(0x3F3F3FFF));
        this.m_button_continue.setBorderColorStrategy(
                new Color(0x00FF00FF),
                new Color(0x00FF00FF),
                new Color(0x888888FF));
        this.addActor(this.m_button_continue);

        this.m_button_restart = new CUiShapeTextButton(0.0f, 0.0f, BUTTON_WIDTH, BUTTON_HEIGHT, true,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        // hide game over
                        CStageGameOver.this.m_selection = ESelection.RESTART;
                        CStageGameOver.this.hide();
                    }
                },
                screen_playing.get_game().get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_LARGE), "Restart",
                null);
        this.m_button_restart.setColorStrategy(
                new Color(0x003F00FF),
                new Color(0x008800FF),
                new Color(0x3F3F3FFF));
        this.m_button_restart.setBorderColorStrategy(
                new Color(0x00FF00FF),
                new Color(0x00FF00FF),
                new Color(0x888888FF));
        this.addActor(this.m_button_restart);

        this.m_button_quit = new CUiShapeTextButton(0.0f, 0.0f, BUTTON_WIDTH, BUTTON_HEIGHT, true,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        // hide game over
                        CStageGameOver.this.m_selection = ESelection.QUIT;
                        CStageGameOver.this.hide();
                    }
                },
                screen_playing.get_game().get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_LARGE), "Quit",
                null);
        this.m_button_quit.setColorStrategy(
                new Color(0x003F00FF),
                new Color(0x008800FF),
                new Color(0x3F3F3FFF));
        this.m_button_quit.setBorderColorStrategy(
                new Color(0x00FF00FF),
                new Color(0x00FF00FF),
                new Color(0x888888FF));
        this.addActor(this.m_button_quit);

        // cart button
        this.m_cart_button = new CUiShapeButton(0.0f, 0.0f, this.m_button_continue.getHeight() * 0.5f,
                this.m_button_continue.getHeight() * 0.5f, true,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        CStageGameOver.this.m_screen_playing.get_game().request_save_player();
                        CStageGameOver.this.m_screen_playing.get_game().request_show_purchases();
                    }
                },
                screen_playing.get_game().get_asset_manager().get_texture_region(CGame.GRAPHICS_ID_CART));
        this.m_cart_button.setColorStrategy(
                new Color(0x003F00FF),
                new Color(0x008800FF),
                new Color(0x3F3F3FFF));
        this.m_cart_button.setBorderColorStrategy(
                new Color(0x00FF00FF),
                new Color(0x00FF00FF),
                new Color(0x888888FF));
        this.addActor(this.m_cart_button);
    }

    @Override
    public void act(float delta_time){
        super.act(delta_time);
        this.m_cart_button.setX(750.0f - this.m_cart_button.getWidth() - CScreenPlaying.BORDER_WALL_SIZE);
        this.m_cart_button.setY(this.m_button_continue.getY() - this.m_cart_button.getHeight() * 0.40f);

        // see if continue should be disabled
        if (this.m_continue_cost <= this.m_screen_playing.get_game().get_player().get_current_points()){
            this.m_button_continue.set_disabled(false);
        }
        else{
            this.m_button_continue.set_disabled(true);
        }
    }

    public void show(int continue_cost){
        this.m_button_continue.reset();
        this.m_button_restart.reset();
        this.m_button_quit.reset();
        this.m_cart_button.reset();

        this.m_continue_cost = continue_cost;
        this.m_button_continue.setText("Continue - " + this.m_continue_cost + "  ");

        // see if continue should be disabled
        if (this.m_continue_cost <= this.m_screen_playing.get_game().get_player().get_current_points()){
            this.m_button_continue.set_disabled(false);
        }
        else{
            this.m_button_continue.set_disabled(true);
        }

        this.m_background_actor.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        this.m_background_actor.addAction(Actions.color(new Color(0.0f, 0.0f, 0.0f, 0.75f), ACTION_DURATION_SHORT));

        this.m_label_game_over.setPosition(750.0f, 1334.0f - 350.0f);
        this.m_label_game_over.addAction(Actions.moveTo((750.0f - this.m_label_game_over.getWidth()) / 2.0f, this.m_label_game_over.getY(), ACTION_DURATION_LONG));

        this.m_button_continue.setPosition((750.0f - BUTTON_WIDTH) / 2.0f, -(this.m_button_continue.getHeight()));
        this.m_button_continue.addAction(Actions.moveTo(this.m_button_continue.getX(), ((1334 - BUTTON_HEIGHT * 3 - BUTTON_SEPARATION * 2) / 2.0f) + BUTTON_HEIGHT * 2 + BUTTON_SEPARATION * 2, ACTION_DURATION_LONG));

        this.m_button_restart.setPosition((750.0f - BUTTON_WIDTH) / 2.0f, -(this.m_button_restart.getHeight()));
        this.m_button_restart.addAction(Actions.moveTo(this.m_button_restart.getX(), ((1334 - BUTTON_HEIGHT * 3 - BUTTON_SEPARATION * 2) / 2.0f) + BUTTON_HEIGHT * 1 + BUTTON_SEPARATION * 1, ACTION_DURATION_LONG));

        this.m_button_quit.setPosition((750.0f - BUTTON_WIDTH) / 2.0f, -(this.m_button_quit.getHeight()));
        this.m_button_quit.addAction(Actions.sequence(
                Actions.moveTo(this.m_button_quit.getX(), ((1334 - BUTTON_HEIGHT * 3 - BUTTON_SEPARATION * 2) / 2.0f) + BUTTON_HEIGHT * 0 + BUTTON_SEPARATION * 0, ACTION_DURATION_LONG),
                new Action(){
                    @Override
                    public boolean act(float delta_time){
                        // signal show complete
                        CStageGameOver.this.m_listener.on_show_complete();

                        // start input
                        Gdx.input.setInputProcessor(CStageGameOver.this);
                        return true;
                    }
                }));
        this.m_button_continue.calculate_coin_x();
    }

    private void hide(){
        // stop input
        Gdx.input.setInputProcessor(null);

        // animate elements off screen
        this.m_background_actor.addAction(Actions.sequence(
                Actions.color(new Color(0.0f, 0.0f, 0.0f, 0.00f), ACTION_DURATION_LONG),
                new Action() {
                    @Override
                    public boolean act(float delta) {
                        CStageGameOver.this.m_listener.on_hide_complete(CStageGameOver.this.m_selection);
                        return true;
                    }
                }));

        this.m_label_game_over.addAction(Actions.moveTo(750.0f, this.m_label_game_over.getY(), ACTION_DURATION_SHORT));

        this.m_button_continue.addAction(Actions.moveTo(this.m_button_continue.getX(), -(this.m_button_continue.getHeight()), ACTION_DURATION_SHORT));

        this.m_button_restart.addAction(Actions.moveTo(this.m_button_restart.getX(), -(this.m_button_restart.getHeight()), ACTION_DURATION_SHORT));

        this.m_button_quit.addAction(Actions.moveTo(this.m_button_quit.getX(), -(this.m_button_quit.getHeight()), ACTION_DURATION_SHORT));
    }

    // assets functions
    private void load_assets(CScreenPlaying screen_playing){
        Pixmap pm = new Pixmap(750, 1334, Pixmap.Format.RGBA8888);
        pm.setColor(1.0f, 1.0f, 1.0f, 1.00f);
        pm.fill();
        this.m_background_texture = new Texture(pm);
        pm.dispose();
    }
    private Label.LabelStyle create_label_style(CScreenPlaying screen_playing){
        BitmapFont font = screen_playing.get_game().get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_EXTRA_LARGE);

        return new Label.LabelStyle(font, Color.WHITE);
    }
    // end assets functions

    public int get_continue_cost(){
        return this.m_continue_cost;
    }
}
