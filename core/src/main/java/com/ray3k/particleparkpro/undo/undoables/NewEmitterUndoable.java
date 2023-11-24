package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to create a new emitter and add it to the particle effect.
 */
@AllArgsConstructor
public class NewEmitterUndoable implements Undoable {
    private ParticleEmitter emitter;
    private String description;

    @Override
    public void undo() {
        var index = particleEffect.getEmitters().indexOf(emitter, true);
        particleEffect.getEmitters().removeIndex(index);
        activeEmitters.remove(emitter);
        selectedEmitter = particleEffect.getEmitters().get(Math.min(index, activeEmitters.orderedKeys().size - 1));
        refreshDisplay();
    }

    @Override
    public void redo() {
        particleEffect.getEmitters().add(emitter);
        activeEmitters.put(emitter, true);
        selectedEmitter = emitter;
        refreshDisplay();
    }

    @Override
    public void start() {
        particleEffect.getEmitters().add(emitter);
        activeEmitters.put(emitter, true);
        selectedEmitter = emitter;
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void refreshDisplay() {
        effectEmittersPanel.populateEmitters();
        effectEmittersPanel.updateDisableableWidgets();
        emitterPropertiesPanel.populateScrollTable(null);
        particleEffect.reset();
    }
}
