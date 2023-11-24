package com.ray3k.particleparkpro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ray3k.particleparkpro.widgets.styles.Styles;

import static com.ray3k.particleparkpro.Core.preferences;
import static com.ray3k.particleparkpro.Core.skin;
import static com.ray3k.particleparkpro.Settings.DEFAULT_SCALE;
import static com.ray3k.particleparkpro.Settings.NAME_SCALE;

public class SkinLoader {
    public static void loadSkin() {
        var uiScale = Utils.valueToUIscale(preferences.getFloat(NAME_SCALE, DEFAULT_SCALE));

        String fontName = null;
        String headerName = null;
        String blackName = null;
        String boldName = null;
        float fontScale = 1;
        boolean fontUseIntegerPositions = true;


        switch (uiScale) {
            case SCALE_1X:
                fontName = "font";
                headerName = "header";
                blackName = "font-black";
                boldName = "bold";
                break;
            case SCALE_1_5X:
                fontName = "fontx1_5";
                headerName = "headerx1_5";
                blackName = "font-blackx1_5";
                boldName = "boldx1_5";
                fontScale = 1 / 1.5f;
                fontUseIntegerPositions = false;
                break;
            case SCALE_2X:
                fontName = "fontx2";
                headerName = "headerx2";
                blackName = "font-blackx2";
                boldName = "boldx2";
                fontScale = 1 / 2f;
                fontUseIntegerPositions = false;
                break;
            case SCALE_3X:
                fontName = "fontx3";
                headerName = "headerx3";
                blackName = "font-blackx3";
                boldName = "boldx3";
                fontScale = 1 / 3f;
                fontUseIntegerPositions = false;
                break;
            case SCALE_4X:
                fontName = "fontx4";
                headerName = "headerx4";
                blackName = "font-blackx4";
                boldName = "boldx4";
                fontScale = 1 / 4f;
                fontUseIntegerPositions = false;
                break;
        }

        skin = new Skin();
        var textureAtlas = new TextureAtlas("skin/particleparkpro.atlas");
        skin.addRegions(textureAtlas);

        var regions = skin.getRegions(fontName);
        BitmapFont font;
        if (regions != null) font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + fontName + ".fnt"), false), regions, true);
        else font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + fontName + ".fnt"), false), skin.getRegion(fontName), true);
        font.getData().setScale(fontScale);
        font.setUseIntegerPositions(fontUseIntegerPositions);
        skin.add("font", font);

        regions = skin.getRegions(headerName);
        if (regions != null) font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + headerName + ".fnt"), false), regions, true);
        else font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + headerName + ".fnt"), false), skin.getRegion(headerName), true);
        font.getData().setScale(fontScale);
        font.setUseIntegerPositions(fontUseIntegerPositions);
        skin.add("header", font);

        regions = skin.getRegions(blackName);
        if (regions != null) font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + blackName + ".fnt"), false), regions, true);
        else font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + blackName + ".fnt"), false), skin.getRegion(blackName), true);
        font.getData().setScale(fontScale);
        font.setUseIntegerPositions(fontUseIntegerPositions);
        skin.add("font-black", font);

        regions = skin.getRegions(boldName);
        if (regions != null) font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + boldName + ".fnt"), false), regions, true);
        else font = new BitmapFont(new BitmapFont.BitmapFontData(Gdx.files.internal("skin/" + boldName + ".fnt"), false), skin.getRegion(boldName), true);
        font.getData().setScale(fontScale);
        font.setUseIntegerPositions(fontUseIntegerPositions);
        skin.add("bold", font);

        skin.load(Gdx.files.internal("skin/particleparkpro.json"));
        Styles.initializeStyles();
    }
}
