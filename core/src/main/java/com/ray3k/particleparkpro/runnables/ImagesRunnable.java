package com.ray3k.particleparkpro.runnables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.particleparkpro.FileDialogs;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.ImagesAddUndoable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ray3k.particleparkpro.Core.selectedEmitter;
import static com.ray3k.particleparkpro.Core.stage;
import static com.ray3k.particleparkpro.Settings.getDefaultImagePath;
import static com.ray3k.particleparkpro.Settings.setDefaultImagePath;
import static com.ray3k.particleparkpro.Utils.showToast;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;
import static com.ray3k.particleparkpro.widgets.subpanels.ImagesSubPanel.imagesSubPanel;

public class ImagesRunnable implements Runnable {

    private static boolean open;

    @Override
    public void run () {
        if (open)
            return;

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
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
        });
        service.shutdown();
    }

    private void loadOnMainThread (Array<FileHandle> selectedFileHandles) {
        UndoManager.add(new ImagesAddUndoable(selectedEmitter, selectedFileHandles, "Add Images"));
        if (selectedFileHandles.size > 0) {
            imagesSubPanel.updateList();
            imagesSubPanel.updateDisabled();

            showToast(selectedFileHandles.size == 1 ? "Added image " + selectedFileHandles.first().name() : "Added " + selectedFileHandles.size + " images");
        }
    }
}
