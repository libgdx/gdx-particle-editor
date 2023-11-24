package com.ray3k.particleparkpro.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.particleparkpro.FileDialogs;
import com.ray3k.particleparkpro.Settings;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.stripe.PopTable;
import regexodus.Pattern;
import regexodus.REFlags;

import static com.ray3k.particleparkpro.Core.skin;
import static com.ray3k.particleparkpro.Core.stage;
import static com.ray3k.particleparkpro.Listeners.addHandListener;
import static com.ray3k.particleparkpro.Listeners.onChange;

public class PopLocateImages extends PopTable {
    private Array<String> imagePaths = new Array<>();
    private ObjectMap<String, FileHandle> newFileHandles = new ObjectMap<>();
    private FileHandle particleFileHandle;
    private boolean merge;

    public PopLocateImages(FileHandle particleFileHandle, boolean merge) {
        super(skin.get(WindowStyle.class));

        this.particleFileHandle = particleFileHandle;
        this.merge = merge;

        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);

        var pattern = new Pattern("^.*-\\n");
        pattern.setFlags(REFlags.DOTALL);

        var text = particleFileHandle.readString(null);
        var matcher = pattern.matcher(text);
        text = matcher.replaceAll("");

        pattern = new Pattern("\\n*$");
        matcher = pattern.matcher(text);
        text = matcher.replaceAll("");

        imagePaths.addAll(text.split("\\n"));

        for (var imagePath : imagePaths) {
            var sibling = particleFileHandle.sibling(imagePath);
            if (sibling.exists()) newFileHandles.put(imagePath, sibling);
        }

        populate();
        addListener(new TableShowHideListener() {
            @Override
            public void tableShown(Event event) {

            }

            @Override
            public void tableHidden(Event event) {
                Gdx.input.setInputProcessor(stage);
                Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
            }
        });
    }

    private void populate() {
        var spacing = 10;
        var padding = 20;

        clearChildren();
        pad(20);
        defaults().space(spacing);

        var label = new Label("Locate Images:", skin, "bold");
        add(label);

        row();
        label = new Label("Locate the images below by clicking each entry.\nThe new images will be copied to the parent folder of the particle file.", skin);
        label.setAlignment(Align.center);
        add(label).padBottom(padding);

        row();
        var table = new Table();
        var scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).grow().padBottom(padding);

        label = new Label("Path", skin, "header");
        table.add(label);

        label = new Label("Found?", skin, "header");
        table.add(label);

        table.defaults().space(spacing).left();
        for (var imagePath : imagePaths) {
            table.row();
            var textButton = new TextButton(imagePath, skin, "no-bg");
            table.add(textButton);
            addHandListener(textButton);
            onChange(textButton, () -> {
                var sibling = particleFileHandle.sibling(imagePath);
                var fileHandle = FileDialogs.openDialog("Locate image " + sibling.name(), Settings.getDefaultImagePath(), new String[] {"png","jpg","jpeg"}, "Image files (*.png;*.jpg;*.jpeg)");
                if (fileHandle != null) {
                    newFileHandles.put(imagePath, fileHandle);
                    populate();
                }
            });

            var isFound = newFileHandles.containsKey(imagePath);
            label = new Label(isFound ? "yes" : "no", skin);
            table.add(label);
        }

        row();
        table = new Table();
        add(table);

        table.defaults().space(spacing).uniformX().fillX();
        var textButton = new TextButton(merge ? "Merge Particle Effect" : "Load Particle Effect", skin);
        textButton.setDisabled(imagePaths.size != newFileHandles.size);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            for (var imagePath : imagePaths) {
                var fileHandle = newFileHandles.get(imagePath);
                fileHandle.copyTo(particleFileHandle.parent());
            }
            if (merge) Utils.mergeParticle(particleFileHandle);
            else Utils.loadParticle(particleFileHandle);
            hide();
        });

        textButton = new TextButton("Cancel", skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, this::hide);
    }
}
