package com.ray3k.particleparkpro.runnables;

import com.ray3k.particleparkpro.undo.UndoManager;

public class UndoShortcutRunnable implements Runnable {

    @Override
    public void run () {
        if (UndoManager.hasUndo()) {
            UndoManager.undo();
        }
    }
}
