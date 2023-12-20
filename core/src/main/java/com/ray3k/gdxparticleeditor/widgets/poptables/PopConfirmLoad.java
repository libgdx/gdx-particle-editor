package com.ray3k.gdxparticleeditor.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.gdxparticleeditor.Listeners;
import com.ray3k.gdxparticleeditor.Utils;
import com.ray3k.stripe.PopTable;

import static com.ray3k.gdxparticleeditor.Core.foregroundStage;
import static com.ray3k.gdxparticleeditor.Core.skin;
import static com.ray3k.gdxparticleeditor.Listeners.*;
import static com.ray3k.gdxparticleeditor.Listeners.addHandListener;
import static com.ray3k.gdxparticleeditor.Listeners.onChange;

/**
 * PopTable used to display errors during runtime that can be recovered from. These are typically file errors when
 * saving or opening particles and images. A link to the log directory enables users to retrieve the crash log file
 * through their OS file explorer.
 */
public class PopConfirmLoad extends PopTable {
    private final InputProcessor previousInputProcessor;
    private Runnable runnableSave;
    private Runnable runnableDiscard;

    public PopConfirmLoad(Runnable runnableSave, Runnable runnableDiscard) {
        super(skin.get(WindowStyle.class));

        setHideOnUnfocus(true);
        key(Keys.ESCAPE, this::hide);
        key(Keys.ENTER, this::save);
        key(Keys.NUMPAD_ENTER, this::save);

        this.runnableSave = runnableSave;
        this.runnableDiscard = runnableDiscard;

        addListener(new TableShowHideListener() {
            @Override
            public void tableShown(Event event) {
                hideAllTooltips();
            }

            @Override
            public void tableHidden(Event event) {
                if (Gdx.input.getInputProcessor() == foregroundStage) Gdx.input.setInputProcessor(previousInputProcessor);
            }
        });

        previousInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(foregroundStage);
        populate();
    }

    private void populate() {
        clearChildren();
        pad(20);
        defaults().space(10);

        var label = new Label("Save Changes?", skin, "bold");
        add(label);

        row();
        label = new Label("Do you want to save your changes before loading the particle effect?", skin);
        label.setWrap(true);
        label.setAlignment(Align.center);
        add(label).growX();

        row();
        var table = new Table();
        table.defaults().uniformX().fillX().space(10);
        add(table);

        var textButton = new TextButton("Save changes", skin, "highlighted");
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            save();
        });

        textButton = new TextButton("Discard changes", skin, "highlighted-red");
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
            Gdx.input.setInputProcessor(previousInputProcessor);
            runnableDiscard.run();
        });

        textButton = new TextButton("Cancel", skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
            Gdx.input.setInputProcessor(previousInputProcessor);
        });
    }

    private void save() {
        hide();
        Gdx.input.setInputProcessor(previousInputProcessor);
        runnableSave.run();
    }
}
