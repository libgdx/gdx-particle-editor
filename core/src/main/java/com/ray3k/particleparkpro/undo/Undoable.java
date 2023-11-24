package com.ray3k.particleparkpro.undo;

/**
 * Undoable is an interface used by the UndoManager to allow the user to undo/redo actions in the interface. This
 * interface also provides the description when the user hovers over the undo/redo buttons.
 */
public interface Undoable {
    void undo();
    void redo();
    void start();
    String getDescription();
}
