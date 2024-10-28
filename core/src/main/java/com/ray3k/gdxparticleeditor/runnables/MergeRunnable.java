package com.ray3k.gdxparticleeditor.runnables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.ray3k.gdxparticleeditor.FileDialogs;
import com.ray3k.gdxparticleeditor.Settings;
import com.ray3k.gdxparticleeditor.Utils;
import com.ray3k.gdxparticleeditor.undo.UndoManager;
import com.ray3k.gdxparticleeditor.undo.undoables.MergeEmitterUndoable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ray3k.gdxparticleeditor.Core.*;
import static com.ray3k.gdxparticleeditor.Settings.*;
import static com.ray3k.gdxparticleeditor.Utils.showToast;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.gdxparticleeditor.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;

public class MergeRunnable implements Runnable {

    private static boolean open;

    @Override
    public void run () {
        if (open)
            return;

        if (UIUtils.isMac) openWindow();
        else {
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(this::openWindow);
            service.shutdown();
        }
    }

    private void openWindow() {
        open = true;
        stage.getRoot().setTouchable(Touchable.disabled);

        if (effectEmittersPanel == null || emitterPropertiesPanel == null) return;

        var useFileExtension = preferences.getBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION);
        var filterPatterns = useFileExtension ? new String[] {"p"} : null;
        var fileHandle = FileDialogs.openDialog("Merge", getDefaultSavePath(), filterPatterns, "Particle files (*.p)");

        if (fileHandle != null) {
            defaultFileName = fileHandle.name();
            Settings.setDefaultSavePath(fileHandle.parent());
            Gdx.app.postRunnable(() -> {
                loadOnMainThread(fileHandle);
            });
        }

        stage.getRoot().setTouchable(Touchable.enabled);
        open = false;
    }

    private void loadOnMainThread (FileHandle fileHandle) {

        var oldActiveEmitters = new OrderedMap<>(activeEmitters);
        var oldFileHandles = new ObjectMap<>(fileHandles);
        var oldSprites = new ObjectMap<>(sprites);
        var oldSelectedIndex = activeEmitters.keys().toArray().indexOf(selectedEmitter, true);

        var completed = Utils.mergeParticle(fileHandle);
        if (!completed) return;

        UndoManager.add(MergeEmitterUndoable
            .builder()
            .oldActiveEmitters(oldActiveEmitters)
            .oldFileHandles(oldFileHandles)
            .oldSprites(oldSprites)
            .oldSelectedIndex(oldSelectedIndex)
            .newActiveEmitters(new OrderedMap<>(activeEmitters))
            .newFileHandles(new ObjectMap<>(fileHandles))
            .newSprites(new ObjectMap<>(sprites))
            .newSelectedIndex(activeEmitters.keys().toArray().indexOf(selectedEmitter, true))
            .description("Merge Particle Effect")
            .build());
        effectEmittersPanel.populateEmitters();
        effectEmittersPanel.updateDisableableWidgets();
        effectEmittersPanel.hidePopEmitterControls();
        emitterPropertiesPanel.populateScrollTable(null);
        showToast("Merged " + fileHandle.name());

        updateWindowTitle();
    }
}
