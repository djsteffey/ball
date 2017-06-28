package djs.game.ball.playing.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import djs.game.ball.CAssetManager;
import djs.game.ball.playing.CScreenPlaying;

public class CObjectWall extends CObject {
    // enums
    public enum EWallLocation { TOP, BOTTOM, LEFT, RIGHT }

    // constants
    public static final int COLLISION_CATEGORY_BITS = 0x0002;
    private static final float FLASH_COOLDOWN = 3.0f / 60.0f;

    // variables
    private EWallLocation m_location;
    private float m_border_size;
    private boolean m_flash;
    private float m_current_flash_cooldown;

    // functions
    public CObjectWall(CAssetManager am, TextureRegion tr, EWallLocation location, float border_size){
        super(am, tr, new Vector2(0.0f, 0.0f), new Vector2(0.0f, 0.0f));
        this.m_location = location;
        this.m_border_size = border_size;
        this.m_flash = false;
        this.m_current_flash_cooldown = 0.0f;
    }

    @Override
    public void update(float delta_time, CObjectManager om, CScreenPlaying screen_playing){
        super.update(delta_time, om, screen_playing);

        if (this.m_flash){
            this.m_current_flash_cooldown += delta_time;
            if (this.m_current_flash_cooldown >= FLASH_COOLDOWN){
                this.m_flash = false;
            }
        }
    }

    @Override
    public void draw(SpriteBatch sb, BitmapFont font){
        /*
        if (this.m_flash) {
            sb.setColor(Color.BLUE);
        }else{
            sb.setColor(Color.CORAL);
        }*/
        sb.setColor(Color.PURPLE);
        super.draw(sb, font);
        sb.setColor(Color.WHITE);
    }

    public EWallLocation get_location(){
        return this.m_location;
    }

    @Override
    public void create_body(World world){
        // create the body definition
        BodyDef body_def = new BodyDef();
        // static, unmoving body
        body_def.type = BodyDef.BodyType.StaticBody;
        // position depends on location
        switch (this.m_location){
            case TOP:{
                this.m_actor.setPosition(750.0f / 2.0f, 1269.0f + this.m_border_size / 2.0f);
            } break;
            case BOTTOM:{
                this.m_actor.setPosition(750.0f / 2.0f, 174.0f + this.m_border_size / 2.0f);
            } break;
            case LEFT:{
                this.m_actor.setPosition(this.m_border_size / 2.0f, 189.0f + 1080.0f / 2.0f);
            } break;
            case RIGHT:{
                this.m_actor.setPosition(750.0f - this.m_border_size / 2.0f, 189.0f + 1080.0f / 2.0f);
            } break;
        }
        body_def.position.set(new Vector2(this.m_actor.getX(), this.m_actor.getY()).scl(1.0f / BOX2D_POSITION_SCALE));

        // create the body, this also adds it to the world
        Body body = world.createBody(body_def);

        // create a box shape for it
        PolygonShape box = new PolygonShape();
        switch (this.m_location){
            case TOP:{
                this.m_actor.setSize(750.0f, this.m_border_size);
            } break;
            case BOTTOM:{
                this.m_actor.setSize(750.0f, this.m_border_size);
            } break;
            case LEFT:{
                this.m_actor.setSize(this.m_border_size, 1080.0f);
            } break;
            case RIGHT:{
                this.m_actor.setSize(this.m_border_size, 1080.0f);
            } break;
        }
        box.setAsBox((this.m_actor.getWidth() / 2.0f) / BOX2D_POSITION_SCALE, (this.m_actor.getHeight() / 2.0f) / BOX2D_POSITION_SCALE);

        // create the fixture definition
        FixtureDef fixture_def = new FixtureDef();
        fixture_def.shape = box;
        fixture_def.density = 1.0f;
        fixture_def.friction = 0.0f;
        fixture_def.restitution = 0.0f;
        fixture_def.filter.categoryBits = CObjectWall.COLLISION_CATEGORY_BITS;
        fixture_def.filter.maskBits = CObjectBall.COLLISION_CATEGORY_BITS | CObjectParticle.COLLISION_CATEGORY_BITS;

        // put it on the body
        body.createFixture(fixture_def);

        // dont need the box anymore
        box.dispose();

        // set it as teh body user data
        body.setUserData(this);
        this.m_body = body;
    }

    public void set_flash(boolean flash){
        this.m_flash = flash;
        this.m_current_flash_cooldown = 0.0f;
    }
}
