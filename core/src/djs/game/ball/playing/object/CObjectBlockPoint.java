package djs.game.ball.playing.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import djs.game.ball.CAssetManager;
import djs.game.ball.CGame;
import djs.game.ball.playing.CScreenPlaying;

public class CObjectBlockPoint extends CObjectBlock {
    // constants


    // variables


    // functions
    public CObjectBlockPoint(CAssetManager am, TextureRegion tr, Vector2 initial_position, Vector2 size, int hits, int row, int column){
        super(am, tr, initial_position, size, hits, row, column);
        this.m_color = new Color(10.0f / 255.0f, 101.0f / 255.0f, 184.0f / 255.0f, 1.0f);
        this.m_text_color = Color.BLACK;
    }

    @Override
    public void on_destroy(CObjectManager om, CScreenPlaying screen_playing){
        super.on_destroy(om, screen_playing);

        // give some points
        screen_playing.point_was_earned();
    }
}
