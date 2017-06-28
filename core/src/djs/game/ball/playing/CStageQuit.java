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

public class CStageQuit extends Stage {
    // enums
    public enum ESelection { NOT_SET, YES, NO }

    // inner
    public interface IListener{
        void on_show_complete();
        void on_hide_complete(ESelection selection);
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
    private Label m_label_quit;
    private CUiShapeTextButton m_button_yes;
    private CUiShapeTextButton m_button_no;
    private CScreenPlaying.EState m_screen_playing_previous_state;


    // functions
    public CStageQuit(final CScreenPlaying screen_playing, Viewport viewport, IListener listener){
        super(viewport);

        this.m_screen_playing = screen_playing;
        this.m_screen_playing_previous_state = CScreenPlaying.EState.NOT_SET;

        // save listener
        this.m_listener = listener;

        // no selection yet
        this.m_selection = ESelection.NOT_SET;

        // load our assets
        this.load_assets(screen_playing);

        // background
        this.m_background_actor = new Actor(){
            @Override
            public void draw(Batch batch, float parent_alpha) {
                batch.setColor(this.getColor());
                batch.draw(CStageQuit.this.m_background_texture, 0.0f, 0.0f);
                batch.setColor(Color.WHITE);
            }
        };
        this.m_background_actor.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        this.addActor(this.m_background_actor);

        // quit label
        this.m_label_quit = new Label("Quit?", this.create_label_style(screen_playing));
        this.addActor(this.m_label_quit);

        // setup our buttons
        this.m_button_yes = new CUiShapeTextButton(0.0f, 0.0f, BUTTON_WIDTH, BUTTON_HEIGHT, true,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        // hide game over
                        CStageQuit.this.m_selection = ESelection.YES;
                        CStageQuit.this.hide();
                    }
                },
                screen_playing.get_game().get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_LARGE), "Yes",
                null);
        this.m_button_yes.setColorStrategy(
                new Color(0x003F00FF),
                new Color(0x008800FF),
                new Color(0x3F3F3FFF));
        this.m_button_yes.setBorderColorStrategy(
                new Color(0x00FF00FF),
                new Color(0x00FF00FF),
                new Color(0x888888FF));
        this.addActor(this.m_button_yes);

        this.m_button_no = new CUiShapeTextButton(0.0f, 0.0f, BUTTON_WIDTH, BUTTON_HEIGHT, true,
                new CUiShapeButton.IListener() {
                    @Override
                    public void on_click(CUiShapeButton button) {
                        // hide game over
                        CStageQuit.this.m_selection = ESelection.NO;
                        CStageQuit.this.hide();
                    }
                },
                screen_playing.get_game().get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_LARGE), "No",
                null);
        this.m_button_no.setColorStrategy(
                new Color(0x003F00FF),
                new Color(0x008800FF),
                new Color(0x3F3F3FFF));
        this.m_button_no.setBorderColorStrategy(
                new Color(0x00FF00FF),
                new Color(0x00FF00FF),
                new Color(0x888888FF));
        this.addActor(this.m_button_no);
    }

    public void show(CScreenPlaying.EState screen_playing_previous_state){
        this.m_button_yes.reset();
        this.m_button_no.reset();

        this.m_screen_playing_previous_state = screen_playing_previous_state;

        this.m_background_actor.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        this.m_background_actor.addAction(Actions.color(new Color(0.0f, 0.0f, 0.0f, 0.75f), ACTION_DURATION_SHORT));

        this.m_label_quit.setPosition(750.0f, 1334.0f - 350.0f);
        this.m_label_quit.addAction(Actions.sequence(
                Actions.moveTo((750.0f - this.m_label_quit.getWidth()) / 2.0f, this.m_label_quit.getY(), ACTION_DURATION_LONG))
        );

        this.m_button_yes.setPosition((750.0f - BUTTON_WIDTH) / 2.0f, -(this.m_button_yes.getHeight()));
        this.m_button_yes.addAction(Actions.sequence(
                Actions.moveTo(this.m_button_yes.getX(), ((1334 - BUTTON_HEIGHT * 3 - BUTTON_SEPARATION * 2) / 2.0f) + BUTTON_HEIGHT * 2 + BUTTON_SEPARATION * 2, ACTION_DURATION_LONG))
        );

        this.m_button_no.setPosition((750.0f - BUTTON_WIDTH) / 2.0f, -(this.m_button_no.getHeight()));
        this.m_button_no.addAction(Actions.sequence(
                Actions.moveTo(this.m_button_no.getX(), ((1334 - BUTTON_HEIGHT * 3 - BUTTON_SEPARATION * 2) / 2.0f) + BUTTON_HEIGHT * 1 + BUTTON_SEPARATION * 1, ACTION_DURATION_LONG),
                new Action(){
                    @Override
                    public boolean act(float delta_time){
                        // signal show complete
                        CStageQuit.this.m_listener.on_show_complete();

                        // start input
                        Gdx.input.setInputProcessor(CStageQuit.this);
                        return true;
                    }
                }
        ));
    }

    public void hide(){
        // stop input
        Gdx.input.setInputProcessor(null);

        // animate elements off screen
        this.m_background_actor.addAction(Actions.sequence(
                Actions.color(new Color(0.0f, 0.0f, 0.0f, 0.00f), ACTION_DURATION_LONG),
                new Action() {
                    @Override
                    public boolean act(float delta) {
                        CStageQuit.this.m_listener.on_hide_complete(CStageQuit.this.m_selection);
                        return true;
                    }
                }));

        this.m_label_quit.addAction(Actions.moveTo(750.0f, this.m_label_quit.getY(), ACTION_DURATION_SHORT));

        this.m_button_yes.addAction(Actions.moveTo(this.m_button_yes.getX(), -(this.m_button_yes.getHeight()), ACTION_DURATION_SHORT));

        this.m_button_no.addAction(Actions.moveTo(this.m_button_no.getX(), -(this.m_button_no.getHeight()), ACTION_DURATION_SHORT));
    }

    public CScreenPlaying.EState get_screen_playing_previous_state(){
        return this.m_screen_playing_previous_state;
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
}
