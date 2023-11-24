package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the options of the particle effect.
 */
@Data
@AllArgsConstructor
public class OptionsUndoable implements Undoable {
    private ParticleEmitter emitter;
    public enum Type {
        ADDITIVE, ATTACHED, CONTINUOUS, ALIGNED, BEHIND, PMA
    }
    private Type type;
    private boolean newValue;
    private String description;

    @Override
    public void undo() {
        setOption(!newValue);
        refreshDisplay();
    }

    @Override
    public void redo() {
        setOption(newValue);
        refreshDisplay();
    }

    private void setOption(boolean value) {
        switch (type) {
            case ADDITIVE:
                emitter.setAdditive(value);
                break;
            case ATTACHED:
                emitter.setAttached(value);
                break;
            case CONTINUOUS:
                emitter.setContinuous(value);
                break;
            case ALIGNED:
                emitter.setAligned(value);
                break;
            case BEHIND:
                emitter.setBehind(value);
                break;
            case PMA:
                emitter.setPremultipliedAlpha(value);
                break;
        }
    }

    @Override
    public void start() {
        setOption(newValue);
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void refreshDisplay() {
        emitterPropertiesPanel.populateScrollTable(null);
        particleEffect.reset();
    }
}
