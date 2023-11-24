package com.ray3k.particleparkpro.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.SetPropertyUndoable;
import com.ray3k.stripe.PopTable;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.ShownProperty.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipRightArrowStyle;

/**
 * PopTable used to add more properties to the EmitterPropertiesPanel.
 */
public class PopAddProperty extends PopTable {
    public PopAddProperty() {
        super(Core.skin.get(WindowStyle.class));

        setDraggable(false);
        setHideOnUnfocus(true);
        setKeepSizedWithinStage(true);
        addListener(new TableShowHideListener() {
            @Override
            public void tableShown(Event event) {
                Gdx.input.setInputProcessor(foregroundStage);
            }

            @Override
            public void tableHidden(Event event) {
                Gdx.input.setInputProcessor(stage);
            }
        });

        final int itemSpacing = 5;

        var scrollTable = new Table();
        var scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        add(scrollPane).grow();
        addForegroundScrollFocusListener(scrollPane);

        //Delay
        scrollTable.pad(5);
        scrollTable.defaults().space(itemSpacing).left();
        var delayCheckBox = new CheckBox("Delay", skin);
        delayCheckBox.setChecked(selectedEmitter.getDelay().isActive());
        scrollTable.add(delayCheckBox);
        addHandListener(delayCheckBox);
        onChange(delayCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, DELAY, delayCheckBox.isChecked(), "set Delay property"));
        });

        var popTable = addTooltip(delayCheckBox, "Time from beginning of effect to emission start, in milliseconds", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //Life Offset
        scrollTable.row();
        var lifeOffsetCheckbox = new CheckBox("Life Offset", skin);
        lifeOffsetCheckbox.setChecked(selectedEmitter.getLifeOffset().isActive());
        scrollTable.add(lifeOffsetCheckbox);
        addHandListener(lifeOffsetCheckbox);
        onChange(lifeOffsetCheckbox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, LIFE_OFFSET, lifeOffsetCheckbox.isChecked(), "set Life Offset property"));
        });

        popTable = addTooltip(lifeOffsetCheckbox, "Particle starting life consumed, in milliseconds", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //X Offset
        scrollTable.row();
        var xOffsetCheckBox = new CheckBox("X Offset", skin);
        xOffsetCheckBox.setChecked(selectedEmitter.getXOffsetValue().isActive());
        scrollTable.add(xOffsetCheckBox);
        addHandListener(xOffsetCheckBox);
        onChange(xOffsetCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, X_OFFSET, xOffsetCheckBox.isChecked(), "set X Offset property"));
        });

        popTable = addTooltip(xOffsetCheckBox, "Amount to offset a particle's starting X location, in world units", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //Y Offset
        scrollTable.row();
        var yOffsetCheckBox = new CheckBox("Y Offset", skin);
        yOffsetCheckBox.setChecked(selectedEmitter.getYOffsetValue().isActive());
        scrollTable.add(yOffsetCheckBox);
        addHandListener(yOffsetCheckBox);
        onChange(yOffsetCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, Y_OFFSET, yOffsetCheckBox.isChecked(), "set Y Offset property"));
        });

        popTable = addTooltip(yOffsetCheckBox, "Amount to offset a particle's starting y location, in world units", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //Velocity
        scrollTable.row();
        var velocityCheckBox = new CheckBox("Velocity", skin);
        velocityCheckBox.setChecked(selectedEmitter.getVelocity().isActive());
        scrollTable.add(velocityCheckBox);
        addHandListener(velocityCheckBox);
        onChange(velocityCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, VELOCITY, velocityCheckBox.isChecked(), "set Velocity property"));
        });

        popTable = addTooltip(velocityCheckBox, "Particle speed, in world units per second", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //Angle
        scrollTable.row();
        var angleCheckBox = new CheckBox("Angle", skin);
        angleCheckBox.setChecked(selectedEmitter.getAngle().isActive());
        scrollTable.add(angleCheckBox);
        addHandListener(angleCheckBox);
        onChange(angleCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, ANGLE, angleCheckBox.isChecked(), "set Angle property"));
        });

        popTable = addTooltip(angleCheckBox, "Particle emission angle, in degrees", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //Rotation
        scrollTable.row();
        var rotationCheckBox = new CheckBox("Rotation", skin);
        rotationCheckBox.setChecked(selectedEmitter.getRotation().isActive());
        scrollTable.add(rotationCheckBox);
        addHandListener(rotationCheckBox);
        onChange(rotationCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, ROTATION, rotationCheckBox.isChecked(), "set Rotation property"));
        });

        popTable = addTooltip(rotationCheckBox, "Particle rotation, in degrees", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //Wind
        scrollTable.row();
        var windCheckBox = new CheckBox("Wind", skin);
        windCheckBox.setChecked(selectedEmitter.getWind().isActive());
        scrollTable.add(windCheckBox);
        addHandListener(windCheckBox);
        onChange(windCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, WIND, windCheckBox.isChecked(), "set Wind property"));
        });

        popTable = addTooltip(windCheckBox, "Wind strength, in world units per second", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);

        //Graviity
        scrollTable.row();
        var gravityCheckBox = new CheckBox("Gravity", skin);
        gravityCheckBox.setChecked(selectedEmitter.getGravity().isActive());
        scrollTable.add(gravityCheckBox);
        addHandListener(gravityCheckBox);
        onChange(gravityCheckBox, () -> {
            UndoManager.add(new SetPropertyUndoable(selectedEmitter, GRAVITY, gravityCheckBox.isChecked(), "set Gravity property"));
        });

        popTable = addTooltip(gravityCheckBox, "Gravity strength, in world units per second", Align.left, Align.left, tooltipRightArrowStyle);
        popTable.setAttachOffsetX(-10);
        popTable.setKeepSizedWithinStage(false);
    }
}
