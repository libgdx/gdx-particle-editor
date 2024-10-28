package com.ray3k.gdxparticleeditor.runnables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.ray3k.gdxparticleeditor.FileDialogs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ray3k.gdxparticleeditor.Core.*;
import static com.ray3k.gdxparticleeditor.Settings.*;

public class SaveAsRunnable implements Runnable {

    private static boolean open;
    private SaveRunnable saveRunnable;

    public void setSaveRunnable (SaveRunnable runnable) {
        saveRunnable = runnable;
    }

    @Override
    public void run () {
        if (open) return;

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

        var useFileExtension = preferences.getBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION);
        var filterPatterns = useFileExtension ? new String[] {"p"} : null;
        var saveHandle = FileDialogs.saveDialog("Save", getDefaultSavePath(), defaultFileName, filterPatterns, "Particle Files (*.p)");

        if (saveHandle != null) {
            openFileFileHandle = saveHandle;
            saveRunnable.run();
        }

        stage.getRoot().setTouchable(Touchable.enabled);
        open = false;
    }
}
