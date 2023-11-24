package com.ray3k.particleparkpro.widgets.styles;

import com.ray3k.stripe.DraggableTextList.DraggableTextListStyle;

import static com.ray3k.particleparkpro.Core.skin;

public class PPdraggableTextListNoBGStyle extends DraggableTextListStyle {
    public PPdraggableTextListNoBGStyle() {
        dividerUp = skin.getDrawable("draggable-list-divider-10");
        dividerOver = skin.getDrawable("draggable-list-divider-over-10");
        validBackgroundUp = skin.getDrawable("draggable-list-drag-background-10");
        dragBackgroundUp = skin.getDrawable("draggable-list-drag-remove-10");
        transparencyOnDrag = .25f;

        font = skin.getFont("font-black");
        textBackgroundUp = skin.getDrawable("list-selection-invisible");
        textBackgroundDown = skin.getDrawable("list-selection-invisible");
        textBackgroundOver = skin.getDrawable("list-over-10");
        textBackgroundChecked = skin.getDrawable("list-selection-10");
    }
}
