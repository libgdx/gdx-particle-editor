package com.ray3k.particleparkpro.widgets.panels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.ray3k.particleparkpro.widgets.Panel;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;

/**
 * A summary screen used exclusively in wizard mode that displays some emitter statistics and gives the user an option
 * to save.
 */
public class SummaryPanel extends Panel {
    private Table scrollTable;
    public static SummaryPanel summaryPanel;
    private ScrollPane scrollPane;
    private static final Vector2 temp = new Vector2();

    public SummaryPanel() {
        summaryPanel = this;
        setTouchable(Touchable.enabled);

        var label = new Label("Summary", skin, "header");
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

        var label = new Label("Congratulations! You have created a particle effect. It looks great!", skin);
        scrollTable.add(label);

        scrollTable.row();
        var table = new Table();
        scrollTable.add(table);

        table.defaults().spaceRight(10).uniformX();
        table.columnDefaults(0).right();
        table.columnDefaults(1).left();
        label = new Label("Emitters:", skin, "header");
        label.setColor(skin.getColor("selection"));
        table.add(label);

        label = new Label(Integer.toString(activeEmitters.size), skin);
        table.add(label);

        table.row();
        label = new Label("Images:", skin, "header");
        label.setColor(skin.getColor("selection"));
        table.add(label);

        label = new Label(Integer.toString(fileHandles.size), skin);
        table.add(label);

        table.row();
        label = new Label("Max particle count:", skin, "header");
        label.setColor(skin.getColor("selection"));
        table.add(label);

        label = new Label(Integer.toString(maxParticleCount), skin);
        table.add(label);

        scrollTable.row();
        label = new Label("Save Particle Effect?", skin, "bold");
        scrollTable.add(label).padTop(20);

        scrollTable.row();
        var textButton = new TextButton("Save", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, saveRunnable);
    }
}
