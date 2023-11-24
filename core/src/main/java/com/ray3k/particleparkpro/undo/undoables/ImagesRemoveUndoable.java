package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.undo.Undoable;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to remove images from the emitter.
 */
public class ImagesRemoveUndoable implements Undoable {
    private ParticleEmitter emitter;
    private String path;
    private FileHandle fileHandle;
    private Sprite sprite;
    private String description;
    private int index;

    public ImagesRemoveUndoable(ParticleEmitter emitter, String path, FileHandle fileHandle, Sprite sprite,
                                String description) {
        this.emitter = emitter;
        this.path = path;
        this.fileHandle = fileHandle;
        this.sprite = sprite;
        this.description = description;
        index = emitter.getSprites().indexOf(sprite, true);
    }

    @Override
    public void undo() {
        selectedEmitter = emitter;

        emitter.getImagePaths().insert(index, path);
        fileHandles.put(path, fileHandle);
        sprites.put(path, sprite);
        emitter.getSprites().insert(index, sprite);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        emitter.getImagePaths().removeValue(path, false);
        Utils.removeUnusedImageFiles();
        sprites.remove(path);
        emitter.getSprites().removeValue(sprite, true);
        refreshDisplay();
    }

    @Override
    public void start() {
        emitter.getImagePaths().removeValue(path, false);
        Utils.removeUnusedImageFiles();
        sprites.remove(path);
        emitter.getSprites().removeValue(sprite, true);
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
