package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnShapeValue;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the edges value of a spawn shape.
 */
@Data
@AllArgsConstructor
public class SpawnEdgesUndoable implements Undoable {
    private ParticleEmitter emitter;
    private SpawnShapeValue value;
    private boolean active;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        value.setEdges(!active);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        value.setEdges(active);
        refreshDisplay();
    }

    @Override
    public void start() {
        value.setEdges(active);
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
