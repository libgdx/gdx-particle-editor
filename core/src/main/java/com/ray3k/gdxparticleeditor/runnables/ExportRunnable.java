package com.ray3k.gdxparticleeditor.runnables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.ray3k.gdxparticleeditor.Core;
import com.ray3k.gdxparticleeditor.Settings;
import com.ray3k.gdxparticleeditor.widgets.poptables.PopError;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static com.ray3k.gdxparticleeditor.Core.*;
import static com.ray3k.gdxparticleeditor.Settings.*;
import static com.ray3k.gdxparticleeditor.Utils.showToast;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;

public class ExportRunnable implements Runnable {

    @Getter @Setter
    private Runnable onCompletionRunnable;

    @Getter @Setter
    private FileHandle exportFileHandle;

    @Getter @Setter
    private boolean exportImages;

    @Override
    public void run() {
        if (preferences.getBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION) && !exportFileHandle.extension().equals("p"))
            exportFileHandle = exportFileHandle.sibling(exportFileHandle.name() + ".p");

        Settings.setDefaultSavePath(exportFileHandle.parent());

        //enable all emitters for export
        particleEffect.getEmitters().clear();
        for (var entry : activeEmitters.entries()) {
            particleEffect.getEmitters().add(entry.key);
        }

        var fileError = false;
        Writer fileWriter = null;
        try {
            fileWriter = new FileWriter(exportFileHandle.file());
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

        if (exportImages)
            for (var fileHandle : fileHandles.values()) {
                if (fileHandle.parent().equals(exportFileHandle.parent())) continue;
                try {
                    fileHandle.copyTo(exportFileHandle.parent().child(fileHandle.name()));
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
        showToast("Exported " + exportFileHandle.name());

        if (!fileError) {
            if (onCompletionRunnable != null) Gdx.app.postRunnable(onCompletionRunnable);
        }
    }
}
