package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.Builder;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to set the value of a ScaledNumericValue pair (ex. Size)
 */
@Builder(toBuilder = true)
public class DualScaledNumericValueUndoable implements Undoable {
    private ParticleEmitter emitter;
    public final ScaledNumericValue newXvalue = new ScaledNumericValue();
    public final ScaledNumericValue oldXvalue = new ScaledNumericValue();
    private ScaledNumericValue xValue;
    public final ScaledNumericValue newYvalue = new ScaledNumericValue();
    public final ScaledNumericValue oldYvalue = new ScaledNumericValue();
    private ScaledNumericValue yValue;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        xValue.set(oldXvalue);
        yValue.set(oldYvalue);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        xValue.set(newXvalue);
        yValue.set(newYvalue);
        refreshDisplay();
    }

    @Override
    public void start() {
        xValue.set(newXvalue);
        yValue.set(newYvalue);
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
