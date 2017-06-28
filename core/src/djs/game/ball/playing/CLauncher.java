package djs.game.ball.playing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import djs.game.ball.CAssetManager;
import djs.game.ball.CGame;

public class CLauncher {
    // constants
    private static final int RADIUS = 15;
    private static final float LAUNCHER_MOVE_SPEED = 384.0f;
    private static final float BALL_IMPULSE = 4.0f;
    private static final float BALL_LAUNCH_COOLDOWN = 0.15f;
    private static final float BASE_AIMER_LENGTH = 300.0f;
    private static final float MIN_AIMING_ANGLE = 5.0f;
    private static final float MAX_AIMING_ANGLE = 175.0f;

    // variables
    private TextureRegion m_texture_region;
    private Actor m_actor;
    private boolean m_is_moving;
    private Vector2 m_touch_start_position;
    private Vector2 m_aiming_vector;
    private float m_ball_launch_cooldown_current;
    private int m_number_balls_to_launch;
    private ShapeRenderer m_shape_renderer;
    private float m_aimer_length_percent_increase;
    private Label m_number_balls_label;

    // functions
    public CLauncher(CAssetManager asset_manager, float aimer_length_percent_increase){
        // aimer
        this.m_aimer_length_percent_increase = aimer_length_percent_increase;

        // get our texture region
        this.m_texture_region = asset_manager.get_texture_region(CGame.GRAPHICS_ID_LAUNCHER);

        // not moving...yet
        this.m_is_moving = false;

        // actor stuff
        this.m_actor = new Actor();
        this.m_actor.setPosition(750.0f / 2.0f, 189.0f + RADIUS * 1.0f);
        this.m_actor.setColor(1.0f, 0.0f, 0.0f, 1.0f);
        this.m_actor.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                Actions.color(new Color(0.25f, 0.0f, 0.0f, 1.0f), 0.5f),
                Actions.color(new Color(1.0f, 0.0f, 0.0f, 1.0f), 0.5f))));

        // aiming
        this.m_touch_start_position = null;
        this.m_aiming_vector = null;

        // launching
        this.m_ball_launch_cooldown_current = 0.0f;
        this.m_number_balls_to_launch = 0;

        // drawing aimer
        this.m_shape_renderer = new ShapeRenderer();

        // label for balls
        this.m_number_balls_label = new Label("888", new Label.LabelStyle(asset_manager.get_ttf_font(CGame.ID_DEFAULT_FONT_SMALL), Color.WHITE));
        this.m_number_balls_label.setAlignment(Align.center);
    }

    public void update(float delta_time, CScreenPlaying screen_playing){
        this.m_actor.act(delta_time);
        this.m_number_balls_label.setPosition(
                this.m_actor.getX() - (this.m_number_balls_label.getWidth() * 0.5f),
                this.m_actor.getY() - (this.m_number_balls_label.getHeight() * 1.5f));

        // see if we have balls to launch
        if (this.m_number_balls_to_launch > 0){
            // we do so update the cooldown
            this.m_ball_launch_cooldown_current += delta_time;

            // see if the cooldown is up
            if (this.m_ball_launch_cooldown_current >= BALL_LAUNCH_COOLDOWN){
                // it is so launch
                screen_playing.create_ball(this.get_position(), this.m_aiming_vector.cpy(), BALL_IMPULSE);

                // decrement the number to launch
                this.m_number_balls_to_launch -= 1;

                // reset the cooldown
                this.m_ball_launch_cooldown_current -= BALL_LAUNCH_COOLDOWN;

                // update the balls label
                this.set_balls_label(this.m_number_balls_to_launch);
            }
        }
    }

    public void draw(SpriteBatch sb){
        if (this.m_touch_start_position != null){
            float angle = this.m_aiming_vector.angle();
            if ((angle >= MIN_AIMING_ANGLE) && (angle <= MAX_AIMING_ANGLE)) {
                // draw aiming vector
                sb.end();

                this.m_shape_renderer.setProjectionMatrix(sb.getProjectionMatrix());
                this.m_shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
                this.m_shape_renderer.setColor(1.0f, 1.0f, 0.0f, 1.0f);

                Vector2 end = this.get_position().add(this.m_aiming_vector.cpy().scl(BASE_AIMER_LENGTH * (1.0f + this.m_aimer_length_percent_increase)));
                this.m_shape_renderer.rectLine(this.get_position(), end, 4.0f);

                this.m_shape_renderer.end();

                sb.begin();
            }
        }
        sb.draw(this.m_texture_region, this.m_actor.getX() - RADIUS, this.m_actor.getY() - RADIUS, RADIUS * 2, RADIUS * 2);
        this.m_number_balls_label.draw(sb, 1.0f);
    }

    public void touch_start(Vector2 position){
        this.m_touch_start_position = position;
        this.m_aiming_vector = new Vector2(0.0f, 1.0f);
    }

    public void touch_update(Vector2 position){
        this.m_aiming_vector = this.m_touch_start_position.cpy().sub(position).nor();
    }

    public void touch_end(Vector2 position){
        this.m_aiming_vector = this.m_touch_start_position.cpy().sub(position).nor();
        this.m_touch_start_position = null;
    }

    public void set_position(Vector2 position){
        // mark as moving
        this.m_is_moving = true;

        // compute the duration based on the distance
        float duration = position.cpy().sub(this.m_actor.getX(), this.m_actor.getY()).len() / LAUNCHER_MOVE_SPEED;

        // setup an action to move there
        this.m_actor.addAction(Actions.sequence(
                Actions.moveTo(position.x, position.y, duration),
                new Action() {
                    @Override
                    public boolean act(float delta) {
                        // done moving
                        CLauncher.this.m_is_moving = false;
                        // action over
                        return true;
                    }
                }
        ));
    }

    public Vector2 get_position(){
        return new Vector2(this.m_actor.getX(), this.m_actor.getY());
    }

    public boolean get_is_moving(){
        return this.m_is_moving;
    }

    public int get_num_balls_to_launch(){
        return this.m_number_balls_to_launch;
    }

    public void set_num_balls_to_launch(int quantity){
        this.m_number_balls_to_launch = quantity;
        this.m_ball_launch_cooldown_current = 0.0f;
    }

    public boolean get_is_aiming_angle_valid(){
        float angle = this.m_aiming_vector.angle();
        if ((angle >= MIN_AIMING_ANGLE) && (angle <= MAX_AIMING_ANGLE)) {
            return true;
        }
        return false;
    }

    public void set_balls_label(int quantity){
        this.m_number_balls_label.setText(Integer.toString(quantity));
        this.m_number_balls_label.setPosition(
                this.m_actor.getX() - (this.m_number_balls_label.getWidth() * 0.5f),
                this.m_actor.getY() - (this.m_number_balls_label.getHeight() * 1.5f));
    }
}
