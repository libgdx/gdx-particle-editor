package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter.RangedNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.RangedNumericValueUndoable;
import com.ray3k.particleparkpro.undo.undoables.SetPropertyUndoable;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.particleparkpro.widgets.ToggleGroup;
import com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.ShownProperty;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.spinnerStyle;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomArrowStyle;

/**
 * A widget that allows modification of an emitter value that only has a min/max. Numeric spinners.
 */
public class RangeSubPanel extends Panel {
    public RangeSubPanel(String title, RangedNumericValue value, String tooltip, String undoDescription, ShownProperty closeProperty, float sliderIncrement, float sliderRange, int decimalPlaces, boolean adjustByPPM, boolean resetParticleEffect) {
        final int spinnerWidth = 70;
        final int itemSpacing = 5;

        setTouchable(Touchable.enabled);

        tabTable.padRight(7);
        tabTable.left();
        var label = new Label(title, skin, "header");
        tabTable.add(label).space(3);

        if (closeProperty != null) {
            var button = new Button(skin, "close");
            tabTable.add(button);
            addHandListener(button);
            onChange(button, () -> UndoManager.add(new SetPropertyUndoable(selectedEmitter, closeProperty, false, "set " + closeProperty.name + " property")));
        }

        //Value
        var table = new Table();
        bodyTable.add(table).expandX().left();
        table.defaults().space(itemSpacing).left();
        label = new Label("Value:", skin);
        table.add(label);

        var highToggleWidget = new ToggleGroup();
        table.add(highToggleWidget);

        //Value single
        highToggleWidget.table1.defaults().space(itemSpacing);
        var valueSpinner = new Spinner(value.getLowMin(), 1, decimalPlaces, Orientation.RIGHT_STACK, spinnerStyle);
        valueSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table1.add(valueSpinner).width(spinnerWidth);
        addIbeamListener(valueSpinner.getTextField());
        addHandListener(valueSpinner.getButtonPlus());
        addHandListener(valueSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(valueSpinner);
        addTooltip(valueSpinner, "The " + tooltip, Align.top, Align.top, tooltipBottomArrowStyle);

        var button = new Button(skin, "moveright");
        highToggleWidget.table1.add(button);
        addHandListener(button);
        addTooltip(button, "Expand to define a range for the value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(button, highToggleWidget::swap);

        //Value range
        highToggleWidget.table2.defaults().space(itemSpacing);
        var valueMinSpinner = new Spinner(value.getLowMin(), 1, decimalPlaces, Orientation.RIGHT_STACK, spinnerStyle);
        valueMinSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table2.add(valueMinSpinner).width(spinnerWidth);
        addIbeamListener(valueMinSpinner.getTextField());
        addHandListener(valueMinSpinner.getButtonPlus());
        addHandListener(valueMinSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(valueMinSpinner);
        addTooltip(valueMinSpinner, "The minimum " + tooltip, Align.top, Align.top, tooltipBottomArrowStyle);

        var valueMaxSpinner = new Spinner(value.getLowMax(), 1, decimalPlaces, Orientation.RIGHT_STACK, spinnerStyle);
        valueMaxSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table2.add(valueMaxSpinner).width(spinnerWidth);
        addIbeamListener(valueMaxSpinner.getTextField());
        addHandListener(valueMaxSpinner.getButtonPlus());
        addHandListener(valueMaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(valueMaxSpinner);
        addTooltip(valueMaxSpinner, "The maximum " + tooltip, Align.top, Align.top, tooltipBottomArrowStyle);

        button = new Button(skin, "moveleft");
        highToggleWidget.table2.add(button);
        addHandListener(button);
        addTooltip(button, "Collapse to define a single value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(button, highToggleWidget::swap);

        var changeListener = onChange(valueSpinner, () -> {
            var undo = new RangedNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setLow(valueSpinner.getValue());
            UndoManager.add(undo);

            valueMinSpinner.setValue(valueSpinner.getValue());
            valueMaxSpinner.setValue(valueSpinner.getValue());
            if (resetParticleEffect) particleEffect.reset();
        });
        addInfiniteSlider(valueSpinner, sliderIncrement, sliderRange, adjustByPPM, changeListener);

        changeListener = onChange(valueMinSpinner, () -> {
            var undo = new RangedNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setLowMin(valueMinSpinner.getValue());
            UndoManager.add(undo);

            valueSpinner.setValue(valueMinSpinner.getValue());
            if (resetParticleEffect) particleEffect.reset();
        });
        addInfiniteSlider(valueMinSpinner, sliderIncrement, sliderRange, adjustByPPM, changeListener);

        changeListener = onChange(valueMaxSpinner, () -> {
            var undo = new RangedNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setLowMax(valueMaxSpinner.getValue());
            UndoManager.add(undo);

            valueSpinner.setValue(valueMaxSpinner.getValue());
            if (resetParticleEffect) particleEffect.reset();
        });
        addInfiniteSlider(valueMaxSpinner, sliderIncrement, sliderRange, adjustByPPM, changeListener);

        onChange(button, () -> {
            if (highToggleWidget.showingTable1 && !MathUtils.isEqual(value.getLowMin(), value.getLowMax())) {
                var undo = new RangedNumericValueUndoable(selectedEmitter, value, undoDescription);
                undo.oldValue.set(value);
                undo.newValue.set(value);
                undo.newValue.setLow(valueSpinner.getValue());
                UndoManager.add(undo);

                valueMinSpinner.setValue(valueSpinner.getValue());
                valueMaxSpinner.setValue(valueSpinner.getValue());
                if (resetParticleEffect) particleEffect.reset();
            }
        });
        if (!MathUtils.isEqual(value.getLowMin(), value.getLowMax())) highToggleWidget.swap();
    }
}
