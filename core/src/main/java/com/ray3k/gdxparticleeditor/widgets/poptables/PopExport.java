package com.ray3k.gdxparticleeditor.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.gdxparticleeditor.FileDialogs;
import com.ray3k.gdxparticleeditor.Settings;
import com.ray3k.gdxparticleeditor.runnables.ExportRunnable;
import com.ray3k.gdxparticleeditor.widgets.LeadingTruncateLabel;
import com.ray3k.gdxparticleeditor.widgets.styles.Styles;
import com.ray3k.stripe.PopTable;

import static com.ray3k.gdxparticleeditor.Core.*;
import static com.ray3k.gdxparticleeditor.Listeners.*;
import static com.ray3k.gdxparticleeditor.Settings.*;

/**
 * PopTable used to export a particle effect. This enables the user to save a copy of the file without affecting the
 * save location of the original particle effect. An option to include the images bypasses the option in the settings.
 */
public class PopExport extends PopTable {
    private final InputProcessor previousInputProcessor;
    private CheckBox exportImagesCheckBox;
    private FileHandle fileHandle;
    private TextButton exportTextButton;

    public PopExport() {
        super(skin.get(WindowStyle.class));

        setHideOnUnfocus(true);
        key(Keys.ESCAPE, this::hide);
        key(Keys.ENTER, this::save);
        key(Keys.NUMPAD_ENTER, this::save);

        previousInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(foregroundStage);

        addListener(new TableShowHideListener() {
            @Override
            public void tableShown(Event event) {
                hideAllTooltips();
            }

            @Override
            public void tableHidden(Event event) {
                if (Gdx.input.getInputProcessor() == foregroundStage) Gdx.input.setInputProcessor(previousInputProcessor);
            }
        });
        populate();
    }

    private void populate() {
        clearChildren();
        pad(20);
        defaults().space(10);

        var label = new Label("Export", skin, "bold");
        add(label);

        row();
        label = new Label("Save a copy to the selected path.", skin);
        add(label);

        row();
        defaults().left();
        var table = new Table();
        add(table);

        table.defaults().space(5);
        label = new Label("Save Path:", skin);
        table.add(label);

        fileHandle = Gdx.files.absolute(getDefaultExportPath());

        var disabled = fileHandle.isDirectory() || !fileHandle.parent().exists();
        var leadingTruncateLabel = new LeadingTruncateLabel(disabled ? "" : fileHandle.path(), skin, "textfield");
        leadingTruncateLabel.setEllipsis("...");
        table.add(leadingTruncateLabel).width(250);
        addHandListenerIgnoreDisabled(leadingTruncateLabel);
        if (!disabled) addTooltip(leadingTruncateLabel, fileHandle.path(), Align.top, Align.top, Styles.tooltipBottomArrowStyle, true);
        onClick(leadingTruncateLabel, () -> {
            var useFileExtension = preferences.getBoolean(NAME_PRESUME_FILE_EXTENSION, DEFAULT_PRESUME_FILE_EXTENSION);
            var filterPatterns = useFileExtension ? new String[] {"p"} : null;
            var f = FileDialogs.saveDialog("Export File...", fileHandle.isDirectory() ? fileHandle.path() : fileHandle.parent().path(),  defaultFileName, filterPatterns, "Particle Files (*.p)");
            if (f != null) {
                fileHandle = f;
                setDefaultExportPath(fileHandle);
                leadingTruncateLabel.setText(fileHandle.path());
                exportTextButton.setDisabled(false);
            }
        });

        row();
        exportImagesCheckBox = new CheckBox("Export images", skin);
        exportImagesCheckBox.setChecked(preferences.getBoolean(Settings.NAME_EXPORT_IMAGES));
        add(exportImagesCheckBox);
        addHandListener(exportImagesCheckBox);

        defaults().center();
        row();
        table = new Table();
        table.defaults().uniformX().fillX().space(10);
        add(table);

        exportTextButton = new TextButton("Export", skin, "highlighted");
        exportTextButton.setDisabled(disabled);
        table.add(exportTextButton);
        addHandListener(exportTextButton);
        onChange(exportTextButton, this::save);

        var textButton = new TextButton("Cancel", skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, this::hide);
    }

    private void save() {
        if (fileHandle == null) return;
        hide();

        var exportRunnable = new ExportRunnable();
        exportRunnable.setExportImages(exportImagesCheckBox.isChecked());
        exportRunnable.setExportFileHandle(fileHandle);
        exportRunnable.setOnCompletionRunnable(() -> hide());
        exportRunnable.run();
    }
}
