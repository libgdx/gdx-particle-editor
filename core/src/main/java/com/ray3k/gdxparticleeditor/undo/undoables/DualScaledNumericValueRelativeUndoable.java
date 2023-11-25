package com.ray3k.gdxparticleeditor.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.ray3k.gdxparticleeditor.undo.Undoable;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.gdxparticleeditor.Core.particleEffect;
import static com.ray3k.gdxparticleeditor.Core.selectedEmitter;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.gdxparticleeditor.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to set the relative value of a ScaledNumericValue pair (ex. Size)
 */
@Data
@AllArgsConstructor
public class DualScaledNumericValueRelativeUndoable implements Undoable {
    private ParticleEmitter emitter;
    private ScaledNumericValue xValue;
    private ScaledNumericValue yValue;
    private boolean active;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        xValue.setRelative(!active);
        yValue.setRelative(!active);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        xValue.setRelative(active);
        yValue.setRelative(active);
        refreshDisplay();
    }

    @Override
    public void start() {
        xValue.setRelative(active);
        yValue.setRelative(active);
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
