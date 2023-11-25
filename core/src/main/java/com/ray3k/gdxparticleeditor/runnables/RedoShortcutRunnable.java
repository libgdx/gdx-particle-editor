package com.ray3k.gdxparticleeditor.runnables;

import com.ray3k.gdxparticleeditor.undo.UndoManager;

public class RedoShortcutRunnable implements Runnable {

    @Override
    public void run () {
        if (UndoManager.hasRedo()) {
            UndoManager.redo();
        }
    }
}
