package com.ray3k.gdxparticleeditor.runnables;

import com.ray3k.gdxparticleeditor.undo.UndoManager;

public class UndoShortcutRunnable implements Runnable {

    @Override
    public void run () {
        if (UndoManager.hasUndo()) {
            UndoManager.undo();
        }
    }
}
