package com.ray3k.particleparkpro.widgets.styles;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.ray3k.stripe.PopColorPicker.PopColorPickerStyle;

import static com.ray3k.particleparkpro.Core.skin;

public class PPcolorPickerStyle extends PopColorPickerStyle {
    public PPcolorPickerStyle() {
        background = skin.getDrawable("cp-background-10");
        stageBackground = skin.getDrawable("stage-background");
        titleBarBackground = skin.getDrawable("cp-header-10");
        labelStyle = skin.get(LabelStyle.class);
        textButtonStyle = skin.get(TextButtonStyle.class);
        fileTextButtonStyle = skin.get("cp-file", TextButtonStyle.class);
        scrollPaneStyle = skin.get(ScrollPaneStyle.class);
        colorSwatch = skin.getDrawable("cp-color-swatch");
        colorSwatchNew = skin.getDrawable("cp-color-swatch-new");
        colorSwatchPopBackground = skin.getDrawable("cp-color-swatch-pop-background-10");
        previewSwatchBackground = skin.getDrawable("cp-swatch");
        previewSwatchOld = skin.getDrawable("cp-swatch-old");
        previewSwatchNew = skin.getDrawable("cp-swatch-new");
        previewSwatchSingleBackground = skin.getDrawable("cp-swatch-null");
        previewSwatchSingle = skin.getDrawable("cp-swatch-new-null");
        textFieldStyle = skin.get(TextFieldStyle.class);
        hexTextFieldStyle = skin.get("cp", TextFieldStyle.class);
        colorSwatchPopPreview = skin.getDrawable("cp-color-swatch-pop-preview");
        colorSliderBackground = skin.getDrawable("cp-slider-background-10");
        colorSliderKnobHorizontal = skin.getDrawable("cp-slider-knob-horizontal");
        colorSliderKnobVertical = skin.getDrawable("cp-slider-knob-vertical");
        colorKnobCircleBackground = skin.getDrawable("cp-color-knob-circle-background");
        colorKnobCircleForeground = skin.getDrawable("cp-color-circle-knob-foreground");
        radioButtonStyle = skin.get("cp-radio", ImageButtonStyle.class);
        increaseButtonStyle = skin.get("cp-up", ImageButtonStyle.class);
        decreaseButtonStyle = skin.get("cp-down", ImageButtonStyle.class);
        checkerBackground = skin.getDrawable("cp-checker-10");
    }
}
