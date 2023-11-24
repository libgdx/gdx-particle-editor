package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the value of a scaled numeric value.
 */
@AllArgsConstructor
public class ScaledNumericValueUndoable implements Undoable {
    private ParticleEmitter emitter;
    public final ScaledNumericValue newValue = new ScaledNumericValue();
    public final ScaledNumericValue oldValue = new ScaledNumericValue();
    private ScaledNumericValue value;
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
