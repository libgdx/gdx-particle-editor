package com.ray3k.particleparkpro.widgets.styles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.ray3k.particleparkpro.widgets.LineGraph.LineGraphStyle;

import static com.ray3k.particleparkpro.Core.skin;

public class PPlineGraphStyle extends LineGraphStyle {
    public PPlineGraphStyle() {
        background = skin.getDrawable("graph-bg-10");
        backgroundLabelStyle = skin.get("graph", LabelStyle.class);
        nodeUp = skin.getDrawable("graph-node-up");
        nodeDown = skin.getDrawable("graph-node-over");
        nodeOver = skin.getDrawable("graph-node-over");
        lineColor = Color.valueOf("0074ff");
        lineWidth = 2;
    }
}
