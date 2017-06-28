package djs.game.ball.playing.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import djs.game.ball.CAssetManager;
import djs.game.ball.playing.CScreenPlaying;

public class CObjectBall extends CObject {
    // constants
    public static final int COLLISION_CATEGORY_BITS = 0x0001;
    private static final float COLLISION_TIMEOUT = 10.0f;

    // variables
    private Vector2 m_initial_impulse;
    private float m_time_since_last_block_hit;

    // functions
    public CObjectBall(CAssetManager am, TextureRegion tr, Vector2 initial_position, Vector2 size, Vector2 initial_impulse){
        super(am, tr, initial_position, size);
        this.m_initial_impulse = initial_impulse;
        this.m_time_since_last_block_hit = 0.0f;
    }

    @Override
    public void update(float delta_time, CObjectManager om, CScreenPlaying screen_playing){
        super.update(delta_time, om, screen_playing);
        this.m_time_since_last_block_hit += delta_time;
        if (this.m_time_since_last_block_hit >= COLLISION_TIMEOUT){
            // too long between hits...split in two
            Vector2 velocity = this.get_body().getLinearVelocity();
            Vector2 right = velocity.cpy().rotate90(1);
            Vector2 left = velocity.cpy().rotate90(-1);
            right = right.add(velocity);
            left = left.add(velocity);
            right = right.nor();
            left = left.nor();
            right = right.scl(this.m_initial_impulse.len());
            left = left.scl(this.m_initial_impulse.len());
            // now we have the right vectors for the velocities
            // just change it for the current ball
            this.get_body().setLinearVelocity(0.0f, 0.0f);
            this.get_body().applyLinearImpulse(right, this.get_body().getWorldCenter(), true);
            // and create a new ball for the left
            CObjectBall ball = new CObjectBall(this.m_asset_manager, this.m_texture_region,
                    new Vector2(this.m_actor.getX(), this.m_actor.getY()),
                    new Vector2(this.m_actor.getWidth(), this.m_actor.getHeight()), left);
            om.add(ball);
            // finally reset the hit cooldown
            this.m_time_since_last_block_hit -= COLLISION_TIMEOUT;
        }
    }

    @Override
    public void handle_collision(CObject other){
        if (other instanceof CObjectBall){
            // dont interact
        }
        else if (other instanceof CObjectBlock){
            // reset collision cooldown
            this.m_time_since_last_block_hit = 0.0f;
            // remove some hits from the block
            CObjectBlock block = (CObjectBlock)(other);
            block.adjust_hits(-1);
        }
        else if (other instanceof CObjectWall){
            // if its is the bottom wall then remove the ball
            CObjectWall wall = (CObjectWall)(other);
            if (wall.get_location() == CObjectWall.EWallLocation.BOTTOM){
                this.m_is_dead = true;
            }
            wall.set_flash(true);
        }
        else if (other instanceof CObjectParticle){
            // do nothing
        }
    }

    public void apply_impulse(Vector2 impulse){
        this.get_body().applyLinearImpulse(impulse, this.get_body().getWorldCenter(), true);
    }

    @Override
    public void create_body(World world){
        float radius = (this.m_actor.getWidth() / 2.0f) / BOX2D_POSITION_SCALE;

        // create the body definition as dynamic and set the position
        BodyDef body_def = new BodyDef();
        body_def.type = BodyDef.BodyType.DynamicBody;
        body_def.position.set(new Vector2(this.m_actor.getX(), this.m_actor.getY()).scl(1.0f / BOX2D_POSITION_SCALE));

        // create the body
        Body body = world.createBody(body_def);

        // create the balls shape as s circle or size radius
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);

        // create the single fixture that is the ball
        FixtureDef fixture_def = new FixtureDef();
        fixture_def.shape = circle;
        float area = (float)(2 * Math.PI * radius * radius);        // make sure mass is always 1.0f regardless of size
        fixture_def.density = 1.0f / area;
        fixture_def.friction = 0.0f;
        fixture_def.restitution = 1.0f;
        fixture_def.filter.categoryBits = CObjectBall.COLLISION_CATEGORY_BITS;
        fixture_def.filter.maskBits = CObjectWall.COLLISION_CATEGORY_BITS | CObjectBlock.COLLISION_CATEGORY_BITS;
        Fixture fixture = body.createFixture(fixture_def);

        // dont need the circle anyamore
        circle.dispose();

        // ball doesnt respond to gravity
        body.setGravityScale(0.0f);

        // set the body
        body.setUserData(this);
        this.m_body = body;
    }

    public Vector2 get_initial_impulse(){
        return this.m_initial_impulse;
    }
}
