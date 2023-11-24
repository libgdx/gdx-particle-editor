package com.ray3k.particleparkpro.undo;

import com.badlogic.gdx.utils.Array;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.Utils;

import static com.ray3k.particleparkpro.Core.allowClose;

/**
 * The UndoManager is a system to handle undo/redo in the app. The description of the currently primed undo/redo is
 * provided. The undoIndex indicates where in the history that the user is currently at. Certain actions will wipe the
 * undo history, such as loading a new effect. Adding a new undoable will erase all undoables in the history past the
 * current undoIndex.
 */
public class UndoManager {
    public final static Array<Undoable> undoables = new Array<>();
    public static int undoIndex = -1;

    public static void add(Undoable undoable) {
        if (hasRedo()) undoables.removeRange(undoIndex + 1, undoables.size - 1);

        undoables.add(undoable);
        undoable.start();
        undoIndex = undoables.size - 1;
        Utils.refreshUndoButtons();

        Core.unsavedChangesMade = true;
        allowClose = false;
        Core.updateWindowTitle();
    }

    public static String getUndoDescription() {
        return hasUndo() ? undoables.get(undoIndex).getDescription() : "";
    }

    public static String getRedoDescription() {
        return hasRedo() ? undoables.get(undoIndex + 1).getDescription() : "";
    }

    public static boolean hasUndo() {
        return undoIndex >= 0;
    }

    public static boolean hasRedo() {
        return undoables.size > 0 && undoIndex < undoables.size - 1;
    }

    public static void undo() {
        if (!hasUndo()) return;
        var undoable = undoables.get(undoIndex--);
        Utils.showToast("Undo " + undoable.getDescription());
        undoable.undo();
        Utils.refreshUndoButtons();
        Core.unsavedChangesMade = true;
        allowClose = false;
        Core.updateWindowTitle();
    }

    public static void redo() {
        if (!hasRedo()) return;
        var undoable = undoables.get(++undoIndex);
        Utils.showToast("Redo " + undoable.getDescription());
        undoable.redo();
        Utils.refreshUndoButtons();
        Core.unsavedChangesMade = true;
        allowClose = false;
        Core.updateWindowTitle();
    }

    public static void clear() {
        undoables.clear();
        undoIndex = -1;
        Utils.refreshUndoButtons();
    }
}
