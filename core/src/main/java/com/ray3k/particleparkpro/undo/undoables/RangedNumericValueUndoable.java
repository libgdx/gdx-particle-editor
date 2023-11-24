package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.RangedNumericValue;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the value of a RangedNumericValue.
 */
@AllArgsConstructor
public class RangedNumericValueUndoable implements Undoable {
    private ParticleEmitter emitter;
    public final RangedNumericValue newValue = new RangedNumericValue();
    public final RangedNumericValue oldValue = new RangedNumericValue();
    private RangedNumericValue value;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        value.set(oldValue);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        value.set(newValue);
        refreshDisplay();
    }

    @Override
    public void start() {
        value.set(newValue);
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void refreshDisplay() {
        effectEmittersPanel.populateEmitters();
        emitterPropertiesPanel.populateScrollTable(null);
        particleEffect.reset();
    }
}
