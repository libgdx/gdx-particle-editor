package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.GradientColorValue;
import com.ray3k.particleparkpro.undo.Undoable;
import com.ray3k.particleparkpro.widgets.ColorGraph;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the tint of a gradient color value.
 */
@Data
@AllArgsConstructor
public class TintUndoable implements Undoable {
    private ParticleEmitter emitter;
    private GradientColorValue value;
    private final GradientColorValue oldValue = new GradientColorValue();
    private final GradientColorValue newValue = new GradientColorValue();
    private ColorGraph colorGraph;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        value.setTimeline(oldValue.getTimeline());
        value.setColors(oldValue.getColors());
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        value.setTimeline(newValue.getTimeline());
        value.setColors(newValue.getColors());
        refreshDisplay();
    }

    @Override
    public void start() {
        value.setTimeline(newValue.getTimeline());
        value.setColors(newValue.getColors());
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
