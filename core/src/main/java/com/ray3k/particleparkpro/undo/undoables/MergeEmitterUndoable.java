package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.ray3k.particleparkpro.undo.Undoable;
import lombok.Builder;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

/**
 * Undoable to merge a new list of emitters with the existing emitters.
 */
@Builder
public class MergeEmitterUndoable implements Undoable {
    private OrderedMap<ParticleEmitter, Boolean> oldActiveEmitters;
    private ObjectMap<String, FileHandle> oldFileHandles;
    private ObjectMap<String, Sprite> oldSprites;
    private int oldSelectedIndex;
    private OrderedMap<ParticleEmitter, Boolean> newActiveEmitters;
    private ObjectMap<String, FileHandle> newFileHandles;
    private ObjectMap<String, Sprite> newSprites;
    private int newSelectedIndex;
    private String description;

    @Override
    public void undo() {
        particleEffect.getEmitters().clear();

        activeEmitters.clear();
        for (var oldActiveEmitter : oldActiveEmitters) {
            activeEmitters.put(oldActiveEmitter.key, oldActiveEmitter.value);
            if (oldActiveEmitter.value) particleEffect.getEmitters().add(oldActiveEmitter.key);
        }

        fileHandles.clear();
        fileHandles.putAll(oldFileHandles);

        sprites.clear();
        sprites.putAll(oldSprites);

        selectedEmitter = activeEmitters.orderedKeys().get(oldSelectedIndex);

        refreshDisplay();
    }

    @Override
    public void redo() {
        particleEffect.getEmitters().clear();

        activeEmitters.clear();
        for (var newActiveEmitter : newActiveEmitters) {
            activeEmitters.put(newActiveEmitter.key, newActiveEmitter.value);
            if (newActiveEmitter.value) particleEffect.getEmitters().add(newActiveEmitter.key);
        }

        fileHandles.clear();
        fileHandles.putAll(newFileHandles);

        sprites.clear();
        sprites.putAll(newSprites);

        selectedEmitter = activeEmitters.orderedKeys().get(newSelectedIndex);

        refreshDisplay();
    }

    @Override
    public void start() {
        selectedEmitter = activeEmitters.orderedKeys().get(newSelectedIndex);
        particleEffect.reset();
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void refreshDisplay() {
        effectEmittersPanel.populateEmitters();
        effectEmittersPanel.updateDisableableWidgets();
        emitterPropertiesPanel.populateScrollTable(null);
        particleEffect.reset();
    }
}
