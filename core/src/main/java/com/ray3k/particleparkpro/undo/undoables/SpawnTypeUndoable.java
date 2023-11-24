package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnShapeValue;
import com.ray3k.particleparkpro.undo.Undoable;
import com.ray3k.particleparkpro.widgets.subpanels.SpawnSubPanel.SpawnType;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the type of a spawn shape.
 */
@Data
@AllArgsConstructor
public class SpawnTypeUndoable implements Undoable {
    private ParticleEmitter emitter;
    private SpawnShapeValue value;
    private SpawnType spawnType;
    private SpawnType spawnTypeOld;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        value.setShape(spawnTypeOld.spawnShape);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        value.setShape(spawnType.spawnShape);
        refreshDisplay();
    }

    @Override
    public void start() {
        value.setShape(spawnType.spawnShape);
    }

    public void refreshDisplay() {
        effectEmittersPanel.populateEmitters();
        emitterPropertiesPanel.populateScrollTable(null);
        particleEffect.reset();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
