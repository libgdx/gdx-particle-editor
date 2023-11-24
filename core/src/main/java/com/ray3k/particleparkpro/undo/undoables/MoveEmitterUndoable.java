package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to rearrange the emitters in the particle effect.
 */
@AllArgsConstructor
public class MoveEmitterUndoable implements Undoable {
    private ParticleEmitter emitter;
    private int oldIndex;
    private int newIndex;
    private String description;

    @Override
    public void undo() {
        activeEmitters.orderedKeys().removeIndex(newIndex);
        activeEmitters.orderedKeys().insert(oldIndex, emitter);

        particleEffect.getEmitters().clear();
        for (var entry : activeEmitters.entries()) {
            if (entry.value) particleEffect.getEmitters().add(entry.key);
        }

        selectedEmitter = emitter;
        refreshDisplay();
    }

    @Override
    public void redo() {
        activeEmitters.orderedKeys().removeIndex(oldIndex);
        activeEmitters.orderedKeys().insert(newIndex, emitter);

        particleEffect.getEmitters().clear();
        for (var entry : activeEmitters.entries()) {
            if (entry.value) particleEffect.getEmitters().add(entry.key);
        }

        selectedEmitter = emitter;
        refreshDisplay();
    }

    @Override
    public void start() {
        activeEmitters.orderedKeys().removeIndex(oldIndex);
        activeEmitters.orderedKeys().insert(newIndex, emitter);

        particleEffect.getEmitters().clear();
        for (var entry : activeEmitters.entries()) {
            if (entry.value) particleEffect.getEmitters().add(entry.key);
        }

        selectedEmitter = emitter;
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
