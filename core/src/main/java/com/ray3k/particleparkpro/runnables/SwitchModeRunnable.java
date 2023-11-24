package com.ray3k.particleparkpro.runnables;

import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.Settings;
import com.ray3k.particleparkpro.shortcuts.ShortcutManager;
import com.ray3k.particleparkpro.widgets.tables.ClassicTable;
import com.ray3k.particleparkpro.widgets.tables.WizardTable;

import static com.ray3k.particleparkpro.Core.root;
import static com.ray3k.particleparkpro.Core.shortcutManager;
import static com.ray3k.particleparkpro.PresetActions.transition;

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
