package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpriteMode;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.ImagesAddUndoable;
import com.ray3k.particleparkpro.undo.undoables.ImagesMoveUndoable;
import com.ray3k.particleparkpro.undo.undoables.ImagesRemoveUndoable;
import com.ray3k.particleparkpro.undo.undoables.ImagesSpriteModeUndoable;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.stripe.DraggableTextList;
import com.ray3k.stripe.DraggableTextList.DraggableTextListListener;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.draggableTextListNoBgStyle;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomArrowStyle;

/**
 * A widget that allows the user to add images to the particle emitter. Default image options are available. Also allows
 * the user to specify the sprite mode.
 */
public class ImagesSubPanel extends Panel {
    public static ImagesSubPanel imagesSubPanel;
    private DraggableTextList list;
    private Button removeButton;
    private Button moveUpButton;
    private Button moveDownButton;

    public ImagesSubPanel() {
        imagesSubPanel = this;

        var listWidth = 210;
        var listHeight = 100;

        for (int i = 0; i < selectedEmitter.getImagePaths().size; i++) {
            var path = selectedEmitter.getImagePaths().get(i);
            var sprite = selectedEmitter.getSprites().get(i);
            sprites.put(path, sprite);
        }

        setTouchable(Touchable.enabled);

        tabTable.left();
        var label = new Label("Images", skin, "header");
        tabTable.add(label);

        bodyTable.defaults().space(15);
        bodyTable.left();
        var table = new Table();
        bodyTable.add(table);

        //Add
        table.defaults().space(5).fillX();
        var textButton = new TextButton("Add", skin, "small");
        table.add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, "Add an image to be used as the texture for the particle", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(textButton, imagesRunnable);

        //Default
        table.row();
        textButton = new TextButton("Default", skin, "small");
        table.add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, "Set the image to the default round-faded image", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(textButton, () -> {
            var selectedFileHandles = new Array<FileHandle>();
            selectedFileHandles.add(Gdx.files.internal("particle.png"));
            UndoManager.add(new ImagesAddUndoable(selectedEmitter, selectedFileHandles, "Add Default Image"));
            updateList();
            updateDisabled();
        });

        //Default PMA
        table.row();
        textButton = new TextButton("Default PMA", skin, "small");
        table.add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, "Set the image to the default for premultiplied alpha", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(textButton, () -> {
            var selectedFileHandles = new Array<FileHandle>();
            selectedFileHandles.add(Gdx.files.internal("pre_particle.png"));
            UndoManager.add(new ImagesAddUndoable(selectedEmitter, selectedFileHandles, "Add Default Image"));
            updateList();
            updateDisabled();
        });

        table = new Table();
        bodyTable.add(table);

        //Sprite Mode
        label = new Label("Sprite mode:", skin);
        table.add(label);

        var selectedSpriteMode = selectedEmitter.getSpriteMode();

        table.row();
        table.defaults().left();
        var buttonGroup = new ButtonGroup<>();
        var checkBoxSingle = new CheckBox("Single", skin, "radio");
        checkBoxSingle.setProgrammaticChangeEvents(false);
        if (selectedSpriteMode == SpriteMode.single) checkBoxSingle.setChecked(true);
        table.add(checkBoxSingle).spaceTop(5);
        buttonGroup.add(checkBoxSingle);
        addHandListener(checkBoxSingle);
        addTooltip(checkBoxSingle, "Only the selected image will be drawn", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(checkBoxSingle, () -> UndoManager.add(new ImagesSpriteModeUndoable(selectedEmitter, SpriteMode.single, selectedEmitter.getSpriteMode(), "change Image Sprite Mode")));

        table.row();
        var checkBoxRandom = new CheckBox("Random", skin, "radio");
        checkBoxRandom.setProgrammaticChangeEvents(false);
        if (selectedSpriteMode == SpriteMode.random) checkBoxRandom.setChecked(true);
        table.add(checkBoxRandom);
        buttonGroup.add(checkBoxRandom);
        addHandListener(checkBoxRandom);
        addTooltip(checkBoxRandom, "A randomly selected image will be chosen for each particle", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(checkBoxRandom, () -> UndoManager.add(new ImagesSpriteModeUndoable(selectedEmitter, SpriteMode.random, selectedEmitter.getSpriteMode(), "change Image Sprite Mode")));

        table.row();
        var checkBoxAnimated = new CheckBox("Animated", skin, "radio");
        checkBoxAnimated.setProgrammaticChangeEvents(false);
        if (selectedSpriteMode == SpriteMode.animated) checkBoxAnimated.setChecked(true);
        table.add(checkBoxAnimated);
        buttonGroup.add(checkBoxAnimated);
        addHandListener(checkBoxAnimated);
        addTooltip(checkBoxAnimated, "All images will be displayed in sequence over the life of each particle", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(checkBoxAnimated, () -> UndoManager.add(new ImagesSpriteModeUndoable(selectedEmitter, SpriteMode.animated, selectedEmitter.getSpriteMode(), "change Image Sprite Mode")));

        //draggable text list
        list = new DraggableTextList(true, draggableTextListNoBgStyle);
        list.setProgrammaticChangeEvents(false);
        list.setTextAlignment(Align.left);
        list.align(Align.top);
        addHandListener(list);
        list.addListener(new DraggableTextListListener() {
            @Override
            public void removed(String path, int index) {
                list.setAllowRemoval(list.getTexts().size > 1);
                removeButton.setDisabled(!list.isAllowRemoval());

                UndoManager.add(new ImagesRemoveUndoable(selectedEmitter, path, fileHandles.get(path), sprites.get(path), "Remove Image"));
                updateDisabled();
            }

            @Override
            public void reordered(String text, int indexBefore, int indexAfter) {
                UndoManager.add(new ImagesMoveUndoable(selectedEmitter, indexBefore, indexAfter, "Move Image"));
                updateDisabled();
            }

            @Override
            public void selected(String text) {
                updateDisabled();
            }
        });

        var scrollPane = new ScrollPane(list, skin, "draggable-list");
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        bodyTable.add(scrollPane).size(listWidth, listHeight).spaceRight(10);
        addScrollFocusListener(scrollPane);

        table = new Table();
        bodyTable.add(table).spaceLeft(10);

        //move up
        table.defaults().space(5);
        moveUpButton = new Button(skin, "moveup");
        table.add(moveUpButton);
        addHandListener(moveUpButton);
        onChange(moveUpButton, () -> {
            var paths = selectedEmitter.getImagePaths();
            var index = list.getSelectedIndex();
            if (index > 0) {
                UndoManager.add(new ImagesMoveUndoable(selectedEmitter, index, --index, "Move Image"));
                list.clearChildren();
                list.addAllTexts(paths);
                list.setSelected(index);
            }
            updateDisabled();
        });

        //move down
        table.row();
        moveDownButton = new Button(skin, "movedown");
        table.add(moveDownButton);
        addHandListener(moveDownButton);
        onChange(moveDownButton, () -> {
            var paths = selectedEmitter.getImagePaths();
            var index = list.getSelectedIndex();
            if (index < list.getTexts().size - 1) {
                UndoManager.add(new ImagesMoveUndoable(selectedEmitter, index, ++index, "Move Image"));
                list.clearChildren();
                list.addAllTexts(paths);
                list.setSelected(index);
            }
            updateDisabled();
        });

        //remove
        table.row();
        removeButton = new Button(skin, "cancel");
        table.add(removeButton);
        addHandListener(removeButton);
        onChange(removeButton, () -> {
            var paths = selectedEmitter.getImagePaths();
            if (paths.size <= 1) return;
            var index = list.getSelectedIndex();

            var path = list.getSelected().toString();
            UndoManager.add(new ImagesRemoveUndoable(selectedEmitter, path, fileHandles.get(path), sprites.get(path), "Remove Image"));

            list.clearChildren();
            list.addAllTexts(paths);
            list.setSelected(index < list.getTexts().size ? index : list.getTexts().size - 1);
            list.setAllowRemoval(list.getTexts().size > 1);
            removeButton.setDisabled(!list.isAllowRemoval());
            updateDisabled();
        });

        updateList();
        updateDisabled();
    }

    public void updateList() {
        var paths = selectedEmitter.getImagePaths();
        list.clearChildren();
        for (var path : paths) {
            list.addText(path);
        }
        list.setAllowRemoval(list.getTexts().size > 1);
    }

    public void updateDisabled() {
        var index = list.getSelectedIndex();
        var size = list.getTexts().size;
        var allowRemoval = size > 1;

        list.setAllowRemoval(allowRemoval);
        removeButton.setDisabled(!allowRemoval);
        moveUpButton.setDisabled(index == 0 || !allowRemoval);
        moveDownButton.setDisabled(index == size - 1 || !allowRemoval);
    }
}
