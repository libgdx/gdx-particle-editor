package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.OptionsUndoable;
import com.ray3k.particleparkpro.undo.undoables.OptionsUndoable.Type;
import com.ray3k.particleparkpro.widgets.Panel;

import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.Core.skin;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomArrowStyle;

/**
 * A widget that allows modification of the emitter properties additive, attached, continuous, aligned, behind, and
 * premultiplied alpha.
 */
public class OptionsSubPanel extends Panel {
    public OptionsSubPanel() {
        setTouchable(Touchable.enabled);

        final var itemSpacing = 5;
        final var tooltipWidth = 250;

        tabTable.left();
        var label = new Label("Options", skin, "header");
        tabTable.add(label);

        bodyTable.defaults().space(itemSpacing).left();
        bodyTable.left();

        //Additive
        var additiveCheckBox = new CheckBox("Additive", skin);
        additiveCheckBox.setChecked(selectedEmitter.isAdditive());
        bodyTable.add(additiveCheckBox);
        addHandListener(additiveCheckBox);
        addTooltip(additiveCheckBox, "Additive blending is used when the particle emitter is drawn. This causes overlapping colors to approach white",
            Align.top, Align.top, tooltipWidth, tooltipBottomArrowStyle);
        onChange(additiveCheckBox, () -> {
            var undoable = new OptionsUndoable(selectedEmitter, Type.ADDITIVE, additiveCheckBox.isChecked(), "change Additive option");
            UndoManager.add(undoable);
        });

        //Attached
        bodyTable.row();
        var attachedCheckBox = new CheckBox("Attached", skin);
        attachedCheckBox.setChecked(selectedEmitter.isAttached());
        bodyTable.add(attachedCheckBox);
        addHandListener(attachedCheckBox);
        addTooltip(attachedCheckBox,  "An attached particle emitter draws its particles relative to its origin. This makes existing particles move with the emitter when the particle effect's position is changed.", Align.top, Align.top, tooltipWidth, tooltipBottomArrowStyle);
        onChange(attachedCheckBox, () -> {
            var undoable = new OptionsUndoable(selectedEmitter, Type.ATTACHED, attachedCheckBox.isChecked(), "change Attached option");
            UndoManager.add(undoable);
        });

        //Continuous
        bodyTable.row();
        var continuousCheckBox = new CheckBox("Continuous", skin);
        continuousCheckBox.setChecked(selectedEmitter.isContinuous());
        bodyTable.add(continuousCheckBox);
        addHandListener(continuousCheckBox);
        addTooltip(continuousCheckBox, "A continuous particle emitter will keep emitting particles even after the duration has expired", Align.top, Align.top, tooltipWidth, tooltipBottomArrowStyle);
        onChange(continuousCheckBox, () -> {
            var undoable = new OptionsUndoable(selectedEmitter, Type.CONTINUOUS, continuousCheckBox.isChecked(), "change Continuous option");
            UndoManager.add(undoable);
        });

        //Aligned
        bodyTable.row();
        var alignedCheckBox = new CheckBox("Aligned", skin);
        alignedCheckBox.setChecked(selectedEmitter.isAligned());
        bodyTable.add(alignedCheckBox);
        addHandListener(alignedCheckBox);
        addTooltip(alignedCheckBox,"An aligned particle emitter will rotate it's particles relative to the angle of the particle effect. If the particle effect rotates, the particles rotate as well.", Align.top, Align.top, tooltipWidth, tooltipBottomArrowStyle);
        onChange(alignedCheckBox, () -> {
            var undoable = new OptionsUndoable(selectedEmitter, Type.ALIGNED, alignedCheckBox.isChecked(), "change Aligned option");
            UndoManager.add(undoable);
        });

        //Behind
        bodyTable.row();
        var behindCheckBox = new CheckBox("Behind", skin);
        behindCheckBox.setChecked(selectedEmitter.isBehind());
        bodyTable.add(behindCheckBox);
        addHandListener(behindCheckBox);
        addTooltip(behindCheckBox, "Behind has no practical application in the current libGDX API, but is included for backwards compatibility", Align.top, Align.top, tooltipWidth, tooltipBottomArrowStyle);
        onChange(behindCheckBox, () -> {
            var undoable = new OptionsUndoable(selectedEmitter, Type.BEHIND, behindCheckBox.isChecked(), "change Behind option");
            UndoManager.add(undoable);
        });

        //Premultiplied alpha
        bodyTable.row();
        var premultipliedAlphaCheckBox = new CheckBox("Premultiplied Alpha", skin);
        premultipliedAlphaCheckBox.setChecked(selectedEmitter.isPremultipliedAlpha());
        bodyTable.add(premultipliedAlphaCheckBox);
        addHandListener(premultipliedAlphaCheckBox);
        addTooltip(premultipliedAlphaCheckBox, "Premultiplied alpha is an alternative blending mode that expects RGB values to be multiplied by their transparency. Enable this value if your texture atlas is also set to premultiplied alpha.", Align.top, Align.top, tooltipWidth, tooltipBottomArrowStyle);
        onChange(premultipliedAlphaCheckBox, () -> {
            var undoable = new OptionsUndoable(selectedEmitter, Type.PMA, premultipliedAlphaCheckBox.isChecked(), "change PremultipliedAlpha option");
            UndoManager.add(undoable);
        });
    }
}
