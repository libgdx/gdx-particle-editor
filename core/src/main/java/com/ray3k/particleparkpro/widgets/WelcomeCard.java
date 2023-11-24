package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static com.ray3k.particleparkpro.Core.skin;

/**
 * A clickable button to be used with the welcome screen. It consists of a title, an image, description, and a child
 * TextButton.
 */
public class WelcomeCard extends Button {
    public WelcomeCard(String title, String subtitle, Drawable drawable, String buttonText) {
        super(skin, "card");

        top().left();

        defaults().space(15);
        var label = new Label(title, skin, "bold");
        add(label).left().expandX();

        row();
        var image = new Image(drawable);
        add(image).size(240, 190);

        row();
        label = new Label(subtitle, skin);
        add(label);

        row();
        var textButton = new TextButton(buttonText, skin, "highlighted-card");
        add(textButton).expandX().right();
    }
}
