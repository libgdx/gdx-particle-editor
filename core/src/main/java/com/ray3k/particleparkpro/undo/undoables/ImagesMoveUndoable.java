package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.particleEffect;
import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to rearrange the images in the emitter.
 */
@AllArgsConstructor
public class ImagesMoveUndoable implements Undoable {
    private ParticleEmitter emitter;
    private int oldIndex;
    private int newIndex;
    private String description;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        var paths = emitter.getImagePaths();
        var sprites = emitter.getSprites();
        var path = paths.get(newIndex);
        var sprite = sprites.get(newIndex);
        paths.removeIndex(newIndex);
        sprites.removeIndex(newIndex);
        paths.insert(oldIndex, path);
        sprites.insert(oldIndex, sprite);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        var paths = emitter.getImagePaths();
        var sprites = emitter.getSprites();
        var path = paths.get(oldIndex);
        var sprite = sprites.get(oldIndex);
        paths.removeIndex(oldIndex);
        sprites.removeIndex(oldIndex);
        paths.insert(newIndex, path);
        sprites.insert(newIndex, sprite);
        refreshDisplay();
    }

    @Override
    public void start() {
        var paths = emitter.getImagePaths();
        var sprites = emitter.getSprites();
        var path = paths.get(oldIndex);
        var sprite = sprites.get(oldIndex);
        paths.removeIndex(oldIndex);
        sprites.removeIndex(oldIndex);
        paths.insert(newIndex, path);
        sprites.insert(newIndex, sprite);
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
