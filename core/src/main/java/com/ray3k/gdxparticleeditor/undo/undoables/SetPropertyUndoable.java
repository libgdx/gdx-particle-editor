package com.ray3k.gdxparticleeditor.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.ray3k.gdxparticleeditor.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.gdxparticleeditor.Core.*;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.gdxparticleeditor.widgets.panels.EmitterPropertiesPanel.ShownProperty;
import static com.ray3k.gdxparticleeditor.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

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
                if (!active) {
                    var value = new ScaledNumericValue();
                    value.set(selectedEmitter.getAngle());
                    defaultAngleMap.put(selectedEmitter, value);
                    
                    selectedEmitter.getAngle().setHigh(0);
                    selectedEmitter.getAngle().setLow(0);
                } else {
                    selectedEmitter.getAngle().set(defaultAngleMap.get(selectedEmitter));
                }
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
