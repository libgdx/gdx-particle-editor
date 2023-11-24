package com.ray3k.particleparkpro.widgets.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.Settings;
import com.ray3k.particleparkpro.widgets.WelcomeCard;
import com.ray3k.particleparkpro.widgets.poptables.PopEditorSettings;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.PresetActions.transition;
import static com.ray3k.particleparkpro.Settings.DEFAULT_OPEN_TO_SCREEN;
import static com.ray3k.particleparkpro.Settings.NAME_OPEN_TO_SCREEN;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomRightArrowStyle;

/**
 * The introductory table which highlights the app title and buttons to open the classic or wizard modes.
 */
public class WelcomeTable extends Table {
    private Button settingsButton;

    public WelcomeTable() {
        shortcutManager.setDisabled(true);
        var stack = new Stack();
        add(stack).grow();

        var root = new Table();
        stack.add(root);

        var table = new Table();
        root.add(table);

        var image = new Image(skin, "title");
        table.add(image);

        table.row();
        var label = new Label(version, skin);
        table.add(label).expandX().right().padRight(10).padTop(5);

        root.row();
        table = new Table();
        root.add(table).spaceTop(100);

        table.row().spaceRight(70);
        var classicCard = new WelcomeCard("Classic", "The classic Particle Editor experience", skin.getDrawable("thumb-classic"), "Open Classic Mode");
        table.add(classicCard);
        addHandListener(classicCard);

        var wizardCard = new WelcomeCard("Wizard", "Simple with a large preview", skin.getDrawable("thumb-wizard"), "Open Wizard Mode");
        table.add(wizardCard);
        addHandListener(wizardCard);

        table.row();
        var checkbox = new CheckBox("Remember my choice", skin);
        checkbox.setChecked(!preferences.getString(NAME_OPEN_TO_SCREEN, DEFAULT_OPEN_TO_SCREEN).equals(DEFAULT_OPEN_TO_SCREEN));
        table.add(checkbox).space(25).right().colspan(2);
        addHandListener(checkbox);
        onChange(checkbox, () -> {
            if (!checkbox.isChecked()) {
                preferences.putString(NAME_OPEN_TO_SCREEN, "Welcome");
                preferences.flush();
            }
        });

        root = new Table();
        stack.add(root);

        settingsButton = new Button(skin, "settings");
        root.add(settingsButton).expand().bottom().right().padRight(20).padBottom(10);
        addHandListener(settingsButton);
        addTooltip(settingsButton, "Open the Editor Settings dialog", Align.top, Align.topLeft, tooltipBottomRightArrowStyle, false);
        onChange(settingsButton, () -> {
            Gdx.input.setInputProcessor(foregroundStage);
            Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
            var pop = new PopEditorSettings();
            pop.show(foregroundStage);
        });

        onChange(classicCard, () -> {
            if (checkbox.isChecked()) {
                preferences.putString(NAME_OPEN_TO_SCREEN, "Classic");
                preferences.flush();
            }
            Core.openTable = "Classic";
            transition(this, new ClassicTable(), Align.top);
            shortcutManager.setDisabled(false);
            shortcutManager.setScope(Settings.CLASSIC_SCOPE);
            fadeSettingsButton();
        });

        onChange(wizardCard, () -> {
            if (checkbox.isChecked()) {
                preferences.putString(NAME_OPEN_TO_SCREEN, "Wizard");
                preferences.flush();
            }
            Core.openTable = "Wizard";
            transition(this, new WizardTable(), Align.top);
            shortcutManager.setDisabled(false);
            shortcutManager.setScope(Settings.WIZARD_SCOPE);
            fadeSettingsButton();
        });
    }

    private void fadeSettingsButton() {
        settingsButton.addAction(Actions.fadeOut(.3f));
    }
}
