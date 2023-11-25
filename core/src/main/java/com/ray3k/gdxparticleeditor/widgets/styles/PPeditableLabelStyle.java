package com.ray3k.gdxparticleeditor.widgets.styles;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.ray3k.gdxparticleeditor.widgets.EditableLabel.EditableLabelStyle;

import static com.ray3k.gdxparticleeditor.Core.skin;

public class PPeditableLabelStyle extends EditableLabelStyle {
    public PPeditableLabelStyle() {
        labelStyle = skin.get(LabelStyle.class);
        textFieldStyle = skin.get("editable-label-selection", TextFieldStyle.class);
    }
}
