package com.ray3k.particleparkpro.widgets.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.ray3k.particleparkpro.widgets.Panel;

import static com.ray3k.particleparkpro.Core.skin;
import static com.ray3k.particleparkpro.Listeners.*;

/**
 * An introductory screen used exclusively in Wizard mode to help introduce users to Particle Park Pro.
 */
public class StartPanel extends Panel {
    private Table scrollTable;
    public static StartPanel summaryPanel;
    private ScrollPane scrollPane;
    private static final Vector2 temp = new Vector2();

    public StartPanel() {
        summaryPanel = this;
        setTouchable(Touchable.enabled);

        var label = new Label("Start", skin, "header");
        tabTable.add(label);

        bodyTable.defaults().space(5);
        scrollTable = new Table();
        scrollPane = new ScrollPane(scrollTable, skin, "emitter-properties");
        scrollPane.setFlickScroll(false);
        bodyTable.add(scrollPane).grow();
        addScrollFocusListener(scrollPane);

        populateScrollTable();
    }

    public void populateScrollTable() {
        scrollTable.clearChildren(true);
        scrollTable.defaults().space(10);

        var label = new Label("Welcome!", skin, "bold");
        scrollTable.add(label);

        scrollTable.row();
        label = new Label("Particle Park Pro allows you to create particle effects for your libGDX games.", skin);
        scrollTable.add(label);

        scrollTable.row();
        var table = new Table();
        table.columnDefaults(0).right();
        table.columnDefaults(1).left();
        scrollTable.add(table).padTop(10);

        var titleLabel = new Label("Effect Emitters:", skin, "header");
        titleLabel.setColor(skin.getColor("selection"));
        table.add(titleLabel);

        label = new Label(" load and create the emitters that comprise your particle effect", skin);
        table.add(label);

        table.row();
        titleLabel = new Label("Emitter Properties:", skin, "header");
        titleLabel.setColor(skin.getColor("selection"));
        table.add(titleLabel);

        label = new Label(" edit the behavior and appearance of the selected emitter", skin);
        table.add(label);

        table.row();
        titleLabel = new Label("Summary:", skin, "header");
        titleLabel.setColor(skin.getColor("selection"));
        table.add(titleLabel);

        label = new Label(" save your particle effect to a file that can be loaded in libGDX", skin);
        table.add(label);

        scrollTable.row();
        label = new Label("Learn", skin, "bold");
        scrollTable.add(label).padTop(20);

        scrollTable.row();
        table = new Table();
        table.defaults().space(10);
        scrollTable.add(table);

        var textButton = new TextButton("GitHub", skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            Gdx.net.openURI("https://github.com/raeleus/Particle-Park-Pro");
        });

        textButton = new TextButton("YouTube", skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {

        });
    }
}
