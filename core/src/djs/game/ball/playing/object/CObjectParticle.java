package djs.game.ball.playing.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import djs.game.ball.CAssetManager;
import djs.game.ball.playing.CScreenPlaying;

public class CObjectParticle extends CObject {
    // constants
    public static final int COLLISION_CATEGORY_BITS = 0x0008;

    // variables
    private Vector2 m_initial_impulse;
    private Color m_color;
    private float m_spin_rate;

    // functions
    public CObjectParticle(CAssetManager am, TextureRegion tr, Vector2 initial_position, Vector2 size, Vector2 initial_impulse, Color color, float spin_rate){
        super(am, tr, initial_position, size);
        this.m_initial_impulse = initial_impulse;
        this.m_color = color;
        this.m_spin_rate = spin_rate;
    }


    @Override public void update(float delta_time, CObjectManager om, CScreenPlaying screen_playing){
        super.update(delta_time, om, screen_playing);
        this.m_actor.setRotation(this.m_actor.getRotation() + delta_time * this.m_spin_rate);
        this.m_body.setTransform(this.m_actor.getX() / BOX2D_POSITION_SCALE, this.m_actor.getY() / BOX2D_POSITION_SCALE, this.m_actor.getRotation());
    }

    @Override
    public void draw(SpriteBatch sb, BitmapFont font){
        sb.setColor(this.m_color);
        super.draw(sb, font);
        sb.setColor(Color.WHITE);
    }

    @Override
    public void handle_collision(CObject other){
        if (other instanceof CObjectBall){
            // dont interact
        }
        else if (other instanceof CObjectBlock){
            // dont interact
        }
        else if (other instanceof CObjectWall){
            // just destroy the particle
            this.m_is_dead = true;
        }
        else if (other instanceof CObjectParticle){
            // do nothing
        }
    }

    @Override
    public void create_body(World world){
        // create body definition
        BodyDef body_def = new BodyDef();
        body_def.type = BodyDef.BodyType.DynamicBody;

        // compute location
        body_def.position.set(new Vector2(this.m_actor.getX(), this.m_actor.getY()).scl(1.0f / BOX2D_POSITION_SCALE));

        Body body = world.createBody(body_def);

        PolygonShape box = new PolygonShape();
        box.setAsBox(this.m_actor.getWidth() / 2.0f / BOX2D_POSITION_SCALE, this.m_actor.getHeight() / 2.0f / BOX2D_POSITION_SCALE);

        FixtureDef fixture_def = new FixtureDef();
        fixture_def.shape = box;
        fixture_def.density = 1.0f;
        fixture_def.friction = 0.0f;
        fixture_def.restitution = 0.0f;
        fixture_def.filter.categoryBits = CObjectParticle.COLLISION_CATEGORY_BITS;
        fixture_def.filter.maskBits = CObjectWall.COLLISION_CATEGORY_BITS;

        body.createFixture(fixture_def);

        box.dispose();

        body.applyLinearImpulse(this.m_initial_impulse, body.getWorldCenter(), true);

        body.setUserData(this);
        this.m_body = body;
    }
}
