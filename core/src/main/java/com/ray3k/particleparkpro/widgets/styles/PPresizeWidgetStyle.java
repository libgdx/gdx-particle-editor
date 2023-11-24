package com.ray3k.particleparkpro.widgets.styles;

import com.ray3k.stripe.ResizeWidget.ResizeWidgetStyle;

import static com.ray3k.particleparkpro.Core.skin;

public class PPresizeWidgetStyle extends ResizeWidgetStyle {
    public PPresizeWidgetStyle() {
        background = skin.getDrawable("resize-background-10");
        handle = skin.getDrawable("resize-knob-up");
        handleOver = skin.getDrawable("resize-knob-over");
        minorHandle = skin.getDrawable("resize-knob-up");
        minorHandleOver = skin.getDrawable("resize-knob-over");
    }
}
