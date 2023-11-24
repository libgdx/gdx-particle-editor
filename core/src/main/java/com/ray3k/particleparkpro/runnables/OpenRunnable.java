package com.ray3k.particleparkpro.runnables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.particleparkpro.FileDialogs;
import com.ray3k.particleparkpro.Settings;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.widgets.poptables.PopConfirmLoad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Settings.*;
import static com.ray3k.particleparkpro.Utils.showToast;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

public class OpenRunnable implements Runnable {

    private static boolean open;

    @Override
    public void run () {
        if (open)
            return;

        if (unsavedChangesMade) {
            var saveFirstRunnable = new SaveRunnable();
            var saveAsFirstRunnable = new SaveAsRunnable();
            saveFirstRunnable.setSaveAsRunnable(saveAsFirstRunnable);
            saveAsFirstRunnable.setSaveRunnable(saveFirstRunnable);
            saveFirstRunnable.setOnCompletionRunnable(this);

            var pop = new PopConfirmLoad(saveFirstRunnable, () -> {
                unsavedChangesMade = false;
                OpenRunnable.this.run();
            });
            pop.show(foregroundStage);
            return;
        }

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            open = true;
            stage.getRoot().setTouchable(Touchable.disabled);

            if (effectEmittersPanel == null || emitterPropertiesPanel == null)
                return;

            var useFileExtension = preferences.getBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION);
            var filterPatterns = useFileExtension ? new String[] {"p"} : null;
            var fileHandle = FileDialogs.openDialog("Open", getDefaultSavePath(), filterPatterns, "Particle files (*.p)");

            if (fileHandle != null) {
                defaultFileName = fileHandle.name();
                Settings.setDefaultSavePath(fileHandle.parent());
                Gdx.app.postRunnable(() -> {
                    loadOnMainThread(fileHandle);
                });

                openFileFileHandle = fileHandle;
            }

            stage.getRoot().setTouchable(Touchable.enabled);
            open = false;
        });
        service.shutdown();
    }

    private void loadOnMainThread (FileHandle fileHandle) {
        var completed = Utils.loadParticle(fileHandle);

        if (!completed) return;

        selectedEmitter = particleEffect.getEmitters().first();

        effectEmittersPanel.populateEmitters();
        effectEmittersPanel.updateDisableableWidgets();
        emitterPropertiesPanel.populateScrollTable(null);
        effectEmittersPanel.hidePopEmitterControls();
        showToast("Opened " + fileHandle.name());

        UndoManager.clear();
        unsavedChangesMade = false;
        allowClose = true;
        updateWindowTitle();
    }
}
