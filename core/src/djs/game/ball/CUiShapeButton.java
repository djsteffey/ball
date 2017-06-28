package djs.game.ball;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class CUiShapeButton extends Actor {
    // inner
    public interface IListener{
        void on_click(CUiShapeButton button);
    }

    // constants


    // variables
    private IListener m_listener;
    private TextureRegion m_texture_region;
    private Color m_texture_region_color;
    private ShapeRenderer m_shape_renderer;
    private float m_border_size;
    private boolean m_round_corners;
    private Color m_current_color;
    private Color m_current_border_color;
    private boolean m_disabled;
    private boolean m_disable_click;

    // colors
    private Color m_normal_color;
    private Color m_pressed_color;
    private Color m_disabled_color;
    // border colors
    private Color m_normal_border_color;
    private Color m_pressed_border_color;
    private Color m_disabled_border_color;

    // functions
    public CUiShapeButton(float x, float y, float width, float height, boolean round_corner, IListener listener, TextureRegion tr){
        this.m_listener = listener;
        this.m_texture_region = tr;
        this.m_texture_region_color = Color.WHITE;
        this.m_shape_renderer = new ShapeRenderer();
        this.m_border_size = 4.0f;
        this.m_round_corners = round_corner;

        this.setColorStrategy(Color.BLACK, Color.BLACK, Color.BLACK);
        this.setBorderColorStrategy(Color.GREEN, Color.RED, Color.DARK_GRAY);

        this.setPosition(x, y);
        this.setSize(width, height);
        this.m_current_color = this.m_normal_color;
        this.m_current_border_color = this.m_normal_border_color;

        this.m_disabled = false;
        this.m_disable_click = false;

        // add listener
        this.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                if (CUiShapeButton.this.m_disabled){
                    // dont do anything
                    return false;
                }
                if (CUiShapeButton.this.m_disable_click){
                    // dont do anything return false;
                    return false;
                }
                // change color to the pressed color
                CUiShapeButton.this.m_current_color = CUiShapeButton.this.m_pressed_color;
                CUiShapeButton.this.m_current_border_color = CUiShapeButton.this.m_pressed_border_color;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                if (CUiShapeButton.this.m_disabled){
                    // dont do anything
                    return;
                }
                if (CUiShapeButton.this.m_disable_click){
                    // dont do anything return false;
                    return;
                }
                // change to the normal color
                CUiShapeButton.this.m_current_color = CUiShapeButton.this.m_normal_color;
                CUiShapeButton.this.m_current_border_color = CUiShapeButton.this.m_normal_border_color;
                CUiShapeButton.this.m_listener.on_click(CUiShapeButton.this);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parent_alpha){
        // stop the batch
        batch.end();

        // start the drawing
        this.m_shape_renderer.setProjectionMatrix(batch.getProjectionMatrix());
        this.m_shape_renderer.setTransformMatrix(batch.getTransformMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        if (this.m_round_corners){
            float radius = this.getHeight() / 2.0f;
            this.m_shape_renderer.begin(ShapeRenderer.ShapeType.Filled);

            // draw the border area
            this.m_shape_renderer.setColor(this.m_current_border_color);
            this.m_shape_renderer.rect(this.getX() + radius, this.getY(), this.getWidth() - 2.0f * radius, this.getHeight());
            this.m_shape_renderer.arc(this.getX() + radius, this.getY() + radius, radius, 90.0f, 180.0f);
            this.m_shape_renderer.arc(this.getX() + this.getWidth() - radius, this.getY() + radius, radius, 270.0f, 180.0f);

            // draw the inner "background" area
            this.m_shape_renderer.setColor(this.m_current_color);
            this.m_shape_renderer.rect(this.getX() + radius, this.getY() + this.m_border_size, this.getWidth() - 2.0f * radius, this.getHeight() - 2.0f * this.m_border_size);
            this.m_shape_renderer.arc(this.getX() + radius, this.getY() + radius, radius - this.m_border_size, 90.0f, 180.0f);
            this.m_shape_renderer.arc(this.getX() + this.getWidth() - radius, this.getY() + radius, radius - this.m_border_size, 270.0f, 181.0f);

            this.m_shape_renderer.end();
        }
        else {
            this.m_shape_renderer.begin(ShapeRenderer.ShapeType.Filled);

            // draw the border area
            this.m_shape_renderer.setColor(this.m_current_border_color);
            this.m_shape_renderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

            // draw the inner "background" area
            this.m_shape_renderer.setColor(this.m_current_color);
            this.m_shape_renderer.rect(this.getX() + this.m_border_size, this.getY() + this.m_border_size, this.getWidth() - 2.0f * this.m_border_size, this.getHeight() - 2.0f * this.m_border_size);

            this.m_shape_renderer.end();
        }
        // stop drawing
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // restart the batch
        batch.begin();
        batch.setColor(this.m_texture_region_color);
        if (this.m_texture_region != null) {
            batch.draw(this.m_texture_region, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        batch.setColor(Color.WHITE);
    }

    public void setColorStrategy(Color normal, Color pressed, Color disabled){
        this.m_normal_color = normal;
        this.m_pressed_color = pressed;
        this.m_disabled_color = disabled;

        this.m_current_color = this.m_normal_color;
    }

    public void setBorderColorStrategy(Color normal, Color pressed, Color disabled){
        this.m_normal_border_color = normal;
        this.m_pressed_border_color = pressed;
        this.m_disabled_border_color = disabled;

        this.m_current_border_color = this.m_normal_border_color;
    }

    public void set_disabled(boolean disabled){
        this.m_disabled = disabled;
        if (disabled){
            this.setColor(this.m_disabled_color);
            this.m_current_border_color = this.m_disabled_border_color;
        }
        else{
            this.setColor(this.m_normal_color);
            this.m_current_border_color = this.m_normal_border_color;
        }
    }

    protected ShapeRenderer get_shape_renderer(){
        return this.m_shape_renderer;
    }

    protected Color get_current_border_color(){
        return this.m_current_border_color;
    }

    public void set_texture_region_color(Color color){
        this.m_texture_region_color = color;
    }

    public void set_disable_click(boolean disabled){
        this.m_disable_click = disabled;
    }

    public void set_border_size(float size){
        this.m_border_size = size;
    }

    public void reset(){
        this.m_current_color = this.m_normal_color;
        this.m_current_border_color = this.m_normal_border_color;
    }
}
