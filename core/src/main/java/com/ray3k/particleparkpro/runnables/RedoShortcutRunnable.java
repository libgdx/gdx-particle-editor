package com.ray3k.particleparkpro.runnables;

import com.ray3k.particleparkpro.undo.UndoManager;

public class RedoShortcutRunnable implements Runnable {

    @Override
    public void run () {
        if (UndoManager.hasRedo()) {
            UndoManager.redo();
        }
    }
}
