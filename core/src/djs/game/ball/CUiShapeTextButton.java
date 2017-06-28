package djs.game.ball;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class CUiShapeTextButton extends CUiShapeButton {
    // constants


    // variables
    protected BitmapFont m_font;
    protected Label m_label;

    // functions
    public CUiShapeTextButton(float x, float y, float width, float height, boolean round_corner, IListener listener, BitmapFont font, String text, TextureRegion tr){
        super(x, y, width, height, round_corner, listener, tr);
        this.m_font = font;
        this.m_label = new Label(text, new Label.LabelStyle(font, Color.WHITE));
        this.m_label.setPosition(
                this.getX() + ((this.getWidth() - this.m_label.getWidth()) / 2.0f),
                this.getY() + ((this.getHeight() - this.m_label.getHeight()) / 2.0f));
    }

    @Override
    public void draw(Batch batch, float parent_alpha){
        super.draw(batch, parent_alpha);

        this.m_label.draw(batch, parent_alpha);
    }

    @Override
    public void setPosition(float x, float y){
        super.setPosition(x, y);
        if (this.m_label != null) {
            this.m_label.setPosition(
                    this.getX() + ((this.getWidth() - this.m_label.getWidth()) / 2.0f),
                    this.getY() + ((this.getHeight() - this.m_label.getHeight()) / 2.0f));
        }
    }
    @Override
    public void setPosition(float x, float y, int alignment){
        super.setPosition(x, y, alignment);
        if (this.m_label != null) {
            this.m_label.setPosition(
                    this.getX() + ((this.getWidth() - this.m_label.getWidth()) / 2.0f),
                    this.getY() + ((this.getHeight() - this.m_label.getHeight()) / 2.0f));
        }
    }
    @Override
    public void setX(float x){
        super.setX(x);
        if (this.m_label != null) {
            this.m_label.setX(this.getX() + ((this.getWidth() - this.m_label.getWidth()) / 2.0f));
        }
    }
    @Override
    public void setY(float y){
        super.setY(y);
        if (this.m_label != null) {
            this.m_label.setY(this.getY() + ((this.getHeight() - this.m_label.getHeight()) / 2.0f));
        }
    }
    @Override
    public void setSize(float width, float height){
        super.setSize(width, height);
        if (this.m_label != null) {
            this.m_label.setPosition(
                    this.getX() + ((this.getWidth() - this.m_label.getWidth()) / 2.0f),
                    this.getY() + ((this.getHeight() - this.m_label.getHeight()) / 2.0f));
        }
    }
    @Override
    public void setWidth(float width){
        super.setWidth(width);
        if (this.m_label != null) {
            this.m_label.setX(this.getX() + ((this.getWidth() - this.m_label.getWidth()) / 2.0f));
        }
    }
    @Override
    public void setHeight(float height){
        super.setHeight(height);
        if (this.m_label != null) {
            this.m_label.setY(this.getY() + ((this.getHeight() - this.m_label.getHeight()) / 2.0f));
        }

    }

    public void setText(String text){
        this.m_label.setAlignment(Align.center);
        this.m_label.setText(text);
    }

    public void setTextColor(Color color){
        this.m_label.setColor(color);
    }
}
