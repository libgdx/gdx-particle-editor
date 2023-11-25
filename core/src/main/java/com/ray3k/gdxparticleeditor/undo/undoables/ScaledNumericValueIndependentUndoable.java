package com.ray3k.gdxparticleeditor.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.IndependentScaledNumericValue;
import com.ray3k.gdxparticleeditor.undo.Undoable;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.gdxparticleeditor.Core.particleEffect;
import static com.ray3k.gdxparticleeditor.Core.selectedEmitter;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.gdxparticleeditor.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the independent value of a scaled numeric value.
 */
@Data
@AllArgsConstructor
public class ScaledNumericValueIndependentUndoable implements Undoable {
    private ParticleEmitter emitter;
    private IndependentScaledNumericValue value;
    private boolean active;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        value.setIndependent(!active);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        value.setIndependent(active);
        refreshDisplay();
    }

    @Override
    public void start() {
        value.setIndependent(active);
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
