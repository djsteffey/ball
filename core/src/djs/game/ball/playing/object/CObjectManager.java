package djs.game.ball.playing.object;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import java.util.HashSet;
import java.util.Set;
import djs.game.ball.CGame;
import djs.game.ball.playing.CScreenPlaying;

public class CObjectManager {
    // constants
    private static final Vector2 BOX2D_GRAVITY = new Vector2(0.0f, -5.0f);
    private static final float BOX2D_VELOCITY_THRESHOLD = 0.01f;
    private static final int BOX2D_STEP_VELOCITY_ITERATIONS = 6;
    private static final int BOX2D_STEP_POSITION_ITERATIONS = 2;

    // variables
    private World m_box2d_world;
    private Box2DDebugRenderer m_box2d_debug_renderer;
    private Set<CObject> m_objects;
    private Set<CObject> m_objects_to_remove;
    private Set<CObject> m_objects_to_add;
    private int m_num_active_balls;
    private float m_last_active_ball_x_position;
    private int m_num_active_particles;
    private BitmapFont m_bitmap_font;

    // functions
    public CObjectManager(CScreenPlaying screen_playing){
        // create object containers
        this.m_objects = new HashSet<CObject>();
        this.m_objects_to_add = new HashSet<CObject>();
        this.m_objects_to_remove = new HashSet<CObject>();

        // keep track of number of balls active and their destruct x
        // this is needed to place the launcher on the next turn
        this.m_num_active_balls = 0;
        this.m_last_active_ball_x_position = 0.0f;

        // particles
        this.m_num_active_particles = 0;

        // font
        this.m_bitmap_font = screen_playing.get_game().get_asset_manager().get_ttf_font(CGame.ID_DEFAULT_FONT_BLOCKS);

        // setup box2d
        this.m_box2d_world = new World(BOX2D_GRAVITY, true);
        this.m_box2d_debug_renderer = new Box2DDebugRenderer();
        World.setVelocityThreshold(BOX2D_VELOCITY_THRESHOLD);

        // setup contact listener
        this.m_box2d_world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                // get the block from the body...or die trying
                CObject oa = (CObject)(contact.getFixtureA().getBody().getUserData());
                CObject ob = (CObject)(contact.getFixtureB().getBody().getUserData());
                oa.handle_collision(ob);
                ob.handle_collision(oa);
            }
            @Override
            public void endContact(Contact contact) {

            }
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    public void dispose(){
        this.m_box2d_world.dispose();
        this.m_box2d_debug_renderer.dispose();
    }

    public void update(float delta_time, float speed_multiplier, CScreenPlaying screen_playing){
        // step the box2d world
        this.m_box2d_world.step(delta_time, (int)(BOX2D_STEP_VELOCITY_ITERATIONS * speed_multiplier), (int)(BOX2D_STEP_POSITION_ITERATIONS * speed_multiplier));

        // loop over all objects
        for (CObject o : this.m_objects){
            // update each object
            o.update(delta_time, this, screen_playing);

            // is the object now dead?
            if (o.get_is_dead()){
                // it is dead so add it to the remove list
                this.m_objects_to_remove.add(o);

                // let it know it is being destroyed
                o.on_destroy(this, screen_playing);

                // if it is a ball then decrement the active balls
                if (o instanceof CObjectBall){
                    this.m_num_active_balls -= 1;
                    this.m_last_active_ball_x_position = o.get_position_x();
                }
                else if (o instanceof CObjectParticle){
                    this.m_num_active_particles -= 1;
                }
            }
        }

        // destroy the box2d body of all objects in the remove set
        for (CObject o : this.m_objects_to_remove){
            this.m_box2d_world.destroyBody(o.get_body());
        }

        // remove the objects in the remove set
        this.m_objects.removeAll(this.m_objects_to_remove);

        // clear the remove set for next loop
        this.m_objects_to_remove.clear();

        // add in any that need to be added
        for (CObject o : this.m_objects_to_add){
            // create its body
            o.create_body(this.m_box2d_world);

            // if it is a ball then apply the impulse
            if (o instanceof CObjectBall){
                CObjectBall ball = (CObjectBall)o;
                ball.apply_impulse(ball.get_initial_impulse());
            }
            this.m_objects.add(o);
        }

        // clear the add set for next loop
        this.m_objects_to_add.clear();
    }

    public void draw(SpriteBatch sb){
        // draw all the objects
        for (CObject o : this.m_objects){
            o.draw(sb, this.m_bitmap_font);
        }

        // draw the box2d debugging
//        this.m_box2d_debug_renderer.render(this.m_box2d_world, sb.getProjectionMatrix());
    }

    public void add(CObject object){
        // add an object
        this.m_objects_to_add.add(object);

        // increment balls counter if it is a ball
        if (object instanceof CObjectBall){
            this.m_num_active_balls += 1;
        }
        else if (object instanceof CObjectParticle){
            this.m_num_active_particles += 1;
        }
    }

    public int get_num_active_balls(){
        return this.m_num_active_balls;
    }

    public float get_last_active_ball_position_x(){
        return this.m_last_active_ball_x_position;
    }

    public int get_num_active_particles(){
        return this.m_num_active_particles;
    }

    public void advance_all_blocks(){
        for (CObject o : this.m_objects){
            if (o instanceof CObjectBlock){
                CObjectBlock block = (CObjectBlock)o;
                block.advance_row();
            }
        }
    }

    public boolean get_is_blocks_on_row(int row){
        for (CObject o : this.m_objects){
            if (o instanceof CObjectBlock){
                CObjectBlock block = (CObjectBlock)o;
                if (block.get_row() == row){
                    return true;
                }
            }
        }
        return false;
    }

    public void clear_all(){
        for (CObject o : this.m_objects){
            this.m_box2d_world.destroyBody(o.get_body());
        }
        this.m_objects.clear();
        this.m_objects_to_remove.clear();
        this.m_objects_to_add.clear();
        this.m_num_active_balls = 0;
    }

    public CObjectBlock get_block_at_location(int row, int column){
        for (CObject o : this.m_objects){
            if (o instanceof CObjectBlock){
                CObjectBlock block = (CObjectBlock)o;
                if ((block.get_row() == row) && (block.get_column() == column)){
                    return block;
                }
            }
        }
        return null;
    }

    public void destroy_blocks_row(int row){
        for (CObject o : this.m_objects){
            if (o instanceof CObjectBlock){
                CObjectBlock block = (CObjectBlock)o;
                if (block.get_row() == row){
                    block.adjust_hits(-(block.get_hits()));
                }
            }
        }
    }
}
