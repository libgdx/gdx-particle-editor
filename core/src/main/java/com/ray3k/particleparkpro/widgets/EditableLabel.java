package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.ray3k.particleparkpro.Utils;
import lombok.Getter;

/**
 * A label that shows ellipses when its size is decreased below its preferred dimensions. If the user clicks the label,
 * the label is converted to a textfield, allowing the text to be modified.
 */
public class EditableLabel extends ToggleGroup {
    @Getter
    private String text;
    public Label label;
    public TextField textField;
    public EditableLabelStyle style;

    public EditableLabel(String text, EditableLabelStyle style) {
        this.text = text;
        this.style = style;

        label = new Label(text, style.labelStyle);
        label.setEllipsis("...");
        label.setTouchable(Touchable.enabled);
        table1.add(label).grow().minWidth(0);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                swap();
                getStage().setKeyboardFocus(textField);
                textField.selectAll();
            }
        });

        textField = new TextField(text, style.textFieldStyle);
        table2.add(textField).grow().minWidth(0);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                label.setText(textField.getText());
                EditableLabel.this.text = textField.getText();
            }
        });
        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) textField.selectAll();
                else swap();
            }
        });
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
    }

    public void setText(String text) {
        this.text = text;
        label.setText(text);
        textField.setText(text);
    }

    public void unfocused() {

    }

    public static class EditableLabelStyle {
        public LabelStyle labelStyle;
        public TextFieldStyle textFieldStyle;
    }
}
