package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.CountMaxUndoable;
import com.ray3k.particleparkpro.undo.undoables.CountMinUndoable;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.Core.skin;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.spinnerStyle;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomArrowStyle;

/**
 * A widget that allows the user to modify the Count value of the particle effect specifically. Numeric spinners for min
 * and max.
 */
public class CountSubPanel extends Panel {
    public CountSubPanel() {
        setTouchable(Touchable.enabled);

        final var itemSpacing = 5;
        final var gap = 15;
        final var spinnerWidth = 50;

        tabTable.left();
        var label = new Label("Count", skin, "header");
        tabTable.add(label);

        bodyTable.defaults().space(itemSpacing);
        bodyTable.left();

        label = new Label("Min:", skin);
        bodyTable.add(label);

        var minSpinner = new Spinner(selectedEmitter.getMinParticleCount(), 1, 0, Orientation.RIGHT_STACK, spinnerStyle);
        bodyTable.add(minSpinner).width(spinnerWidth);
        addHandListener(minSpinner.getButtonMinus());
        addHandListener(minSpinner.getButtonPlus());
        addIbeamListener(minSpinner.getTextField());
        addUnfocusOnEnterKeyListener(minSpinner);
        addTooltip(minSpinner, "The minimum number of particles at all times", Align.top, Align.top, tooltipBottomArrowStyle);
        var changeListener = onChange(minSpinner, () -> UndoManager.add(new CountMinUndoable(selectedEmitter, minSpinner.getValueAsInt(), selectedEmitter.getMinParticleCount())));
        addInfiniteSlider(minSpinner, 1, 20, false, changeListener);

        label = new Label("Max:", skin);
        bodyTable.add(label).spaceLeft(gap);

        var maxSpinner = new Spinner(selectedEmitter.getMaxParticleCount(), 1, 0, Orientation.RIGHT_STACK, spinnerStyle);
        bodyTable.add(maxSpinner).width(spinnerWidth);
        addHandListener(maxSpinner.getButtonMinus());
        addHandListener(maxSpinner.getButtonPlus());
        addIbeamListener(maxSpinner.getTextField());
        addUnfocusOnEnterKeyListener(maxSpinner);
        addTooltip(maxSpinner, "The maximum number of particles allowed", Align.top, Align.top, tooltipBottomArrowStyle);
        changeListener = onChange(maxSpinner, () -> UndoManager.add(new CountMaxUndoable(selectedEmitter, maxSpinner.getValueAsInt(), selectedEmitter.getMaxParticleCount())));
        addInfiniteSlider(maxSpinner, 1, 20, false, changeListener);
    }
}
