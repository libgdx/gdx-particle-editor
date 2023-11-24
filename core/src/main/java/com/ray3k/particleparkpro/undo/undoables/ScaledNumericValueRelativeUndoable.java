package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Change the relative value of a scaled numeric value.
 */
@Data
@AllArgsConstructor
public class ScaledNumericValueRelativeUndoable implements Undoable {
    private ParticleEmitter emitter;
    private ScaledNumericValue value;
    private boolean active;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        value.setRelative(!active);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        value.setRelative(active);
        refreshDisplay();
    }

    @Override
    public void start() {
        value.setRelative(active);
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
