package com.ray3k.particleparkpro.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.*;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.SkinLoader;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.Utils.UIscale;
import com.ray3k.particleparkpro.shortcuts.Shortcut;
import com.ray3k.particleparkpro.shortcuts.ShortcutManager;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.StringBuilder;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.Settings.*;
import static com.ray3k.particleparkpro.Utils.changeKeybind;
import static com.ray3k.particleparkpro.Utils.clearKeybind;
import static com.ray3k.particleparkpro.widgets.styles.Styles.spinnerStyle;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomArrowStyle;

/**
 * PopTable used to change the app settings. These include options for the UndoManager, default screen, checking for updates, UI
 * scale, keyboard shortcuts, and more. Links to related directories in the user folder are provided for convenience.
 */
public class PopEditorSettings extends PopTable {
    private static final Array<TextField> textFields = new Array<>();
    private UIscale uiScale;
    private KeybindResolverInfoLookup keybindResolverInfoLookup;
    private IntMap<KeybindResolver> keybindResolvers;

    public PopEditorSettings () {
        super(skin.get(WindowStyle.class));
        keybindResolverInfoLookup = new KeybindResolverInfoLookup();
        keybindResolvers = new IntMap<>();
        populate();
    }

    private void populate () {
        textFields.clear();
        clearChildren();

        pad(20).padTop(10);
        setHideOnUnfocus(true);
        setKeepCenteredInWindow(true);
        addListener(new TableShowHideListener() {
            @Override
            public void tableShown (Event event) {

            }

            @Override
            public void tableHidden (Event event) {
                Gdx.input.setInputProcessor(stage);
            }
        });

        var label = new Label("SETTINGS", skin, "bold");
        add(label).padBottom(10);

        //misc settings
        row();
        var settingsTable = new Table();
        settingsTable.columnDefaults(0).right().uniformX();
        settingsTable.columnDefaults(1).left().uniformX().width(80);
        settingsTable.defaults().space(5);
        add(settingsTable);

        label = new Label("Maximum Undos:", skin);
        settingsTable.add(label);

        var spinner = new Spinner(0, 1, 0, Orientation.RIGHT_STACK, spinnerStyle);
        spinner.setValue(preferences.getInteger(NAME_MAXIMUM_UNDOS, DEFAULT_MAXIMUM_UNDOS));
        spinner.setProgrammaticChangeEvents(false);
        settingsTable.add(spinner);
        addIbeamListener(spinner.getTextField());
        addHandListener(spinner.getButtonMinus());
        addHandListener(spinner.getButtonPlus());
        addTooltip(spinner, "The maximum number of undos that will be kept in memory", Align.top, Align.top,
            tooltipBottomArrowStyle);
        onChange(spinner, () -> {
            preferences.putInteger(NAME_MAXIMUM_UNDOS, spinner.getValueAsInt());
            preferences.flush();
        });

        settingsTable.row();
        label = new Label("Open to screen:", skin);
        settingsTable.add(label);

        var selectBox = new SelectBox<String>(skin);
        selectBox.setItems("Welcome", "Classic", "Wizard");
        selectBox.setSelected(preferences.getString(NAME_OPEN_TO_SCREEN, DEFAULT_OPEN_TO_SCREEN));
        settingsTable.add(selectBox);
        addHandListener(selectBox);
        addHandListener(selectBox.getList());
        addTooltip(selectBox, "The default screen that the app opens to", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(selectBox, () -> {
            preferences.putString(NAME_OPEN_TO_SCREEN, selectBox.getSelected());
            preferences.flush();
        });

        //checkboxes
        settingsTable.row();
        var checkBoxTable = new Table();
        checkBoxTable.defaults().left().space(5);
        settingsTable.add(checkBoxTable).colspan(2).center();

        var checkForUpdatesCheckBox = new CheckBox("Check for updates", skin);
        checkForUpdatesCheckBox.setChecked(preferences.getBoolean(NAME_CHECK_FOR_UPDATES, DEFAULT_CHECK_FOR_UPDATES));
        checkBoxTable.add(checkForUpdatesCheckBox);
        addHandListener(checkForUpdatesCheckBox);
        addTooltip(checkForUpdatesCheckBox, "Whether or not the app checks to see if there is an update available at startup",
            Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(checkForUpdatesCheckBox, () -> {
            preferences.putBoolean(NAME_CHECK_FOR_UPDATES, checkForUpdatesCheckBox.isChecked());
            preferences.flush();
        });

        checkBoxTable.row();
        var presumeFileExtensionCheckBox = new CheckBox("Presume file extension is .p", skin);
        presumeFileExtensionCheckBox.setChecked(
            preferences.getBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION));
        checkBoxTable.add(presumeFileExtensionCheckBox);
        addHandListener(presumeFileExtensionCheckBox);
        addTooltip(presumeFileExtensionCheckBox,
            "Whether or not the app defaults all particle file dialogs to use the \".p\" file extension", Align.top, Align.top,
            tooltipBottomArrowStyle);
        onChange(presumeFileExtensionCheckBox, () -> {
            preferences.putBoolean(NAME_PRESUME_FILE_EXTENSION, presumeFileExtensionCheckBox.isChecked());
            preferences.flush();
        });

        //sliders
        settingsTable.row();
        var sliderTable = new Table();
        sliderTable.defaults().space(5);
        settingsTable.add(sliderTable).colspan(2).center();

        label = new Label("UI Scale:", skin);
        sliderTable.add(label);

        var slider = new Slider(0, 4, 1, false, skin);
        uiScale = Utils.valueToUIscale(preferences.getFloat(NAME_SCALE, DEFAULT_SCALE));
        var scaleArray = new Array<>(Utils.UIscale.values());
        slider.setValue(scaleArray.indexOf(uiScale, true));
        sliderTable.add(slider).width(80);
        addHandListener(slider);
        addTooltip(slider, "Increase the UI Scale for high DPI displays", Align.top, Align.top, tooltipBottomArrowStyle);

        var scaleLabel = new Label(uiScale.text, skin);
        sliderTable.add(scaleLabel).padRight(5).width(20);
        onChange(slider, () -> {
            var index = MathUtils.round(slider.getValue());
            uiScale = Utils.UIscale.values()[index];
            scaleLabel.setText(uiScale.text);
        });

        var textButton = new TextButton("Apply", skin);
        sliderTable.add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, "Apply the UI Scale for high DPI displays", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(textButton, () -> {
            Utils.updateViewportScale(uiScale);
            showConfirmScalePop();
        });

        // Shortcuts
        row();

        var shortcutTable = new Table();
        shortcutTable.columnDefaults(0).right().uniformX();
        shortcutTable.columnDefaults(1).width(90);
        shortcutTable.columnDefaults(2).width(90);
        shortcutTable.columnDefaults(3).uniformX();
        shortcutTable.defaults().space(5);
        add(shortcutTable).padTop(20);

        label = new Label("SHORTCUTS", skin, "header");
        shortcutTable.add(label).colspan(4).align(Align.center);

        shortcutTable.row();
        shortcutTable.add();

        label = new Label("Primary:", skin);
        label.setAlignment(Align.center);
        shortcutTable.add(label);

        label = new Label("Secondary:", skin);
        label.setAlignment(Align.center);
        shortcutTable.add(label);

        shortcutTable.add();

        for (Shortcut s : keyMap.getAllShortcuts()) {
            createShortcutSettingsOption(s, shortcutTable);
        }

        //buttons
        row();
        var buttonTable = new Table();
        buttonTable.defaults().space(5).uniformX().fillX();
        add(buttonTable).padTop(20);

        var subButton = new TextButton("Open Preferences Directory", skin);
        buttonTable.add(subButton);
        addHandListener(subButton);
        addTooltip(subButton, "Open the preferences directory where Particle Park Pro saves its settings", Align.top, Align.top,
            tooltipBottomArrowStyle);
        onChange(subButton, () -> {
            try {
                openFileExplorer(Gdx.files.external(".prefs/"));
            } catch (IOException e) {
                var error = "Error opening preferences directory.";
                var pop = new PopError(error, e.getMessage());
                pop.show(stage);

                Gdx.app.error(Core.class.getName(), error, e);
            }
        });

        buttonTable.row();
        subButton = new TextButton("Open Log Directory", skin);
        buttonTable.add(subButton);
        addHandListener(subButton);
        addTooltip(subButton, "Open the log directory where Particle Park Pro saves errors", Align.top, Align.top,
            tooltipBottomArrowStyle);
        onChange(subButton, () -> {
            try {
                openFileExplorer(Gdx.files.external(".particleparkpro/"));
            } catch (IOException e) {
                var error = "Error opening log directory.";
                var pop = new PopError(error, e.getMessage());
                pop.show(stage);

                Gdx.app.error(Core.class.getName(), error, e);
            }
        });

        buttonTable.row();
        subButton = new TextButton("Reset to Defaults", skin);
        buttonTable.add(subButton);
        addHandListener(subButton);
        addTooltip(subButton, "Reset all settings to their defaults", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(subButton, this::resetSettingsToDefaults);

        buttonTable.row();
        subButton = new TextButton("Open GitHub Page", skin);
        buttonTable.add(subButton);
        addHandListener(subButton);
        addTooltip(subButton, "Open the GitHub page for Particle Park Pro", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(subButton, () -> {
            Gdx.net.openURI("https://github.com/raeleus/Particle-Park-Pro");
        });

        buttonTable.row();
        subButton = new TextButton("Close", skin);
        buttonTable.add(subButton).padTop(20);
        addHandListener(subButton);
        addTooltip(subButton, "Close the settings dialog.", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(subButton, this::hide);
    }

    int confirmTime;

    private void showConfirmScalePop () {
        var pop = new PopTable(skin.get("confirm-scale", WindowStyle.class));
        pop.setModal(true);

        confirmTime = 5;

        var label = new Label("", skin, "confirm-scale");
        label.setAlignment(Align.center);
        pop.add(label).size(200, 100);
        label.addAction(Actions.sequence(Actions.repeat(confirmTime, Actions.sequence(
            Actions.run(() -> label.setText("Click to confirm scale.\nResetting in " + (--confirmTime + 1) + "...")),
            Actions.delay(1f))), Actions.run(() -> {
            pop.hide();
            label.remove();
            uiScale = Utils.valueToUIscale(preferences.getFloat(NAME_SCALE, DEFAULT_SCALE));
            Utils.updateViewportScale(uiScale);
            populate();
        })));

        pop.show(foregroundStage);

        onClick(pop.getParentGroup(), () -> {
            preferences.putFloat(NAME_SCALE, uiScale.multiplier);
            preferences.flush();
            label.remove();
            pop.hide();
            SkinLoader.loadSkin();
            Core.populate(openTable);
        });

    }

    private void createShortcutSettingsOption (Shortcut shortcut, Table shortcutTable) {
        shortcutTable.row();

        var label = new Label(shortcut.getName() + ":", skin);
        shortcutTable.add(label);

        var primaryTextField = new TextField("", skin);
        primaryTextField.setText(constructShortcutText(shortcut.getPrimaryKeybind()));
        shortcutTable.add(primaryTextField);
        textFields.add(primaryTextField);
        addHandListener(primaryTextField);
        addTooltip(primaryTextField, shortcut.getDescription(), Align.top, Align.top, tooltipBottomArrowStyle);
        onTouchDown(primaryTextField, () -> {
            getStage().setKeyboardFocus(null);
            showKeyBindPop(primaryTextField, shortcut, getStage(), true);
        });

        var secondaryTextField = new TextField("", skin);
        secondaryTextField.setText(constructShortcutText(shortcut.getSecondaryKeybind()));
        shortcutTable.add(secondaryTextField);
        textFields.add(secondaryTextField);
        addHandListener(secondaryTextField);
        addTooltip(secondaryTextField, shortcut.getDescription(), Align.top, Align.top, tooltipBottomArrowStyle);
        onTouchDown(secondaryTextField, () -> {
            getStage().setKeyboardFocus(null);
            showKeyBindPop(secondaryTextField, shortcut, getStage(), false);
        });
    }

    public static void openFileExplorer (FileHandle startDirectory) throws IOException {
        if (startDirectory.exists()) {
            File file = startDirectory.file();
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } else {
            throw new IOException("Directory doesn't exist: " + startDirectory.path());
        }
    }

    private static String constructShortcutText (int[] modifiers, int shortcut) {
        if (shortcut == Keys.ANY_KEY)
            return "";

        var stringBuilder = new StringBuilder();
        for (var modifier : modifiers) {
            var text = Keys.toString(modifier);
            if (text.startsWith("L-"))
                text = text.substring(2);
            stringBuilder.append(text);
            stringBuilder.append("+");
        }
        stringBuilder.append(Keys.toString(shortcut));
        return stringBuilder.toString();
    }

    private String constructShortcutText (int[] keybind) {
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < keybind.length; i++) {
            int keycode = keybind[i];

            if (keycode <= 0)
                continue;

            var text = Keys.toString(keybind[i]);
            if (text.startsWith("L-"))
                text = text.substring(2);
            if (text.equals("Alt") && UIUtils.isMac) {
                text = "Cmd";
            }
            stringBuilder.append(text);
            if (i < keybind.length - 1)
                stringBuilder.append(" + ");
        }
        return stringBuilder.toString();
    }

    public void resetSettingsToDefaults () {
        resetKeybinds(false);
        keyMap.changeAllKeybinds(DEFAULT_KEYBINDS);
        preferences.putInteger(NAME_MAXIMUM_UNDOS, DEFAULT_MAXIMUM_UNDOS);
        preferences.putString(NAME_OPEN_TO_SCREEN, DEFAULT_OPEN_TO_SCREEN);
        preferences.putBoolean(NAME_CHECK_FOR_UPDATES, DEFAULT_CHECK_FOR_UPDATES);
        preferences.putBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION);
        preferences.flush();
        populate();
    }

    private void setShortcutTextFieldText (TextField textField, String modifiersKey, String shortcutKey, int[] defaultModifiers,
        int defaultShortcut) {
        if (preferences.contains(modifiersKey) && preferences.contains(shortcutKey)) {
            textField.setText(constructShortcutText(readModifierText(preferences.getString(modifiersKey)),
                preferences.getInteger(shortcutKey)));
        } else {
            textField.setText(constructShortcutText(defaultModifiers, defaultShortcut));
        }
    }

    private void showKeyBindPop (TextField textField, Shortcut shortcut, Stage stage, boolean primary) {
        var pop = new PopTable(skin.get("key-bind", WindowStyle.class));
        pop.setHideOnUnfocus(true);

        var label = new Label("Press a key combination for\n" + shortcut.getName() + "\nPress ESCAPE to clear", skin, "bold");
        label.setAlignment(Align.center);
        pop.add(label);
        label.addListener(new InputListener() {
            @Override
            public boolean keyDown (InputEvent event, int keycode) {
                if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT)
                    return false;
                if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT)
                    return false;
                if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT)
                    return false;
                if (keycode == Keys.SYM)
                    return false;

                var intArray = new IntArray();
                if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT))
                    intArray.add(Keys.CONTROL_LEFT);
                if (Gdx.input.isKeyPressed(Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Keys.ALT_RIGHT))
                    intArray.add(Keys.ALT_LEFT);
                if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))
                    intArray.add(Keys.SHIFT_LEFT);
                if (Gdx.input.isKeyPressed(Keys.SYM))
                    intArray.add(Keys.SYM);

                boolean clearShortcut = keycode == Keys.ESCAPE;

                if (clearShortcut) {
                    textField.setText("");
                    clearKeybind(keyMap, shortcut, primary, true);

                    int key = keybindResolverInfoLookup.getKey(shortcut, primary);

                    if (key != -1) {
                              KeybindResolver r = keybindResolvers.get(key);
                        if (r.removeConflict(shortcut, primary)) {
                            if (primary) {
                                keybindResolvers.remove(shortcut.getPrimaryKeybindPacked());
                            } else {
                                keybindResolvers.remove(shortcut.getSecondaryKeybindPacked());
                            }
                        }
                        keybindResolverInfoLookup.remove(shortcut, primary);
                    }
                } else {
                    intArray.add(keycode);
                    int[] keybind = ShortcutManager.sortKeybind(intArray.toArray());
                    int packed = ShortcutManager.packKeybindSorted(keybind);
                    textField.setText(constructShortcutText(keybind));

                    keybindResolverInfoLookup.remove(shortcut, primary);

                    if (keyMap.hasKeybind(packed)) {
                        KeybindResolver r = keybindResolvers.get(packed);

                        if (r == null) {
                            r = new KeybindResolver(packed);
                            keybindResolvers.put(packed, r);

                            // Add shortcut holding the keybind
                            Shortcut s = keyMap.getShortcut(packed);
                            if (s.getPrimaryKeybindPacked() == packed) {
                                r.addConflict(s, true);
                                keybindResolverInfoLookup.put(s, packed, true);
                            } else {
                                r.addConflict(s, false);
                                keybindResolverInfoLookup.put(s, packed, false);
                            }
                        }

                        r.addConflict(shortcut, primary);
                        keybindResolverInfoLookup.put(shortcut, packed, primary);
                    } else {
                        changeKeybind(keyMap, shortcut, keybind, primary, true);
                    }
                }

                pop.hide();
                updateDuplicateKeybindUI();
                return false;
            }
        });

        pop.show(stage);
        stage.setKeyboardFocus(label);
    }

    private void updateDuplicateKeybindUI () {
        var texts = new Array<String>();
        for (int i = 0; i < textFields.size; i++) {
            var textField = textFields.get(i);
            var text = textField.getText();

            var index = texts.indexOf(text, false);
            if (!text.equals("") && index != -1) {
                textFields.get(index).setColor(Color.RED);
                textField.setColor(Color.RED);
            } else {
                textField.setColor(Color.WHITE);
            }

            texts.add(text);
        }
    }

    private class KeybindResolver {

        private final int packedConflictKeybind;
        private final Array<KeybindResolverInfo> conflictShortcuts;

        KeybindResolver (int keybind) {
            packedConflictKeybind = keybind;
            conflictShortcuts = new Array<>();
        }

        public boolean containsConflict (Shortcut s, boolean primary) {
            for (int i = 0; i < conflictShortcuts.size; i++) {
                KeybindResolverInfo info = conflictShortcuts.get(i);

                if (info.shortcut.equals(s) && info.primary == primary) {
                    return true;
                }
            }
            return false;
        }

        KeybindResolverInfo addConflict (Shortcut s, boolean primary) {
            KeybindResolverInfo info = new KeybindResolverInfo(s, primary);
            conflictShortcuts.add(info);
            return info;
        }

        boolean removeConflict (Shortcut s, boolean primary) {
            for (int i = conflictShortcuts.size - 1; i >= 0; i--) {
                KeybindResolverInfo info = conflictShortcuts.get(i);

                if (info.shortcut.equals(s) && info.primary == primary) {
                    conflictShortcuts.removeIndex(i);
                    break;
                }
            }

            if (conflictShortcuts.size == 1) {
                KeybindResolverInfo info = conflictShortcuts.peek();
                changeKeybind(keyMap, info.shortcut, packedConflictKeybind, info.primary, true);
                return true;
            }
            return false;
        }
    }

    private class KeybindResolverInfoLookup {

        private ObjectLongMap<Shortcut> lookup;

        KeybindResolverInfoLookup () {
            lookup = new ObjectLongMap<>();
        }

        long pack (int primary, int secondary) {
            return (((long)primary) << 32 | (secondary & 0xffffffffL));
        }

        long packPrimary (long num, int primary) {
            int sec = (int)num;
            return pack(primary, sec);
        }

        long packSecondary (long num, int secondary) {
            int pri = (int)(num >> 32);
            return pack(pri, secondary);
        }

        int getPrimary (long num) {
            return (int)(num >> 32);
        }

        int getSecondary (long num) {
            return (int)num;
        }

        void put (Shortcut s, int v, boolean primary) {
            long key = lookup.get(s, -1);

            if (key == -1) {
                key = 0;
            }

            key = primary ? packPrimary(key, v) : packSecondary(key, v);
            lookup.put(s, key);
        }

        void remove(Shortcut s, boolean primary) {
           long key = lookup.get(s, -1);

           if (key == -1) return;

           key = primary ? packPrimary(key, 0) : packSecondary(key, 0);

           int primaryKeybindPacked = getPrimary(key);
           int secondaryKeybindPacked = getSecondary(key);

           if (primaryKeybindPacked == 0 && secondaryKeybindPacked == 0) {
              lookup.remove(s, -1);
           } else {
               lookup.put(s, key);
           }
        }

        int getKey (Shortcut s, boolean primary) {
            long result = lookup.get(s, -1);
            if (result == -1) return -1;
            return primary ? getPrimary(result) : getSecondary(result);
        }

    }

    private class KeybindResolverInfo {

        private boolean primary;
        private Shortcut shortcut;

        KeybindResolverInfo (Shortcut shortcut, boolean primary) {
            this.shortcut = shortcut;
            this.primary = primary;
        }
    }
}
