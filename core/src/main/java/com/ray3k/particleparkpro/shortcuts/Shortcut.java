package com.ray3k.particleparkpro.shortcuts;

import java.util.Objects;

import static com.ray3k.particleparkpro.Utils.EMPTY_KEYBIND;

/**
 * A Shortcut consists of a primary and secondary keybind associated with a Runnable action. A name and description
 * are stored for display in the settings UI.
 */
public class Shortcut {

    private int[] primaryKeybind;
    private int[] secondaryKeybind;
    private int primaryKeybindPacked;
    private int secondaryKeybindPacked;
    private final Runnable runnable;
    private final String name;
    private final String description;
    private int scope;

    public Shortcut (String name, String description, Runnable runnable) {
       this.name = name;
       this.description = description;
       this.runnable = runnable;
    }

    public Shortcut setScope (int scope) {
        this.scope = scope;
        return this;
    }

    public int getScope() {
       return scope;
    }

    public Runnable getRunnable() {
         return runnable;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Shortcut setPrimaryKeybind(int[] keybind, int packed) {
        primaryKeybind = keybind;
        primaryKeybindPacked = packed;
        return this;
    }
    public int[] getPrimaryKeybind() {
        return primaryKeybind != null ? primaryKeybind : EMPTY_KEYBIND;
    }

    public int getPrimaryKeybindPacked () {
        return primaryKeybindPacked;
    }

    public Shortcut setSecondaryKeybind (int[] keybind, int packed) {
        secondaryKeybind = keybind;
        secondaryKeybindPacked = packed;
        return this;
    }

    public int[] getSecondaryKeybind() {
        return secondaryKeybind != null ? secondaryKeybind : EMPTY_KEYBIND;
    }

    public int getSecondaryKeybindPacked() {
        return secondaryKeybindPacked;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Shortcut shortcut = (Shortcut)o;
        return scope == shortcut.scope && runnable.equals(shortcut.runnable) && name.equals(shortcut.name) && description.equals(
            shortcut.description);
    }

    @Override
    public int hashCode () {
        return Objects.hash(runnable, name, description, scope);
    }

}
