package com.ray3k.particleparkpro.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.FileDialogs;
import com.ray3k.particleparkpro.Settings;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel;
import com.ray3k.stripe.PopColorPicker;
import com.ray3k.stripe.PopColorPicker.PopColorPickerListener;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.PreviewSettings.*;
import static com.ray3k.particleparkpro.Settings.*;
import static com.ray3k.particleparkpro.widgets.panels.PreviewPanel.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.popColorPickerStyle;
import static com.ray3k.particleparkpro.widgets.styles.Styles.spinnerStyle;

/**
 * PopTable used to change the settings of the PreviewPanel and ParticlePreview.
 */
public class PopPreviewSettings extends PopTable {
    public PopPreviewSettings() {
        super(Core.skin.get(WindowStyle.class));

        setDraggable(false);
        setHideOnUnfocus(true);
        setKeepSizedWithinStage(true);
        addListener(new TableShowHideListener() {
            @Override
            public void tableShown(Event event) {
                Gdx.input.setInputProcessor(foregroundStage);
            }

            @Override
            public void tableHidden(Event event) {
                Gdx.input.setInputProcessor(stage);
            }
        });

        final int sectionPadding = 10;
        final int tabWidth = 5;
        final int itemSpacing = 5;
        final int spinnerWidth = 70;
        final int seperationWidth = 30;

        var scrollTable = new Table();
        var scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        add(scrollPane).grow();
        addForegroundScrollFocusListener(scrollPane);

        scrollTable.top().pad(5);

        //Pixels per meter
        var label = new Label("Pixels per meter", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        var table = new Table();
        scrollTable.add(table).space(itemSpacing).left();

        label = new Label("Value:", skin);
        table.add(label).padLeft(tabWidth);

        var pixelsPerMeterSpinner = new Spinner(getPixelsPerMeter(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        table.add(pixelsPerMeterSpinner).spaceLeft(itemSpacing).width(spinnerWidth);
        addHandListener(pixelsPerMeterSpinner.getButtonMinus());
        addHandListener(pixelsPerMeterSpinner.getButtonPlus());
        addIbeamListener(pixelsPerMeterSpinner.getTextField());
        onChange(pixelsPerMeterSpinner, () -> {
            setPixelsPerMeter(Math.max(0, pixelsPerMeterSpinner.getValue()));
            previewViewport.setUnitsPerPixel(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());
            EmitterPropertiesPanel.emitterPropertiesPanel.populateScrollTable(null);
        });

        scrollTable.row();
        var image = new Image(skin, "divider-10");
        image.setScaling(Scaling.stretchX);
        scrollTable.add(image).growX().padTop(sectionPadding).padBottom(sectionPadding);

        //Delta multiplier
        scrollTable.row();
        label = new Label("Delta multiplier", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).space(itemSpacing).left();

        label = new Label("Value:", skin);
        table.add(label).padLeft(tabWidth);

        var deltaMultiplierSpinner = new Spinner(getDeltaMultiplier(), .1f, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        table.add(deltaMultiplierSpinner).spaceLeft(itemSpacing).width(spinnerWidth);
        addHandListener(deltaMultiplierSpinner.getButtonMinus());
        addHandListener(deltaMultiplierSpinner.getButtonPlus());
        addIbeamListener(deltaMultiplierSpinner.getTextField());
        onChange(deltaMultiplierSpinner, () -> setDeltaMultiplier(Math.max(0, deltaMultiplierSpinner.getValue())));

        scrollTable.row();
        image = new Image(skin, "divider-10");
        image.setScaling(Scaling.stretchX);
        scrollTable.add(image).growX().padTop(sectionPadding).padBottom(sectionPadding);

        //Background color
        scrollTable.row();
        label = new Label("Background color", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        var stack = new Stack();
        scrollTable.add(stack).left().padLeft(tabWidth);

        image = new Image(skin, "swatch-bg");
        image.setScaling(Scaling.none);
        stack.add(image);

        var backgroundColorImage = new Image(skin, "swatch-fill");
        backgroundColorImage.setColor(getBackgroundColor());
        backgroundColorImage.setScaling(Scaling.none);
        stack.add(backgroundColorImage);
        addHandListener(stack);
        onClick(backgroundColorImage, () -> {
            var cp = new PopColorPicker(getBackgroundColor(), popColorPickerStyle);
            cp.setHideOnUnfocus(true);
            cp.setButtonListener(handListener);
            cp.setTextFieldListener(ibeamListener);
            cp.addListener(new TableShowHideListener() {
                @Override
                public void tableShown(Event event) {
                    setHideOnUnfocus(false);
                }

                @Override
                public void tableHidden(Event event) {
                    setHideOnUnfocus(true);
                }
            });
            cp.addListener(new PopColorPickerListener() {
                @Override
                public void picked(Color color) {
                    backgroundColorImage.setColor(color);
                    setBackgroundColor(color);
                    previewBackgroundImage.setColor(color);
                }

                @Override
                public void updated(Color color) {
                    backgroundColorImage.setColor(color);
                    setBackgroundColorTemporarily(color);
                    previewBackgroundImage.setColor(color);
                }

                @Override
                public void cancelled(Color oldColor) {
                    backgroundColorImage.setColor(oldColor);
                    setBackgroundColorTemporarily(oldColor);
                    previewBackgroundImage.setColor(oldColor);
                }
            });
            cp.show(foregroundStage);
        });

        scrollTable.row();
        image = new Image(skin, "divider-10");
        image.setScaling(Scaling.stretchX);
        scrollTable.add(image).growX().padTop(sectionPadding).padBottom(sectionPadding);

        //Statistics
        scrollTable.row();
        label = new Label("Statistics", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        var checkBox = new CheckBox("Statistics enabled", skin);
        checkBox.setChecked(isStatisticsEnabled());
        scrollTable.add(checkBox).padLeft(tabWidth).left();
        addHandListener(checkBox);
        onChange(checkBox, () -> {
            setStatisticsEnabled(checkBox.isChecked());
            statsLabel.setVisible(isStatisticsEnabled());
        });

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).left().padLeft(tabWidth).space(itemSpacing);

        table.defaults().space(itemSpacing);
        label = new Label("Label color:", skin);
        table.add(label);

        stack = new Stack();
        table.add(stack);

        image = new Image(skin, "swatch-bg");
        image.setScaling(Scaling.none);
        stack.add(image);

        var statisticsColorImage = new Image(skin, "swatch-fill");
        statisticsColorImage.setColor(getStatisticsColor());
        statisticsColorImage.setScaling(Scaling.none);
        stack.add(statisticsColorImage);
        addHandListener(stack);
        onClick(statisticsColorImage, () -> {
            var cp = new PopColorPicker(getStatisticsColor(), popColorPickerStyle);
            cp.setHideOnUnfocus(true);
            cp.setButtonListener(handListener);
            cp.setTextFieldListener(ibeamListener);
            cp.addListener(new TableShowHideListener() {
                @Override
                public void tableShown(Event event) {
                    setHideOnUnfocus(false);
                }

                @Override
                public void tableHidden(Event event) {
                    setHideOnUnfocus(true);
                }
            });
            cp.addListener(new PopColorPickerListener() {
                @Override
                public void picked(Color color) {
                    statisticsColorImage.setColor(color);
                    setStatisticsColor(color);
                    statsLabel.setColor(color);
                }

                @Override
                public void updated(Color color) {
                    statisticsColorImage.setColor(color);
                    setStatisticsColorTemporarily(color);
                    statsLabel.setColor(color);
                }

                @Override
                public void cancelled(Color oldColor) {
                    statisticsColorImage.setColor(oldColor);
                    setStatisticsColorTemporarily(oldColor);
                    statsLabel.setColor(oldColor);
                }
            });
            cp.show(foregroundStage);
        });

        scrollTable.row();
        image = new Image(skin, "divider-10");
        image.setScaling(Scaling.stretchX);
        scrollTable.add(image).growX().padTop(sectionPadding).padBottom(sectionPadding);

        //Preview image
        scrollTable.row();
        label = new Label("Preview image", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).space(itemSpacing).left().padLeft(tabWidth);

        table.defaults().space(itemSpacing);
        var selectPreviewTextButton = new TextButton("Select preview", skin);
        table.add(selectPreviewTextButton);
        addHandListener(selectPreviewTextButton);

        var removePreviewTextButton = new TextButton("Remove preview", skin);
        table.add(removePreviewTextButton);
        addHandListener(removePreviewTextButton);

        scrollTable.row();
        var previewSizeTable = new Table();
        scrollTable.add(previewSizeTable).space(itemSpacing).left().padLeft(tabWidth);

        if (previewImageTexture != null) {
            previewSizeTable.setColor(Color.WHITE);
            previewSizeTable.setTouchable(Touchable.childrenOnly);
        } else {
            previewSizeTable.setColor(skin.getColor("disabled"));
            previewSizeTable.setTouchable(Touchable.disabled);
        }

        var showResizeInterfaceCheckBox = new CheckBox("Show resize interface", skin);
        showResizeInterfaceCheckBox.setChecked(showResizeInterface);
        previewSizeTable.add(showResizeInterfaceCheckBox).left().space(itemSpacing).colspan(4);
        addHandListener(showResizeInterfaceCheckBox);
        onChange(showResizeInterfaceCheckBox, () -> {
            showResizeInterface = showResizeInterfaceCheckBox.isChecked();
            resizeWidget.setVisible(showResizeInterface);
        });

        previewSizeTable.row();
        previewSizeTable.defaults().right().space(itemSpacing);

        label = new Label("X:", skin);
        previewSizeTable.add(label).padLeft(tabWidth);

        var xSpinner = new Spinner(previewImageX, 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        previewSizeTable.add(xSpinner).width(spinnerWidth);
        addHandListener(xSpinner.getButtonMinus());
        addHandListener(xSpinner.getButtonPlus());
        addIbeamListener(xSpinner.getTextField());
        onChange(xSpinner, () -> previewImageX = xSpinner.getValue());

        label = new Label("Y:", skin);
        previewSizeTable.add(label).padLeft(tabWidth);

        var ySpinner = new Spinner(previewImageY, 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        previewSizeTable.add(ySpinner).width(spinnerWidth);
        addHandListener(ySpinner.getButtonMinus());
        addHandListener(ySpinner.getButtonPlus());
        addIbeamListener(ySpinner.getTextField());
        onChange(ySpinner, () -> previewImageY = ySpinner.getValue());

        previewSizeTable.row();
        label = new Label("Width:", skin);
        previewSizeTable.add(label).padLeft(tabWidth);

        var widthSpinner = new Spinner(previewImageWidth, 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        previewSizeTable.add(widthSpinner).width(spinnerWidth);
        addHandListener(widthSpinner.getButtonMinus());
        addHandListener(widthSpinner.getButtonPlus());
        addIbeamListener(widthSpinner.getTextField());
        onChange(widthSpinner, () -> previewImageWidth = widthSpinner.getValue());

        label = new Label("Height:", skin);
        previewSizeTable.add(label).padLeft(tabWidth);

        var heightSpinner = new Spinner(previewImageHeight, 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        previewSizeTable.add(heightSpinner).width(spinnerWidth);
        addHandListener(heightSpinner.getButtonMinus());
        addHandListener(heightSpinner.getButtonPlus());
        addIbeamListener(heightSpinner.getTextField());
        onChange(heightSpinner, () -> previewImageHeight = heightSpinner.getValue());

        onChange(selectPreviewTextButton, () -> {
            var fileHandle = FileDialogs.openDialog("Open preview image", getDefaultImagePath(), new String[] {"png","jpg","jpeg"}, "Image files (*.png;*.jpg;*.jpeg)");
            if (fileHandle != null) {
                setDefaultImagePath(fileHandle.parent());
                previewImageTexture = new Texture(fileHandle);

                previewImageX = 0;
                previewImageY = 0;
                previewImageWidth = previewImageTexture.getWidth();
                previewImageHeight = previewImageTexture.getHeight();

                xSpinner.setValue(previewImageX);
                ySpinner.setValue(previewImageY);
                widthSpinner.setValue(previewImageWidth);
                heightSpinner.setValue(previewImageHeight);

                previewSizeTable.setColor(Color.WHITE);
                previewSizeTable.setTouchable(Touchable.childrenOnly);
            }
        });
        onChange(removePreviewTextButton, () -> {
            if (previewImageTexture != null) {
                previewImageTexture.dispose();
                previewImageTexture = null;
            }

            previewSizeTable.setColor(skin.getColor("disabled"));
            previewSizeTable.setTouchable(Touchable.disabled);
            showResizeInterfaceCheckBox.setChecked(false);
        });

        scrollTable.row();
        image = new Image(skin, "divider-10");
        image.setScaling(Scaling.stretchX);
        scrollTable.add(image).growX().padTop(sectionPadding).padBottom(sectionPadding);

        //Render grid
        scrollTable.row();
        label = new Label("Render Grid", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        var renderGridTable = new Table();
        scrollTable.add(renderGridTable).left().padLeft(tabWidth);

        renderGridTable.defaults().spaceLeft(seperationWidth).spaceTop(itemSpacing);
        var gridCheckBox = new CheckBox("Grid enabled", skin);
        gridCheckBox.setChecked(isGridEnabled());
        renderGridTable.add(gridCheckBox).left().expandX();
        addHandListener(gridCheckBox);
        onChange(gridCheckBox, () -> setGridEnabled(gridCheckBox.isChecked()));

        var axesCheckBox = new CheckBox("Axes enabled", skin);
        axesCheckBox.setChecked(isAxesEnabled());
        renderGridTable.add(axesCheckBox).left().expandX();
        addHandListener(axesCheckBox);
        onChange(axesCheckBox, () -> setAxesEnabled(axesCheckBox.isChecked()));

        //Grid
        renderGridTable.row();
        var gridTable = new Table();
        renderGridTable.add(gridTable).left();

        gridTable.defaults().space(itemSpacing);
        gridTable.columnDefaults(0).right();
        gridTable.columnDefaults(1).left();
        label = new Label("Major gridlines:", skin);
        gridTable.add(label).padLeft(tabWidth);

        var majorGridlinesSpinner = new Spinner(getGridMajorGridlines(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        gridTable.add(majorGridlinesSpinner).width(spinnerWidth);
        addHandListener(majorGridlinesSpinner.getButtonMinus());
        addHandListener(majorGridlinesSpinner.getButtonPlus());
        addIbeamListener(majorGridlinesSpinner.getTextField());
        onChange(xSpinner, () -> previewImageX = xSpinner.getValue());
        onChange(majorGridlinesSpinner, () -> {
            float value = majorGridlinesSpinner.getValue();
            if (value > 0) setGridMajorGridlines(value);
        });

        gridTable.row();
        label = new Label("Minor gridlines:", skin);
        gridTable.add(label).padLeft(tabWidth);

        var minorGridlinesSpinner = new Spinner(getGridMinorGridlines(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        gridTable.add(minorGridlinesSpinner).width(spinnerWidth);
        addHandListener(minorGridlinesSpinner.getButtonMinus());
        addHandListener(minorGridlinesSpinner.getButtonPlus());
        addIbeamListener(minorGridlinesSpinner.getTextField());
        onChange(minorGridlinesSpinner, () -> {
            float value = minorGridlinesSpinner.getValue();
            if (value > 0) setGridMinorGridlines(value);
        });

        gridTable.row();
        label = new Label("Grid color:", skin);
        gridTable.add(label).padLeft(tabWidth);

        stack = new Stack();
        gridTable.add(stack).space(itemSpacing);

        image = new Image(skin, "swatch-bg");
        image.setScaling(Scaling.none);
        stack.add(image);

        var gridColorImage = new Image(skin, "swatch-fill");
        gridColorImage.setColor(getGridColor());
        gridColorImage.setScaling(Scaling.none);
        stack.add(gridColorImage);
        addHandListener(stack);
        onClick(gridColorImage, () -> {
            var cp = new PopColorPicker(getGridColor(), popColorPickerStyle);
            cp.setHideOnUnfocus(true);
            cp.setButtonListener(handListener);
            cp.setTextFieldListener(ibeamListener);
            cp.addListener(new TableShowHideListener() {
                @Override
                public void tableShown(Event event) {
                    setHideOnUnfocus(false);
                }

                @Override
                public void tableHidden(Event event) {
                    setHideOnUnfocus(true);
                }
            });
            cp.addListener(new PopColorPickerListener() {
                @Override
                public void picked(Color color) {
                    gridColorImage.setColor(color);
                    setGridColor(color);
                }

                @Override
                public void updated(Color color) {
                    gridColorImage.setColor(color);
                    setGridColorTemporarily(color);
                }

                @Override
                public void cancelled(Color oldColor) {
                    gridColorImage.setColor(oldColor);
                    setGridColorTemporarily(oldColor);
                }
            });
            cp.show(foregroundStage);
        });

        onChange(gridCheckBox, () -> {
            if (gridCheckBox.isChecked()) {
                gridTable.setTouchable(Touchable.enabled);
                gridTable.setColor(Color.WHITE);
            } else {
                gridTable.setTouchable(Touchable.disabled);
                gridTable.setColor(skin.getColor("disabled"));
                foregroundStage.setKeyboardFocus(null);
            }
        });
        if (!isGridEnabled()) {
            gridTable.setTouchable(Touchable.disabled);
            gridTable.setColor(skin.getColor("disabled"));
        }

        //Axes
        var axesTable = new Table();
        renderGridTable.add(axesTable).left().top();

        axesTable.defaults().space(itemSpacing);
        axesTable.columnDefaults(0).right();
        axesTable.columnDefaults(1).left();
        label = new Label("Axes color:", skin);
        axesTable.add(label).padLeft(tabWidth);

        stack = new Stack();
        axesTable.add(stack).space(itemSpacing);

        image = new Image(skin, "swatch-bg");
        image.setScaling(Scaling.none);
        stack.add(image);

        var axesColorImage = new Image(skin, "swatch-fill");
        axesColorImage.setColor(getAxesColor());
        axesColorImage.setScaling(Scaling.none);
        stack.add(axesColorImage);
        addHandListener(stack);
        onClick(axesColorImage, () -> {
            var cp = new PopColorPicker(getAxesColor(), popColorPickerStyle);
            cp.setHideOnUnfocus(true);
            cp.setButtonListener(handListener);
            cp.setTextFieldListener(ibeamListener);
            cp.addListener(new TableShowHideListener() {
                @Override
                public void tableShown(Event event) {
                    setHideOnUnfocus(false);
                }

                @Override
                public void tableHidden(Event event) {
                    setHideOnUnfocus(true);
                }
            });
            cp.addListener(new PopColorPickerListener() {
                @Override
                public void picked(Color color) {
                    axesColorImage.setColor(color);
                    setAxesColor(color);
                }

                @Override
                public void updated(Color color) {
                    axesColorImage.setColor(color);
                    setAxesColorTemporarily(color);
                }

                @Override
                public void cancelled(Color oldColor) {
                    axesColorImage.setColor(oldColor);
                    setAxesColorTemporarily(oldColor);
                }
            });
            cp.show(foregroundStage);
        });

        onChange(axesCheckBox, () -> {
            if (axesCheckBox.isChecked()) {
                axesTable.setTouchable(Touchable.enabled);
                axesTable.setColor(Color.WHITE);
            } else {
                axesTable.setTouchable(Touchable.disabled);
                axesTable.setColor(skin.getColor("disabled"));
                foregroundStage.setKeyboardFocus(null);
            }
        });
        if (!isAxesEnabled()) {
            axesTable.setTouchable(Touchable.disabled);
            axesTable.setColor(skin.getColor("disabled"));
        }

        scrollTable.row();
        image = new Image(skin, "divider-10");
        image.setScaling(Scaling.stretchX);
        scrollTable.add(image).growX().padTop(sectionPadding).padBottom(sectionPadding);

        //Shading
        scrollTable.row();
        label = new Label("Shading", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).space(itemSpacing).left();

        //Vertex Shader
        table.defaults().top().space(itemSpacing);
        var subtable = new Table();
        table.add(subtable);

        subtable.defaults().space(itemSpacing);
        label = new Label("Vertex Shader", skin);
        subtable.add(label).padLeft(tabWidth);

        subtable.row();
        var textButton = new TextButton("Default", skin);
        subtable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            vertShaderFile = null;
            Utils.initShaderProgram();
        });

        subtable.row();
        textButton = new TextButton("Set", skin);
        subtable.add(textButton);
        addHandListener(textButton);

        //Frag. Shader
        subtable = new Table();
        table.add(subtable);

        subtable.defaults().space(itemSpacing);
        label = new Label("Frag. Shader", skin);
        subtable.add(label).padLeft(tabWidth);

        subtable.row();
        textButton = new TextButton("Default", skin);
        subtable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            fragShaderFile = null;
            Utils.initShaderProgram();
        });

        subtable.row();
        textButton = new TextButton("Set", skin);
        subtable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {
            var fileHandle = FileDialogs.openDialog("Open shader", getDefaultSavePath(), null, null);

            if (fileHandle != null) {
                defaultFileName = fileHandle.name();
                Settings.setDefaultSavePath(fileHandle.parent());

                fragShaderFile = fileHandle;
                Utils.initShaderProgram();
            }
        });

        subtable.row();
        textButton = new TextButton("Reload", skin);
        subtable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Utils.initShaderProgram());

        scrollTable.row();
        image = new Image(skin, "divider-10");
        image.setScaling(Scaling.stretchX);
        scrollTable.add(image).growX().padTop(sectionPadding).padBottom(sectionPadding);

        //Reset
        scrollTable.row();
        label = new Label("Reset all setting to default", skin, "header");
        scrollTable.add(label).left();

        scrollTable.row();
        textButton = new TextButton("Reset", skin);
        scrollTable.add(textButton).left().padLeft(tabWidth).space(itemSpacing);
        addHandListener(textButton);
        onChange(textButton, () -> {
            resetPreviewSettings();
            hide();

            previewViewport.setUnitsPerPixel(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());
            EmitterPropertiesPanel.emitterPropertiesPanel.populateScrollTable(null);

            previewBackgroundImage.setColor(getBackgroundColor());

            statsLabel.setVisible(isStatisticsEnabled());
            statsLabel.setColor(getStatisticsColor());
        });
    }
}
