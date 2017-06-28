package djs.game.ball;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import djs.game.ball.playing.CScreenPlaying;

public class CGame extends Game {
	// inner classes
	public interface IListener{
		void on_quit(CGame game);
		void save_player_request(CPlayer player);
		CPlayer load_player_request();
		void show_ad();
	}

	// enums
	private enum EState { SCREEN_TRANSITION, NORMAL }
	public enum EScreen { PLAYING, QUIT }

	// constants
	private static final float TRANSITION_DURATION = 1.0f;
	public static final String ID_DEFAULT_FONT_EXTRA_LARGE = "default-font-extra-large";
	public static final String ID_DEFAULT_FONT_LARGE = "default-font-large";
	public static final String ID_DEFAULT_FONT_MEDIUM = "default-font-medium";
	public static final String ID_DEFAULT_FONT_SMALL = "default-font-small";
	public static final String ID_DEFAULT_FONT_BLOCKS = "default-font-blocks";
	public static final String GRAPHICS_ID_BLOCK_RED = "block-red";
	public static final String GRAPHICS_ID_BLOCK_GREEN = "block-green";
	public static final String GRAPHICS_ID_BLOCK_BLUE = "block-blue";
	public static final String GRAPHICS_ID_BLOCK_COIN = "block-coin";
	public static final String GRAPHICS_ID_FAST_FORWARD = "fast-forward";
	public static final String GRAPHICS_ID_BALL_00 = "ball-00";
	public static final String GRAPHICS_ID_LAUNCHER = "launcher";
	public static final String GRAPHICS_ID_CART = "cart";
	public static final String SOUND_ID_BLOCK_EXPLODE = "sound-block-explode";
	public static final String SOUND_ID_COLLECT_COIN = "sound-collect-coin";
	public static final String SOUND_ID_GAME_OVER = "sound-game-over";
	public static final String SOUND_ID_LAUNCH_BALL = "sound-launch-ball";


	// variables
	private IListener m_listener;
	private EState m_state;
	private CPlayer m_player;
	private CAssetManager m_asset_manager;
	private Actor m_transition_actor;
	private ShapeRenderer m_transition_shape_renderer;
	private CScreenPlaying m_screen_playing;

	// functions
	public CGame(IListener listener){
		this.m_listener = listener;
		this.m_screen_playing = null;
	}

	@Override
	public void create () {
		// catch back button
		Gdx.input.setCatchBackKey(true);

		// state
		this.m_state = EState.NORMAL;

		// the one and only player
		this.m_player = this.m_listener.load_player_request();

		// assets
		this.m_asset_manager = new CAssetManager();
		this.load_assets();

		// create the transition stuff
		this.m_transition_actor = new Actor();
		this.m_transition_shape_renderer = new ShapeRenderer();

		// set our first screen
		this.set_next_screen(EScreen.PLAYING);
	}

	@Override
	public void dispose(){
		this.m_transition_shape_renderer.dispose();
		this.m_asset_manager.dispose();
	}

	@Override
	public void render(){
		super.render();



		// handle different states
		switch (this.m_state){
			case SCREEN_TRANSITION:
				// get elapsed time
				float delta_time = Gdx.graphics.getDeltaTime();
				this.m_transition_actor.act(delta_time);
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				this.m_transition_shape_renderer.setColor(this.m_transition_actor.getColor());
				this.m_transition_shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
				this.m_transition_shape_renderer.rect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				this.m_transition_shape_renderer.end();
				Gdx.gl.glDisable(GL20.GL_BLEND);
				break;
			case NORMAL:
				break;
		}
	}

	public void set_next_screen(final EScreen screen){
		// now in a transition state
		this.m_state = EState.SCREEN_TRANSITION;

		// the sequence of actions to execute
		SequenceAction actions = new SequenceAction();

		// create a fade out action if there is a current screen
		if (this.getScreen() != null) {
			this.m_transition_actor.setColor(Color.CLEAR);
			actions.addAction(Actions.color(Color.BLACK, TRANSITION_DURATION / 2.0f, Interpolation.fade));
		}
		else {
			this.m_transition_actor.setColor(Color.BLACK);
		}

		// set the next screen action
		switch (screen){
			case PLAYING:
				actions.addAction(new Action() {
					@Override
					public boolean act(float delta) {
						// set the screen
						if (CGame.this.m_screen_playing == null) {
							CGame.this.m_screen_playing = new CScreenPlaying(CGame.this);
						}
						else {
							CGame.this.m_screen_playing.restart();
						}
						CGame.this.setScreen(CGame.this.m_screen_playing);
						return true;
					}
				});
				break;
			case QUIT:
				// quit out of this libgdx "game" playing and go back to the main menu
				this.m_listener.on_quit(this);
				break;
		}

		// fade back in
		actions.addAction(Actions.color(Color.CLEAR, TRANSITION_DURATION / 2.0f, Interpolation.fade));

		// inform the new screen that their transition in is complete
		actions.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				((CScreen)CGame.this.getScreen()).transition_in_complete();
				CGame.this.m_state = EState.NORMAL;
				return true;
			}
		});

		// action setup complete so set it on the transition actor
		this.m_transition_actor.clearActions();
		this.m_transition_actor.addAction(actions);
	}

	public CPlayer get_player(){
		return this.m_player;
	}

	public CAssetManager get_asset_manager(){
		return this.m_asset_manager;
	}

	public void request_save_player(){
		this.m_listener.save_player_request(this.m_player);
	}

	public void request_load_player(){
		this.m_player = this.m_listener.load_player_request();
	}

	public void request_show_ad(){
		this.m_listener.show_ad();
	}

	private void load_assets(){
		// font
		this.m_asset_manager.load_ttf_font(CGame.ID_DEFAULT_FONT_EXTRA_LARGE, "droid_serif_bold.ttf", 120, Color.WHITE, 4.0f, Color.BLACK);
		this.m_asset_manager.load_ttf_font(CGame.ID_DEFAULT_FONT_LARGE, "droid_serif_bold.ttf", 72, Color.WHITE, 4.0f, Color.BLACK);
		this.m_asset_manager.load_ttf_font(CGame.ID_DEFAULT_FONT_MEDIUM, "droid_serif_bold.ttf", 36, Color.WHITE, 4.0f, Color.BLACK);
		this.m_asset_manager.load_ttf_font(CGame.ID_DEFAULT_FONT_SMALL, "droid_serif_bold.ttf", 32, Color.WHITE, 0.0f, Color.BLACK);
		this.m_asset_manager.load_ttf_font(CGame.ID_DEFAULT_FONT_BLOCKS, "droid_serif_bold.ttf", 32, Color.WHITE, 0.0f, Color.BLACK);

		// blocks
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_BLOCK_RED, "block_red_64x64.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_BLOCK_RED, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_BLOCK_RED)));
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_BLOCK_GREEN, "block_green_64x64.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_BLOCK_GREEN, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_BLOCK_GREEN)));
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_BLOCK_BLUE, "block_blue_64x64.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_BLOCK_BLUE, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_BLOCK_BLUE)));

		// balls
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_BALL_00, "balls/ball_00.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_BALL_00, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_BALL_00)));

		// misc
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_LAUNCHER, "balls/ball_00.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_LAUNCHER, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_LAUNCHER)));
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_BLOCK_COIN, "block_coin.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_BLOCK_COIN, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_BLOCK_COIN)));
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_FAST_FORWARD, "fast_forward.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_FAST_FORWARD, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_FAST_FORWARD)));
		this.m_asset_manager.load_texture(CGame.GRAPHICS_ID_CART, "cart.png");
		this.m_asset_manager.add_texture_region(CGame.GRAPHICS_ID_CART, new TextureRegion(this.m_asset_manager.get_texture(CGame.GRAPHICS_ID_CART)));

		// sounds
		this.m_asset_manager.load_sound(SOUND_ID_BLOCK_EXPLODE, "block_explode.mp3");
		this.m_asset_manager.load_sound(SOUND_ID_COLLECT_COIN, "collect_coin.mp3");
		this.m_asset_manager.load_sound(SOUND_ID_GAME_OVER, "game_over.mp3");
		this.m_asset_manager.load_sound(SOUND_ID_LAUNCH_BALL, "launch_ball.mp3");
	}
}
