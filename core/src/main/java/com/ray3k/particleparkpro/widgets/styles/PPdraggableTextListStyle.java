package com.ray3k.particleparkpro.widgets.styles;

import com.ray3k.stripe.DraggableTextList.DraggableTextListStyle;

import static com.ray3k.particleparkpro.Core.skin;

public class PPdraggableTextListStyle extends DraggableTextListStyle {
    public PPdraggableTextListStyle() {
        dividerUp = skin.getDrawable("draggable-list-divider-10");
        dividerOver = skin.getDrawable("draggable-list-divider-over-10");
        background = skin.getDrawable("draggable-list-10");
        validBackgroundUp = skin.getDrawable("draggable-list-drag-background-10");
        dragBackgroundUp = skin.getDrawable("draggable-list-drag-remove-10");
        transparencyOnDrag = .25f;

        font = skin.getFont("font-black");
        textBackgroundUp = skin.getDrawable("list-selection-invisible");
        textBackgroundDown = skin.getDrawable("list-selection-invisible");
        textBackgroundOver = skin.getDrawable("list-selection-10");
        textBackgroundChecked = null;
    }
}
