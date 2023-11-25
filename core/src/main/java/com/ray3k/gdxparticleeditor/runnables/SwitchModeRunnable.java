package com.ray3k.gdxparticleeditor.runnables;

import com.badlogic.gdx.utils.Align;
import com.ray3k.gdxparticleeditor.Core;
import com.ray3k.gdxparticleeditor.Settings;
import com.ray3k.gdxparticleeditor.shortcuts.ShortcutManager;
import com.ray3k.gdxparticleeditor.widgets.tables.ClassicTable;
import com.ray3k.gdxparticleeditor.widgets.tables.WizardTable;

import static com.ray3k.gdxparticleeditor.Core.shortcutManager;
import static com.ray3k.gdxparticleeditor.PresetActions.transition;

public class SwitchModeRunnable implements Runnable {

    @Override
    public void run () {
        if (Core.openTable.equals("Wizard") && (ClassicTable.classicTable == null || ClassicTable.classicTable.getStage() == null)) {
            Core.openTable = "Classic";
            transition(WizardTable.wizardTable, new ClassicTable(), Align.top);
            shortcutManager.setDisabled(false);
            ShortcutManager.setScope(Settings.CLASSIC_SCOPE);
        } else if (Core.openTable.equals("Classic") && (WizardTable.wizardTable == null || WizardTable.wizardTable.getStage() == null)) {
            Core.openTable = "Wizard";
            transition(ClassicTable.classicTable, new WizardTable(), Align.top);
            shortcutManager.setDisabled(false);
            ShortcutManager.setScope(Settings.WIZARD_SCOPE);
        }
    }
}
