package com.ray3k.particleparkpro.widgets.styles;

import com.ray3k.particleparkpro.widgets.ColorGraph.ColorGraphStyle;
import com.ray3k.tenpatch.TenPatchDrawable;

import static com.ray3k.particleparkpro.Core.skin;

public class PPcolorGraphStyle extends ColorGraphStyle {
    public PPcolorGraphStyle() {
        background = skin.getDrawable("colorbar-bg-10");
        nodeStartUp = skin.getDrawable("colorbar-left-bg");
        nodeStartOver = null;
        nodeStartDown = null;
        nodeStartFill = skin.getDrawable("colorbar-left-fill");
        nodeUp = skin.getDrawable("colorbar-knob-bg");
        nodeOver = null;
        nodeDown = null;
        nodeFill = skin.getDrawable("colorbar-knob-fill");
        nodeEndUp = skin.getDrawable("colorbar-right-bg");
        nodeEndOver = null;
        nodeEndDown = null;
        nodeEndFill = skin.getDrawable("colorbar-right-fill");
        white = skin.get("colorbar-white-10", TenPatchDrawable.class);
    }
}
