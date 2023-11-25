package com.ray3k.gdxparticleeditor.shortcuts;

/**
 * An exception thrown when the user enters an invalid key combination.
 */
public class InvalidShortcutException extends RuntimeException {

    public InvalidShortcutException (String message) {
        super(message);
    }
}
