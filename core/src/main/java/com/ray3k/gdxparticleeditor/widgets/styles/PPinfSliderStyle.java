package com.ray3k.gdxparticleeditor.widgets.styles;

import com.ray3k.gdxparticleeditor.widgets.InfSlider.InfSliderStyle;

import static com.ray3k.gdxparticleeditor.Core.skin;

public class PPinfSliderStyle extends InfSliderStyle {
    public PPinfSliderStyle() {
        background = skin.getDrawable("slider-bg-10");
        knobUp = skin.getDrawable("slider-knob");
    }
}
