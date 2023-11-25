package com.ray3k.gdxparticleeditor.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnEllipseSide;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnShapeValue;
import com.ray3k.gdxparticleeditor.undo.Undoable;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.gdxparticleeditor.Core.particleEffect;
import static com.ray3k.gdxparticleeditor.Core.selectedEmitter;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.gdxparticleeditor.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the side of a spawn shape.
 */
@Data
@AllArgsConstructor
public class SpawnSideUndoable implements Undoable {
    private ParticleEmitter emitter;
    private SpawnShapeValue value;
    private SpawnEllipseSide side;
    private SpawnEllipseSide oldSide;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        value.setSide(oldSide);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        value.setSide(side);
        refreshDisplay();
    }

    @Override
    public void start() {
        value.setSide(side);
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
