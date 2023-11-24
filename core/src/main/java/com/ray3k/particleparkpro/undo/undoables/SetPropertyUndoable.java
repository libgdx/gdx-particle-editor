package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.ShownProperty;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the active/disabled status of an emitter.
 */
@AllArgsConstructor
public class SetPropertyUndoable implements Undoable {
    private ParticleEmitter emitter;
    private ShownProperty property;
    private boolean active;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        activateProperty(!active);

        refreshDisplay(!active);
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        activateProperty(active);

        refreshDisplay(active);
    }

    @Override
    public void start() {
        activateProperty(active);

        if (active) emitterPropertiesPanel.populateScrollTable(property);
        else emitterPropertiesPanel.removeProperty(property);
    }

    public void activateProperty(boolean active) {
        switch (property) {
            case DELAY:
                selectedEmitter.getDelay().setActive(active);
                break;
            case LIFE_OFFSET:
                selectedEmitter.getLifeOffset().setActive(active);
                break;
            case X_OFFSET:
                selectedEmitter.getXOffsetValue().setActive(active);
                break;
            case Y_OFFSET:
                selectedEmitter.getYOffsetValue().setActive(active);
                break;
            case VELOCITY:
                selectedEmitter.getVelocity().setActive(active);
                break;
            case ANGLE:
                selectedEmitter.getAngle().setActive(active);
                break;
            case ROTATION:
                selectedEmitter.getRotation().setActive(active);
                break;
            case WIND:
                selectedEmitter.getWind().setActive(active);
                break;
            case GRAVITY:
                selectedEmitter.getGravity().setActive(active);
                break;
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void refreshDisplay(boolean active) {
        effectEmittersPanel.populateEmitters();
        if (active) emitterPropertiesPanel.populateScrollTable(property);
        else emitterPropertiesPanel.removeProperty(property);
        particleEffect.reset();
    }
}
