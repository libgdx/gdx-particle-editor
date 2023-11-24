package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to add images to the emitter.
 */
@AllArgsConstructor
public class ImagesAddUndoable implements Undoable {
    private ParticleEmitter emitter;
    private Array<FileHandle> selectedFileHandles;
    private String description;
    private final Array<String> newImagePaths = new Array<>();
    private final ObjectMap<String, FileHandle> newFileHandles = new ObjectMap<>();
    private final ObjectMap<String, Sprite> newSpriteMap = new ObjectMap<>();
    private final Array<Sprite> newSprites = new Array<>();

    @Override
    public void undo() {
        selectedEmitter = emitter;

        emitter.getImagePaths().removeRange(emitter.getImagePaths().size - newImagePaths.size, emitter.getImagePaths().size - 1);
        Utils.removeUnusedImageFiles();
        for (var newSprite : newSpriteMap) {
            sprites.remove(newSprite.key);
        }
        emitter.getSprites().removeRange(emitter.getSprites().size - newSprites.size, emitter.getSprites().size - 1);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        emitter.getImagePaths().addAll(newImagePaths);
        fileHandles.putAll(newFileHandles);
        sprites.putAll(newSpriteMap);
        emitter.getSprites().addAll(newSprites);
        refreshDisplay();
    }

    @Override
    public void start() {
        for (var fileHandle : selectedFileHandles) {
            var path = fileHandle.name();
            newImagePaths.add(path);
            newFileHandles.put(path, fileHandle);
            var sprite = new Sprite(new Texture(fileHandle));
            newSpriteMap.put(path, sprite);
            newSprites.add(sprite);
        }

        emitter.getImagePaths().addAll(newImagePaths);
        fileHandles.putAll(newFileHandles);
        sprites.putAll(newSpriteMap);
        emitter.getSprites().addAll(newSprites);
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
