package com.ray3k.particleparkpro.shortcuts;

import static com.ray3k.particleparkpro.Utils.EMPTY_KEYBIND;

/**
 * A pair of integer arrays that hold a primary and secondary keybind for a shortcut.
 */
public class KeybindReference {

    private final int[] primaryKeybind;
    private final int[] secondaryKeybind;

    public KeybindReference (int[] primaryKeybind, int[] secondaryKeybind) {
        this.primaryKeybind = primaryKeybind;
        this.secondaryKeybind = secondaryKeybind;
    }

    public int[] getPrimaryKeybind() {
        return primaryKeybind != null ? primaryKeybind : EMPTY_KEYBIND;
    }

    public int[] getSecondaryKeybind() {
        return secondaryKeybind != null ? secondaryKeybind : EMPTY_KEYBIND;
    }

}
