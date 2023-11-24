package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * A layout widget that can be programmatically changed to display one of two tables. This is useful in user interfaces
 * that need to display alternate layouts depending on certain criteria.
 */
public class ToggleGroup extends Container<Table> {
    public Table table1 = new Table();
    public Table table2 = new Table();
    public boolean showingTable1;

    public ToggleGroup() {
        showTable1();
        fill();
    }

    public void showTable1() {
        setActor(table1);
        showingTable1 = true;
    }

    public void showTable2() {
        setActor(table2);
        showingTable1 = false;
    }

    public void swap() {
        if (showingTable1) showTable2();
        else showTable1();
    }

    @Override @Deprecated
    public void setActor(Table table) {
        super.setActor(table);
    }
}
