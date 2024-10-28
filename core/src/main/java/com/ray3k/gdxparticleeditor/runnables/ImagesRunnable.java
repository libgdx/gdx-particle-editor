package com.ray3k.gdxparticleeditor.runnables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
import com.ray3k.gdxparticleeditor.FileDialogs;
import com.ray3k.gdxparticleeditor.undo.UndoManager;
import com.ray3k.gdxparticleeditor.undo.undoables.ImagesAddUndoable;
import com.ray3k.gdxparticleeditor.widgets.subpanels.ImagesSubPanel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ray3k.gdxparticleeditor.Core.selectedEmitter;
import static com.ray3k.gdxparticleeditor.Core.stage;
import static com.ray3k.gdxparticleeditor.Settings.getDefaultImagePath;
import static com.ray3k.gdxparticleeditor.Settings.setDefaultImagePath;
import static com.ray3k.gdxparticleeditor.Utils.showToast;
import static com.ray3k.gdxparticleeditor.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.gdxparticleeditor.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;
import static com.ray3k.gdxparticleeditor.widgets.subpanels.ImagesSubPanel.imagesSubPanel;

public class ImagesRunnable implements Runnable {

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

        var selectedFileHandles = FileDialogs.openMultipleDialog("Add images", getDefaultImagePath(), new String[] {"png","jpg","jpeg"}, "Image files (*.png;*.jpg;*.jpeg)");
        if (selectedFileHandles == null) {
            stage.getRoot().setTouchable(Touchable.enabled);
            open = false;
            return;
        }

        if (selectedFileHandles.size > 0) {
            setDefaultImagePath(selectedFileHandles.first().parent());
            Gdx.app.postRunnable(() -> {
                loadOnMainThread(selectedFileHandles);
            });
        }

        stage.getRoot().setTouchable(Touchable.enabled);
        open = false;
    }

    private void loadOnMainThread (Array<FileHandle> selectedFileHandles) {
        UndoManager.add(new ImagesAddUndoable(selectedEmitter, ImagesSubPanel.imagesSubPanel.list.getSelectedIndex() + 1, selectedFileHandles, "Add Images"));
        if (selectedFileHandles.size > 0) {
            imagesSubPanel.updateList();
            imagesSubPanel.updateDisabled();

            showToast(selectedFileHandles.size == 1 ? "Added image " + selectedFileHandles.first().name() : "Added " + selectedFileHandles.size + " images");
        }
    }
}
