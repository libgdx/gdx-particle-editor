package com.ray3k.particleparkpro;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;

import static com.ray3k.particleparkpro.Core.preferences;

/**
 * This class stores the settings for the preview panel.
 */
public class PreviewSettings {
    @Getter
    private static final Color backgroundColor = new Color(Color.BLACK);
    @Getter
    private static final Color axesColor = new Color(Color.BLUE);
    @Getter
    private static final Color gridColor = new Color(Color.LIGHT_GRAY);
    @Getter
    private static final Color statisticsColor = new Color(Color.WHITE);
    @Getter
    private static float pixelsPerMeter;
    @Getter
    private static float deltaMultiplier;
    @Getter
    private static boolean statisticsEnabled;
    @Getter
    private static boolean axesEnabled;
    @Getter
    private static boolean gridEnabled;
    @Getter
    private static float gridMajorGridlines;
    @Getter
    private static float gridMinorGridlines;
    public static boolean showResizeInterface;
    public static Texture previewImageTexture;
    public static float previewImageX;
    public static float previewImageY;
    public static float previewImageWidth;
    public static float previewImageHeight;

    public static void initializeSettings() {
        Color.valueOf(preferences.getString("backgroundColor", Color.BLACK.toString()), backgroundColor);
        Color.valueOf(preferences.getString("axesColor", Color.BLUE.toString()), axesColor);
        Color.valueOf(preferences.getString("gridColor", Color.LIGHT_GRAY.toString()), gridColor);
        Color.valueOf(preferences.getString("statisticsColor", Color.WHITE.toString()), statisticsColor);
        pixelsPerMeter = preferences.getFloat("pixelsPerMeter", 1f);
        deltaMultiplier = preferences.getFloat("deltaMultiplier", 1f);
        statisticsEnabled = preferences.getBoolean("statisticsEnabled", false);
        axesEnabled = preferences.getBoolean("axesEnabled", false);
        gridEnabled = preferences.getBoolean("gridEnabled", false);
        gridMajorGridlines = preferences.getFloat("gridMajorGridlines", 200f);
        gridMinorGridlines = preferences.getFloat("gridMinorGridlines", 25f);
    }

    public static void resetPreviewSettings() {
        setBackgroundColor(Color.BLACK);
        setAxesColor(Color.BLUE);
        setGridColor(Color.LIGHT_GRAY);
        setStatisticsColor(Color.WHITE);
        setPixelsPerMeter(1f);
        setDeltaMultiplier(1f);
        setStatisticsEnabled(false);
        setAxesEnabled(false);
        setGridEnabled(false);
        setGridMajorGridlines(200f);
        setGridMinorGridlines(25f);
    }

    public static void setBackgroundColor(Color backgroundColor) {
        PreviewSettings.backgroundColor.set(backgroundColor);
        preferences.putString("backgroundColor", backgroundColor.toString());
        preferences.flush();
    }

    public static void setBackgroundColorTemporarily(Color backgroundColor) {
        PreviewSettings.backgroundColor.set(backgroundColor);
    }

    public static void setAxesColor(Color axesColor) {
        PreviewSettings.axesColor.set(axesColor);
        preferences.putString("axesColor", axesColor.toString());
        preferences.flush();
    }

    public static void setAxesColorTemporarily(Color axesColor) {
        PreviewSettings.axesColor.set(axesColor);
    }

    public static void setGridColor(Color gridColor) {
        PreviewSettings.gridColor.set(gridColor);
        preferences.putString("gridColor", gridColor.toString());
        preferences.flush();
    }

    public static void setGridColorTemporarily(Color gridColor) {
        PreviewSettings.gridColor.set(gridColor);
    }

    public static void setStatisticsColor(Color statisticsColor) {
        PreviewSettings.statisticsColor.set(statisticsColor);
        preferences.putString("statisticsColor", statisticsColor.toString());
        preferences.flush();
    }

    public static void setStatisticsColorTemporarily(Color statisticsColor) {
        PreviewSettings.statisticsColor.set(statisticsColor);
    }

    public static void setPixelsPerMeter(float pixelsPerMeter) {
        PreviewSettings.pixelsPerMeter = pixelsPerMeter;
        preferences.putFloat("pixelsPerMeter", pixelsPerMeter);
        preferences.flush();
    }

    public static void setDeltaMultiplier(float deltaMultiplier) {
        PreviewSettings.deltaMultiplier = deltaMultiplier;
        preferences.putFloat("deltaMultiplier", deltaMultiplier);
        preferences.flush();
    }

    public static void setStatisticsEnabled(boolean statisticsEnabled) {
        PreviewSettings.statisticsEnabled = statisticsEnabled;
        preferences.putBoolean("statisticsEnabled", statisticsEnabled);
        preferences.flush();
    }

    public static void setAxesEnabled(boolean axesEnabled) {
        PreviewSettings.axesEnabled = axesEnabled;
        preferences.putBoolean("axesEnabled", axesEnabled);
        preferences.flush();
    }

    public static void setGridEnabled(boolean gridEnabled) {
        PreviewSettings.gridEnabled = gridEnabled;
        preferences.putBoolean("gridEnabled", gridEnabled);
        preferences.flush();
    }

    public static void setGridMajorGridlines(float gridMajorGridlines) {
        PreviewSettings.gridMajorGridlines = gridMajorGridlines;
        preferences.putFloat("gridMajorGridlines", gridMajorGridlines);
        preferences.flush();
    }

    public static void setGridMinorGridlines(float gridMinorGridlines) {
        PreviewSettings.gridMinorGridlines = gridMinorGridlines;
        preferences.putFloat("gridMinorGridlines", gridMinorGridlines);
        preferences.flush();
    }
}
