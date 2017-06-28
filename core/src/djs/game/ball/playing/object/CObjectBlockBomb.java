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

public class CObjectBlockBomb extends CObjectBlock {
    // constants


    // variables


    // functions
    public CObjectBlockBomb(CAssetManager am, TextureRegion tr, Vector2 initial_position, Vector2 size, int hits, int row, int column){
        super(am, tr, initial_position, size, hits, row, column);
        this.m_color = new Color(219.0f / 255.0f, 44.0f / 255.0f, 44.0f / 255.0f, 1.0f);
        this.m_text_color = Color.WHITE;
    }

    @Override
    public void on_destroy(CObjectManager om, CScreenPlaying screen_playing){
        super.on_destroy(om, screen_playing);

        // blow up neighboring blocks
        for (int row = this.get_row() - 1; row <= this.get_row() + 1; ++row){
            for (int col = this.get_column() - 1; col <= this.get_column() + 1; ++col){
                CObjectBlock block = om.get_block_at_location(row, col);
                if (block != null){
                    block.adjust_hits(-(block.get_hits()));
                }
            }
        }
    }
}
