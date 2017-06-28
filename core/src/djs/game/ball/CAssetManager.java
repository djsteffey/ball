package djs.game.ball;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

public class CAssetManager {
    // constants


    // variables
    private Map<String, BitmapFont> m_fonts;
    private Map<String, Texture> m_textures;
    private Map<String, TextureRegion> m_texture_regions;
    private Map<String, Sound> m_sounds;


    // functions
    public CAssetManager(){
        this.m_fonts = new HashMap<String, BitmapFont>();
        this.m_textures = new HashMap<String, Texture>();
        this.m_texture_regions = new HashMap<String, TextureRegion>();
        this.m_sounds = new HashMap<String, Sound>();
    }

    public void dispose(){
        for (Sound sound : this.m_sounds.values()){
            sound.dispose();
        }
        for (Texture texture : this.m_textures.values()){
            texture.dispose();
        }
        for (BitmapFont font : this.m_fonts.values()){
            font.dispose();
        }
    }

    public void load_ttf_font(String name, String filename, int size, Color color, float border_size, Color border_color){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filename));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        parameter.borderWidth = border_size;
        parameter.borderColor = border_color;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        this.m_fonts.put(name, font);
    }

    public BitmapFont get_ttf_font(String name){
        if (this.m_fonts.containsKey(name)) {
            return this.m_fonts.get(name);
        }
        return null;
    }

    public boolean contains_ttf_font(String name){
        return this.m_fonts.containsKey(name);
    }

    public void remove_ttf_font(String name){
        this.m_fonts.remove(name);
    }

    public void load_texture(String name, String filename){
        this.m_textures.put(name, new Texture(filename));
    }

    public void add_texture(String name, Texture texture){
        this.m_textures.put(name, texture);
    }

    public Texture get_texture(String name){
        if (this.m_textures.containsKey(name)) {
            return this.m_textures.get(name);
        }
        return null;
    }

    public boolean contains_texture(String name){
        return this.m_textures.containsKey(name);
    }

    public void remove_texture(String name){
        this.m_textures.remove(name);
    }

    public void add_texture_region(String name, TextureRegion tr){
        this.m_texture_regions.put(name, tr);
    }

    public TextureRegion get_texture_region(String name){
        if (this.m_texture_regions.containsKey(name)) {
            return this.m_texture_regions.get(name);
        }
        return null;
    }

    public boolean contains_texture_region(String name){
        return this.m_texture_regions.containsKey(name);
    }

    public void remove_texture_region(String name){
        this.m_texture_regions.remove(name);
    }

    public void load_sound(String name, String filename){
        this.m_sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(filename)));
    }

    public Sound get_sound(String name){
        if (this.m_sounds.containsKey(name)) {
            return this.m_sounds.get(name);
        }
        return null;
    }
}
