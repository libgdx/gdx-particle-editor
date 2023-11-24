package com.ray3k.particleparkpro.widgets.styles;

import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.ray3k.stripe.Spinner.SpinnerStyle;

import static com.ray3k.particleparkpro.Core.skin;

public class PPspinnerStyle extends SpinnerStyle {
    public PPspinnerStyle() {
        buttonPlusStyle = skin.get("spinner-top", ButtonStyle.class);
        buttonMinusStyle = skin.get("spinner-bottom", ButtonStyle.class);
        textFieldStyle = skin.get("spinner", TextFieldStyle.class);;
    }
}
