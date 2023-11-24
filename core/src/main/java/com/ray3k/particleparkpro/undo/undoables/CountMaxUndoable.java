package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable for increasing the maximum particle count for the emitter.
 */
@AllArgsConstructor
public class CountMaxUndoable implements Undoable {
    private ParticleEmitter emitter;
    private int newValue;
    private int oldValue;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        emitter.setMaxParticleCount(oldValue);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;
        emitter.setMaxParticleCount(newValue);
        refreshDisplay();
    }

    @Override
    public void start() {
        emitter.setMaxParticleCount(newValue);
    }

    @Override
    public String getDescription() {
        return "change Count max";
    }

    private void refreshDisplay() {
        effectEmittersPanel.populateEmitters();
        emitterPropertiesPanel.populateScrollTable(null);
        particleEffect.reset();
    }
}
