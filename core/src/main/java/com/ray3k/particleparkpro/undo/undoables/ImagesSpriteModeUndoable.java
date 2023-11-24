package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpriteMode;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to change the sprite mode of the emitter.
 */
@AllArgsConstructor
public class ImagesSpriteModeUndoable implements Undoable {
    private ParticleEmitter emitter;
    private SpriteMode spriteMode;
    private SpriteMode oldSpriteMode;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        emitter.setSpriteMode(oldSpriteMode);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        emitter.setSpriteMode(spriteMode);
        refreshDisplay();
    }

    @Override
    public void start() {
        emitter.setSpriteMode(spriteMode);
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
