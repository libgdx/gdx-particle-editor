package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to delete the selected emitter.
 */
@AllArgsConstructor
public class DeleteEmitterUndoable implements Undoable {
    private ParticleEmitter emitter;
    private int index;
    private String description;

    @Override
    public void undo() {
        particleEffect.getEmitters().add(emitter);
        activeEmitters.put(emitter, true);
        activeEmitters.orderedKeys().removeIndex(activeEmitters.orderedKeys().size - 1);
        activeEmitters.orderedKeys().insert(index, emitter);
        selectedEmitter = emitter;
        refreshDisplay();
    }

    @Override
    public void redo() {
        particleEffect.getEmitters().removeValue(emitter, true);
        activeEmitters.remove(emitter);
        selectedEmitter = activeEmitters.orderedKeys().get(Math.min(index, activeEmitters.orderedKeys().size - 1));
        refreshDisplay();
    }

    @Override
    public void start() {
        particleEffect.getEmitters().removeValue(emitter, true);
        activeEmitters.remove(emitter);
        selectedEmitter = activeEmitters.orderedKeys().get(Math.min(index, activeEmitters.orderedKeys().size - 1));
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
