package com.ray3k.gdxparticleeditor.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.ray3k.gdxparticleeditor.Listeners;
import com.ray3k.gdxparticleeditor.Utils;
import com.ray3k.stripe.PopTable;

import java.io.IOException;

import static com.ray3k.gdxparticleeditor.Core.foregroundStage;
import static com.ray3k.gdxparticleeditor.Core.skin;
import static com.ray3k.gdxparticleeditor.Settings.logFile;

/**
 * PopTable used to display errors during runtime that can be recovered from. These are typically file errors when
 * saving or opening particles and images. A link to the log directory enables users to retrieve the crash log file
 * through their OS file explorer.
 */
public class PopError extends PopTable {
    private String message;
    private String error;
    private final InputProcessor previousInputProcessor;

    public PopError(String message, String error) {
        super(skin.get(WindowStyle.class));

        setHideOnUnfocus(true);
        key(Keys.ESCAPE, this::hide);

        this.message = message;
        this.error = error;

        populate();
        previousInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(foregroundStage);
    }

    @Override
    public void hide(Action action) {
        super.hide(action);
        if (Gdx.input.getInputProcessor() == foregroundStage) Gdx.input.setInputProcessor(previousInputProcessor);
    }

    private void populate() {
        clearChildren();
        pad(20);
        defaults().space(10);

        var label = new Label("GDX Particle Editor encountered an error:", skin, "bold");
        add(label);

        row();
        label = new Label(message, skin);
        label.setWrap(true);
        add(label).growX();

        row();
        var scrollPane = new ScrollPane(null, skin);
        add(scrollPane).grow();

        label = new Label(error, skin);
        label.setColor(Color.RED);
        scrollPane.setActor(label);

        row();
        var table = new Table();
        table.defaults().uniformX().fillX().space(10);
        add(table);

        var textButton = new TextButton("Close", skin);
        table.add(textButton);
        Listeners.addHandListener(textButton);
        Listeners.onChange(textButton, () -> {
            hide();
        });

        textButton = new TextButton("Open log", skin);
        table.add(textButton);
        Listeners.addHandListener(textButton);
        Listeners.onChange(textButton, () -> {
            try {
                Utils.openFileExplorer(logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            hide();
        });
    }
}
