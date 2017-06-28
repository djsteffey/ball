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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import djs.game.ball.CAssetManager;
import djs.game.ball.playing.CScreenPlaying;

public class CObjectBlock extends CObject {
    // constants
    public static final int COLLISION_CATEGORY_BITS = 0x0004;
    private static final int BLOCK_PARTICLES = 16;
    private static final float ACTION_MOVE_DURATION = 0.25f;

    // variables
    private int m_hits;
    private int m_row;
    private int m_column;
    protected Color m_color;
    protected Color m_text_color;

    // functions
    public CObjectBlock(CAssetManager am, TextureRegion tr, Vector2 initial_position, Vector2 size, int hits, int row, int column){
        super(am, tr, new Vector2(initial_position.x, 1334.0f), size);
        this.m_hits = hits;
        this.m_row = row;
        this.m_column = column;

        // action to move it in place
        this.m_actor.addAction(Actions.moveTo(initial_position.x, initial_position.y, ACTION_MOVE_DURATION));

        // color so we know for particles
        this.m_color = new Color(175.0f / 255.0f, 243.0f / 255.0f, 40.0f / 255.0f, 1.0f);
        this.m_text_color = Color.BLACK;
    }

    @Override
    public void update(float delta_time, CObjectManager om, CScreenPlaying screen_playing){
        // act the actor
        this.m_actor.act(delta_time);

        // update the body...a block gets its body positional data from the actor
        if (this.m_body != null){
            this.m_body.setTransform(this.m_actor.getX() / BOX2D_POSITION_SCALE,
                    this.m_actor.getY() / BOX2D_POSITION_SCALE, this.m_actor.getRotation());
        }
    }

    @Override
    public void draw(SpriteBatch sb, BitmapFont font){
        super.draw(sb, font);
        float x = (this.m_actor.getX() - (this.m_actor.getWidth() * 0.5f));
        float y = (this.m_actor.getY() + (0.50f * font.getCapHeight()));
        font.setColor(this.m_text_color);
        font.draw(sb, "" + this.m_hits, x, y, this.m_actor.getWidth(), Align.center, false);
    }

    public void adjust_hits(int amount){
        this.m_hits += amount;
        if (this.m_hits <= 0){
            this.m_is_dead = true;
        }
    }

    public int get_hits(){
        return this.m_hits;
    }

    public void advance_row(){
        this.m_row += 1;
        this.m_actor.addAction(Actions.moveBy(0.0f, -(this.m_actor.getHeight()), ACTION_MOVE_DURATION));
    }

    public int get_row(){
        return this.m_row;
    }

    public int get_column(){
        return this.m_column;
    }

    @Override
    public void create_body(World world){
        // create body definition
        BodyDef body_def = new BodyDef();
        body_def.type = BodyDef.BodyType.StaticBody;

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
        fixture_def.filter.categoryBits = CObjectBlock.COLLISION_CATEGORY_BITS;
        fixture_def.filter.maskBits = CObjectBall.COLLISION_CATEGORY_BITS;

        body.createFixture(fixture_def);

        box.dispose();

        body.setUserData(this);
        this.m_body = body;
    }

    @Override
    public void on_destroy(CObjectManager om, CScreenPlaying screen_playing){
        // and add some particles
        for (int i = 0; i < BLOCK_PARTICLES; ++i){
            Vector2 position = this.get_body().getWorldCenter().cpy().scl(BOX2D_POSITION_SCALE);
            Vector2 impulse = new Vector2();
            impulse.setToRandomDirection();
            impulse = impulse.scl(screen_playing.get_random().nextFloat() * 0.1f);
            screen_playing.create_particle(position, impulse, this.m_color,
                    (screen_playing.get_random().nextFloat() * 720.0f) - 360.0f);
        }

        // let the world know a block was destroyed
        screen_playing.block_was_destroyed();
    }
}
