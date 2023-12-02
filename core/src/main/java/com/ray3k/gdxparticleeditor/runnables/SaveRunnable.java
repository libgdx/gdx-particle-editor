package com.ray3k.gdxparticleeditor.runnables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.ray3k.gdxparticleeditor.Core;
import com.ray3k.gdxparticleeditor.Settings;
import com.ray3k.gdxparticleeditor.widgets.poptables.PopError;
import com.ray3k.stripe.PopTable.TableShowHideListener;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static com.ray3k.gdxparticleeditor.Core.*;
import static com.ray3k.gdxparticleeditor.Settings.*;
import static com.ray3k.gdxparticleeditor.Utils.showToast;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;

public class SaveRunnable implements Runnable {

    private SaveAsRunnable saveAsRunnable;
    @Getter @Setter
    private Runnable onCompletionRunnable;

    public void setSaveAsRunnable (SaveAsRunnable runnable) {
        saveAsRunnable = runnable;
    }

    @Override
    public void run () {
        if (openFileFileHandle != null) {
            if (preferences.getBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION) && !openFileFileHandle.extension().equals("p"))
                openFileFileHandle = openFileFileHandle.sibling(openFileFileHandle.name() + ".p");

            Settings.setDefaultSavePath(openFileFileHandle.parent());
            defaultFileName = openFileFileHandle.name();

            //enable all emitters for export
            particleEffect.getEmitters().clear();
            for (var entry : activeEmitters.entries()) {
                particleEffect.getEmitters().add(entry.key);
            }

            var fileError = false;
            Writer fileWriter = null;
            try {
                fileWriter = new FileWriter(openFileFileHandle.file());
                particleEffect.save(fileWriter);
            } catch (IOException e) {
                var error = "Error saving particle file.";
                var pop = new PopError(error, e.getMessage());
                pop.show(foregroundStage);

                Gdx.app.error(Core.class.getName(), error, e);
                fileError = true;
            } finally {
                StreamUtils.closeQuietly(fileWriter);
            }

            if (preferences.getBoolean(NAME_EXPORT_IMAGES, DEFAULT_EXPORT_IMAGES)) for (var fileHandle : fileHandles.values()) {
                if (fileHandle.parent().equals(openFileFileHandle.parent())) continue;
                try {
                    fileHandle.copyTo(openFileFileHandle.parent().child(fileHandle.name()));
                } catch (GdxRuntimeException e) {
                    var error = "Error copying files to save location.";
                    var pop = new PopError(error, e.getMessage());
                    pop.show(foregroundStage);

                    Gdx.app.error(Core.class.getName(), error, e);
                    fileError = true;
                }
            }

            //reset enabled/disabled emitters
            particleEffect.getEmitters().clear();
            for (var entry : activeEmitters.entries()) {
                if (entry.value) particleEffect.getEmitters().add(entry.key);
            }

            effectEmittersPanel.hidePopEmitterControls();
            showToast("Saved " + openFileFileHandle.name());

            if (!fileError) {
                unsavedChangesMade = false;
                allowClose = true;
                Core.updateWindowTitle();
                if (onCompletionRunnable != null) Gdx.app.postRunnable(onCompletionRunnable);
            }
        } else {
            saveAsRunnable.run();
        }
    }
}
