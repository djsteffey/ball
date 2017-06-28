package djs.game.ball.playing.object;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import djs.game.ball.CAssetManager;
import djs.game.ball.playing.CScreenPlaying;

public abstract class CObject {
    // constants
    public static final float BOX2D_POSITION_SCALE = 750.0f / 9.0f;

    // variables
    protected Body m_body;
    protected TextureRegion m_texture_region;
    protected Actor m_actor;
    protected boolean m_is_dead;
    protected CAssetManager m_asset_manager;

    // functions
    public CObject(CAssetManager am, TextureRegion tr, Vector2 initial_position, Vector2 size){
        this.m_asset_manager = am;
        this.m_body = null;
        this.m_texture_region = tr;
        this.m_actor = new Actor();
        this.m_actor.setSize(size.x, size.y);
        this.m_actor.setPosition(initial_position.x, initial_position.y);
        this.m_is_dead = false;
        this.m_actor.setRotation(0.0f);
    }

    public void update(float delta_time, CObjectManager om, CScreenPlaying screen_playing){
        // could use this to update some kind of animation maybe
        // not necessary to update positions/collisions or anything since
        // box2d is handling that
        if (this.m_body != null){
            Vector2 position = this.m_body.getWorldCenter().cpy().scl(BOX2D_POSITION_SCALE);
            this.m_actor.setPosition(position.x, position.y);
        }
    }

    public void draw(SpriteBatch sb, BitmapFont font){
        float x = this.m_actor.getX() - (this.m_actor.getWidth() / 2.0f);
        float y = this.m_actor.getY() - (this.m_actor.getHeight() / 2.0f);
        sb.draw(this.m_texture_region, x, y, 0.0f, 0.0f, this.m_actor.getWidth(), this.m_actor.getHeight(), 1.0f, 1.0f, this.m_actor.getRotation());
    }

    public void handle_collision(CObject other){

    }

    public boolean get_is_dead(){
        return this.m_is_dead;
    }

    public void on_destroy(CObjectManager om, CScreenPlaying screen_playing){

    }

    abstract void create_body(World world);

    public float get_position_x(){
        return this.m_actor.getX();
    }

    public Body get_body(){
        return this.m_body;
    }
}
