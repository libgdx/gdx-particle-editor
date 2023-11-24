package com.ray3k.particleparkpro.shortcuts;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;

import static com.ray3k.particleparkpro.Utils.EMPTY_KEYBIND;

/**
 * Holds the currently added shortcuts.
 */
public class KeyMap {

    private final OrderedMap<String, Shortcut> allShortcuts;

    private final IntMap<Shortcut> activeShortcuts;

    public KeyMap () {
        allShortcuts = new OrderedMap<>();
        activeShortcuts = new IntMap<>();
    }

    public Array<Shortcut> getActiveShortcuts () {
        return activeShortcuts.values().toArray();
    }

    public Array<Shortcut> getAllShortcuts () {
        return allShortcuts.values().toArray();
    }

    public Shortcut getShortcut (int keybindPacked) {
        return activeShortcuts.get(keybindPacked);
    }

    public boolean hasKeybind (int keybindPacked) {
        return activeShortcuts.containsKey(keybindPacked);
    }

    public void addAll (Array<Shortcut> arr) {
        for (Shortcut s : arr) {
            add(s);
        }
    }

    public void add (Shortcut shortcut) {
        if (shortcut.getPrimaryKeybindPacked() > 0) {
            int[] unpacked = ShortcutManager.unpacKeybind(shortcut.getPrimaryKeybindPacked());
            isValidKeybind(unpacked);
            activeShortcuts.put(shortcut.getPrimaryKeybindPacked(), shortcut);
        } else if (shortcut.getPrimaryKeybind() != null && shortcut.getPrimaryKeybind().length > 0) {
            int[] sorted = ShortcutManager.sortKeybind(shortcut.getPrimaryKeybind());
            isValidKeybind(sorted);
            int packed = ShortcutManager.packKeybindSorted(sorted);
            activeShortcuts.put(packed, shortcut);
        }

        if (shortcut.getSecondaryKeybindPacked() > 0) {
            int[] unpacked = ShortcutManager.unpacKeybind(shortcut.getSecondaryKeybindPacked());
            isValidKeybind(unpacked);
            activeShortcuts.put(shortcut.getSecondaryKeybindPacked(), shortcut);
        } else if (shortcut.getSecondaryKeybind() != null && shortcut.getSecondaryKeybind().length > 0) {
            int[] sorted = ShortcutManager.sortKeybind(shortcut.getSecondaryKeybind());
            isValidKeybind(sorted);
            int packed = ShortcutManager.packKeybindSorted(sorted);
            activeShortcuts.put(packed, shortcut);
        }

        allShortcuts.put(shortcut.getName(), shortcut);
    }

    public void removeKeybind (Shortcut shortcut, boolean primary) {
        if (primary) {
            activeShortcuts.remove(shortcut.getPrimaryKeybindPacked());
            shortcut.setPrimaryKeybind(EMPTY_KEYBIND, 0);
        } else {
            activeShortcuts.remove(shortcut.getSecondaryKeybindPacked());
            shortcut.setSecondaryKeybind(EMPTY_KEYBIND, 0);
        }
    }

    public void changeKeybind (Shortcut shortcut, int[] newKeybind, boolean primary) {
        removeKeybind(shortcut, primary);

        isValidKeybind(newKeybind);

        int packed = ShortcutManager.packKeybindUnsorted(newKeybind);

        if (primary) {
            shortcut.setPrimaryKeybind(newKeybind, packed);
        } else {
            shortcut.setSecondaryKeybind(newKeybind, packed);
        }

        activeShortcuts.put(packed, shortcut);
    }

    public void changeKeybind (Shortcut shortcut, int packed, boolean primary) {
        removeKeybind(shortcut, primary);

        int[] unpackedKeybind = ShortcutManager.unpacKeybind(packed);

        isValidKeybind(unpackedKeybind);

        if (primary) {
            shortcut.setPrimaryKeybind(unpackedKeybind, packed);
        } else {
            shortcut.setSecondaryKeybind(unpackedKeybind, packed);
        }

        activeShortcuts.put(packed, shortcut);
    }

    public void changeAllKeybinds (ObjectMap<String, KeybindReference> keybinds) {
        Array<Shortcut> shortcuts = getAllShortcuts();
        for (Shortcut s : shortcuts) {
            KeybindReference ref = keybinds.get(s.getName());

            int[] keybind = ref.getPrimaryKeybind();
            if (keybind != null && keybind.length > 0) {
                changeKeybind(s, keybind, true);
            }

            keybind = ref.getSecondaryKeybind();
            if (keybind != null && keybind.length > 0) {
                changeKeybind(s, keybind, false);
            }
        }
    }

    // Only modifiers is invalid
    // Empty is invalid
    // Using a restricted keybind is invalid
    // Only one non modifier key is allowed
    private void isValidKeybind (int[] keys) {
        if (keys == null || keys.length == 0 || keys.length > ShortcutManager.MAX_KEYS) {
            throw new InvalidShortcutException(
                "Keybind must not be null and have a length between 0 and " + ShortcutManager.MAX_KEYS);
        }

        boolean allModifiers = true;
        boolean hasNormalKey = false;
        boolean hasShift = false;
        boolean hasAlt = false;
        boolean hasControl = false;

        for (int i = 0; i < keys.length; i++) {
            if (keys[i] <= 0)
                continue;

            // Treat left and right modifier keys the same
            switch (keys[i]) {
            case Keys.ALT_LEFT:
            case Keys.ALT_RIGHT:
            case Keys.SYM:
                if (hasAlt)
                    throw new InvalidShortcutException("Alt key already added.");
                hasAlt = true;
                break;
            case Keys.CONTROL_LEFT:
            case Keys.CONTROL_RIGHT:
                if (hasControl)
                    throw new InvalidShortcutException("Control key already added.");
                hasControl = true;
                break;
            case Keys.SHIFT_LEFT:
            case Keys.SHIFT_RIGHT:
                if (hasShift)
                    throw new InvalidShortcutException("Shift key already added.");
                hasShift = true;
                break;
            default:
                if (hasNormalKey)
                    throw new InvalidShortcutException("Keybind must have a maximum of 1 non modifier key");

                hasNormalKey = true;
                allModifiers = false;
            }
        }

        if (allModifiers)
            throw new InvalidShortcutException("All modifier keys are not allowed.");
    }
}
