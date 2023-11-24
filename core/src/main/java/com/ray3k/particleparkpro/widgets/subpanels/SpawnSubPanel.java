package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnEllipseSide;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnShape;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.ScaledNumericValueUndoable;
import com.ray3k.particleparkpro.undo.undoables.SpawnEdgesUndoable;
import com.ray3k.particleparkpro.undo.undoables.SpawnSideUndoable;
import com.ray3k.particleparkpro.undo.undoables.SpawnTypeUndoable;
import com.ray3k.particleparkpro.widgets.LineGraph;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.particleparkpro.widgets.ToggleGroup;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.*;
import static com.ray3k.particleparkpro.widgets.subpanels.SpawnSubPanel.SpawnType.*;

/**
 * A widget that changes the spawn property of the currently selected emitter. Controls for shape, edges, side, width,
 * and height are provided.
 */
public class SpawnSubPanel extends Panel {
    private SpawnType spawnType;
    private ToggleGroup ellipseToggleGroup;
    private ToggleGroup shapeToggleGroup;
    private static final float GRAPH_UNDO_DELAY = .3f;
    private Action graphUndoAction;

    public enum SpawnType {
        POINT("point", SpawnShape.point), LINE("line", SpawnShape.line), SQUARE("square", SpawnShape.square), ELLIPSE("ellipse", SpawnShape.ellipse);

        String name;
        public SpawnShape spawnShape;

        SpawnType(String name, SpawnShape spawnShape) {
            this.name = name;
            this.spawnShape = spawnShape;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum ExpandedType {
        EXPANDED_WIDTH, EXPANDED_HEIGHT
    }
    private ExpandedType expandedType;

    private SpawnType shapeToType(SpawnShape spawnShape) {
        if (spawnShape == SpawnShape.point) return POINT;
        else if (spawnShape == SpawnShape.line) return LINE;
        else if (spawnShape == SpawnShape.square) return SQUARE;
        else return ELLIPSE;
    }

    public SpawnSubPanel() {
        populate();
    }

    public void populate() {
        tabTable.clearChildren();
        bodyTable.clearChildren();

        final int spinnerWidth = 70;
        final int itemSpacing = 5;
        final int sectionPadding = 10;

        var value = selectedEmitter.getSpawnShape();
        var valueWidth = selectedEmitter.getSpawnWidth();
        var valueHeight = selectedEmitter.getSpawnHeight();

        var sliderIncrement = 25f;
        var sliderRange = 300f;

        setTouchable(Touchable.enabled);

        tabTable.padRight(7);
        tabTable.left();
        var label = new Label("Spawn", skin, "header");
        tabTable.add(label).space(3);

        var graphToggleWidget = new ToggleGroup();
        bodyTable.add(graphToggleWidget).grow();

        //Value
        graphToggleWidget.table1.defaults().space(itemSpacing).left();
        graphToggleWidget.table1.left().top();
        var table = new Table();
        graphToggleWidget.table1.add(table);

        //Shape
        table.defaults().space(itemSpacing);
        label = new Label("Shape:", skin);
        table.add(label);

        var shapeSelectBox = new SelectBox<SpawnType>(skin);
        shapeSelectBox.setItems(POINT, LINE, SQUARE, ELLIPSE);
        spawnType = shapeToType(value.getShape());
        shapeSelectBox.setSelected(spawnType);
        table.add(shapeSelectBox).width(spinnerWidth);
        addHandListener(shapeSelectBox);
        addHandListener(shapeSelectBox.getList());
        addTooltip(shapeSelectBox, "The shape used to spawn particles", Align.top, Align.top, tooltipBottomArrowStyle);

        //Edges
        graphToggleWidget.table1.row();
        ellipseToggleGroup = new ToggleGroup();
        graphToggleWidget.table1.add(ellipseToggleGroup);

        ellipseToggleGroup.table2.defaults().space(itemSpacing);
        var checkBox = new CheckBox("Edges", skin);
        checkBox.setChecked(value.isEdges());
        ellipseToggleGroup.table2.add(checkBox).colspan(2).left();
        addHandListener(checkBox);
        addTooltip(checkBox, "If true, particles will spawn on the edges of the ellipse", Align.top, Align.top, tooltipBottomArrowStyle);

        //Side
        ellipseToggleGroup.table2.row();
        table = new Table();
        ellipseToggleGroup.table2.add(table);

        table.defaults().space(itemSpacing);
        label = new Label("Side:", skin);
        table.add(label);

        var sideSelectBox = new SelectBox<String>(skin);
        sideSelectBox.setDisabled(!value.isEdges());
        sideSelectBox.setItems("both", "top", "bottom");
        sideSelectBox.setSelectedIndex(value.getSide() == SpawnEllipseSide.both ? 0 : value.getSide() == SpawnEllipseSide.top ? 1 : 2);
        table.add(sideSelectBox).width(spinnerWidth);
        addHandListener(sideSelectBox);
        addHandListener(sideSelectBox.getList());
        addTooltip(sideSelectBox, "The side of the ellipse where particles will spawn", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(sideSelectBox, () -> {
            SpawnEllipseSide side;

            switch (sideSelectBox.getSelectedIndex()) {
                case 0:
                    side = SpawnEllipseSide.both;
                    break;
                case 1:
                    side = SpawnEllipseSide.top;
                    break;
                default:
                    side = SpawnEllipseSide.bottom;
                    break;
            }

            UndoManager.add(new SpawnSideUndoable(selectedEmitter, value, side, value.getSide(), "change Spawn Side"));
            particleEffect.reset();
        });

        onChange(checkBox, () -> {
            UndoManager.add(new SpawnEdgesUndoable(selectedEmitter, value, checkBox.isChecked(), "change Spawn Edges"));
            sideSelectBox.setDisabled(!value.isEdges());
            particleEffect.reset();
        });

        //Shape specific widgets
        graphToggleWidget.table1.row();

        shapeToggleGroup = new ToggleGroup();
        graphToggleWidget.table1.add(shapeToggleGroup);

        onChange(shapeSelectBox, () -> {
            var spawnTypeOld = spawnType;
            spawnType = shapeSelectBox.getSelected();
            UndoManager.add(new SpawnTypeUndoable(selectedEmitter, value, spawnType, spawnTypeOld, "change Spawn Type"));

            updateShownTable();
            particleEffect.reset();
        });

        //Width
        shapeToggleGroup.table2.defaults().space(itemSpacing);
        label = new Label("Width", skin, "header");
        shapeToggleGroup.table2.add(label).left().padTop(sectionPadding);

        //High
        shapeToggleGroup.table2.row();
        table = new Table();
        shapeToggleGroup.table2.add(table).top().left();
        table.defaults().space(itemSpacing).left();
        label = new Label("High:", skin);
        table.add(label);

        var widthHighToggleWidget = new ToggleGroup();
        table.add(widthHighToggleWidget);

        //High single
        widthHighToggleWidget.table1.defaults().space(itemSpacing);
        var widthHighSpinner = new Spinner(valueWidth.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        widthHighSpinner.setProgrammaticChangeEvents(false);
        widthHighToggleWidget.table1.add(widthHighSpinner).width(spinnerWidth);
        addIbeamListener(widthHighSpinner.getTextField());
        addHandListener(widthHighSpinner.getButtonPlus());
        addHandListener(widthHighSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(widthHighSpinner);
        addTooltip(widthHighSpinner, "The high value for the width of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var widthHighExpandButton = new Button(skin, "moveright");
        widthHighToggleWidget.table1.add(widthHighExpandButton);
        addHandListener(widthHighExpandButton);
        addTooltip(widthHighExpandButton, "Expand to define a range for the high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(widthHighExpandButton, widthHighToggleWidget::swap);

        //High range
        widthHighToggleWidget.table2.defaults().space(itemSpacing);
        var widthHighMinSpinner = new Spinner(valueWidth.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        widthHighMinSpinner.setProgrammaticChangeEvents(false);
        widthHighToggleWidget.table2.add(widthHighMinSpinner).width(spinnerWidth);
        addIbeamListener(widthHighMinSpinner.getTextField());
        addHandListener(widthHighMinSpinner.getButtonPlus());
        addHandListener(widthHighMinSpinner.getButtonMinus());
        ;addUnfocusOnEnterKeyListener(widthHighMinSpinner);
        addTooltip(widthHighMinSpinner, "The minimum high value for the width of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var widthHighMaxSpinner = new Spinner(valueWidth.getHighMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        widthHighMaxSpinner.setProgrammaticChangeEvents(false);
        widthHighToggleWidget.table2.add(widthHighMaxSpinner).width(spinnerWidth);
        addIbeamListener(widthHighMaxSpinner.getTextField());
        addHandListener(widthHighMaxSpinner.getButtonPlus());
        addHandListener(widthHighMaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(widthHighMaxSpinner);
        addTooltip(widthHighMaxSpinner, "The maximum high value for the width of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var widthHighCollapseButton = new Button(skin, "moveleft");
        widthHighToggleWidget.table2.add(widthHighCollapseButton);
        addHandListener(widthHighCollapseButton);
        addTooltip(widthHighCollapseButton, "Collapse to define a single high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(widthHighCollapseButton, widthHighToggleWidget::swap);

        if (!MathUtils.isEqual(valueWidth.getHighMin(), valueWidth.getHighMax())) widthHighToggleWidget.swap();

        //Low
        table.row();
        label = new Label("Low:", skin);
        table.add(label);

        var widthLowToggleWidget = new ToggleGroup();
        table.add(widthLowToggleWidget);

        //Low single
        widthLowToggleWidget.table1.defaults().space(itemSpacing);
        var widthLowSpinner = new Spinner(valueWidth.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        widthLowSpinner.setProgrammaticChangeEvents(false);
        widthLowToggleWidget.table1.add(widthLowSpinner).width(spinnerWidth);
        addIbeamListener(widthLowSpinner.getTextField());
        addHandListener(widthLowSpinner.getButtonPlus());
        addHandListener(widthLowSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(widthLowSpinner);
        addTooltip(widthLowSpinner, "The low value for the width of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var widthLowExpandButton = new Button(skin, "moveright");
        widthLowToggleWidget.table1.add(widthLowExpandButton);
        addHandListener(widthLowExpandButton);
        addTooltip(widthLowExpandButton, "Expand to define a range for the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(widthLowExpandButton, widthLowToggleWidget::swap);

        //Low range
        widthLowToggleWidget.table2.defaults().space(itemSpacing);
        var widthLowMinSpinner = new Spinner(valueWidth.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        widthLowMinSpinner.setProgrammaticChangeEvents(false);
        widthLowToggleWidget.table2.add(widthLowMinSpinner).width(spinnerWidth);
        addIbeamListener(widthLowMinSpinner.getTextField());
        addHandListener(widthLowMinSpinner.getButtonPlus());
        addHandListener(widthLowMinSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(widthLowMinSpinner);
        addTooltip(widthLowMinSpinner, "The minimum low value for the width of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var widthLowMaxSpinner = new Spinner(valueWidth.getLowMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        widthLowMaxSpinner.setProgrammaticChangeEvents(false);
        widthLowToggleWidget.table2.add(widthLowMaxSpinner).width(spinnerWidth);
        addIbeamListener(widthLowMaxSpinner.getTextField());
        addHandListener(widthLowMaxSpinner.getButtonPlus());
        addHandListener(widthLowMaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(widthLowMaxSpinner);
        addTooltip(widthLowMaxSpinner, "The maximum low value for the width of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var widthLowCollapseButton = new Button(skin, "moveleft");
        widthLowToggleWidget.table2.add(widthLowCollapseButton);
        addHandListener(widthLowCollapseButton);
        addTooltip(widthLowCollapseButton, "Collapse to define a single low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(widthLowCollapseButton, widthLowToggleWidget::swap);

        if (!MathUtils.isEqual(valueWidth.getLowMin(), valueWidth.getLowMax())) widthLowToggleWidget.swap();

        //Graph small
        var graphWidth = new LineGraph("Duration", lineGraphStyle);
        graphWidth.setNodes(valueWidth.getTimeline(), valueWidth.getScaling());
        graphWidth.setNodeListener(handListener);
        shapeToggleGroup.table2.add(graphWidth);

        var widthExpandGraphButton = new Button(skin, "plus");
        shapeToggleGroup.table2.add(widthExpandGraphButton).bottom();
        addHandListener(widthExpandGraphButton);
        addTooltip(widthExpandGraphButton, "Expand to large graph view", Align.top, Align.top, tooltipBottomArrowStyle);

        //Expanded graph view
        graphToggleWidget.table2.defaults().space(itemSpacing);
        var graphExpanded = new LineGraph("Duration", lineGraphBigStyle);
        graphExpanded.setNodeListener(handListener);
        graphToggleWidget.table2.add(graphExpanded).grow();

        onChange(widthExpandGraphButton, () -> {
            graphToggleWidget.swap();
            graphExpanded.setNodes(valueWidth.getTimeline(), valueWidth.getScaling());
            expandedType = ExpandedType.EXPANDED_WIDTH;
        });

        var collapseExpandedGraphButton = new Button(skin, "minus");
        graphToggleWidget.table2.add(collapseExpandedGraphButton).bottom();
        addHandListener(collapseExpandedGraphButton);
        addTooltip(collapseExpandedGraphButton, "Collapse to normal view", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(collapseExpandedGraphButton, graphToggleWidget::swap);

        //Height
        shapeToggleGroup.table2.row();
        label = new Label("Height", skin, "header");
        shapeToggleGroup.table2.add(label).left().padTop(sectionPadding);

        //High
        shapeToggleGroup.table2.row();
        table = new Table();
        shapeToggleGroup.table2.add(table).top().left();
        table.defaults().space(itemSpacing).left();
        label = new Label("High:", skin);
        table.add(label);

        var heightHighToggleWidget = new ToggleGroup();
        table.add(heightHighToggleWidget);

        //High single
        heightHighToggleWidget.table1.defaults().space(itemSpacing);
        var heightHighSpinner = new Spinner(valueHeight.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        heightHighSpinner.setProgrammaticChangeEvents(false);
        heightHighToggleWidget.table1.add(heightHighSpinner).width(spinnerWidth);
        addIbeamListener(heightHighSpinner.getTextField());
        addHandListener(heightHighSpinner.getButtonPlus());
        addHandListener(heightHighSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(heightHighSpinner);
        addTooltip(heightHighSpinner, "The high value for the height of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var heightHighExpandButton = new Button(skin, "moveright");
        heightHighToggleWidget.table1.add(heightHighExpandButton);
        addHandListener(heightHighExpandButton);
        addTooltip(heightHighExpandButton, "Expand to define a range for the high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(heightHighExpandButton, heightHighToggleWidget::swap);

        //High range
        heightHighToggleWidget.table2.defaults().space(itemSpacing);
        var heightHighMinSpinner = new Spinner(valueHeight.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        heightHighMinSpinner.setProgrammaticChangeEvents(false);
        heightHighToggleWidget.table2.add(heightHighMinSpinner).width(spinnerWidth);
        addIbeamListener(heightHighMinSpinner.getTextField());
        addHandListener(heightHighMinSpinner.getButtonPlus());
        addHandListener(heightHighMinSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(heightHighMinSpinner);
        addTooltip(heightHighMinSpinner, "The minimum high value for the height of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var heightHighMaxSpinner = new Spinner(valueHeight.getHighMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        heightHighMaxSpinner.setProgrammaticChangeEvents(false);
        heightHighToggleWidget.table2.add(heightHighMaxSpinner).width(spinnerWidth);
        addIbeamListener(heightHighMaxSpinner.getTextField());
        addHandListener(heightHighMaxSpinner.getButtonPlus());
        addHandListener(heightHighMaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(heightHighMaxSpinner);
        addTooltip(heightHighMaxSpinner, "The maximum high value for the height of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var heightHighCollapseButton = new Button(skin, "moveleft");
        heightHighToggleWidget.table2.add(heightHighCollapseButton);
        addHandListener(heightHighCollapseButton);
        addTooltip(heightHighCollapseButton, "Collapse to define a single high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(heightHighCollapseButton, heightHighToggleWidget::swap);

        if (!MathUtils.isEqual(valueHeight.getHighMin(), valueHeight.getHighMax())) heightHighToggleWidget.swap();

        //Low
        table.row();
        label = new Label("Low:", skin);
        table.add(label);

        var heightLowToggleWidget = new ToggleGroup();
        table.add(heightLowToggleWidget);

        //Low single
        heightLowToggleWidget.table1.defaults().space(itemSpacing);
        var heightLowSpinner = new Spinner(valueHeight.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        heightLowSpinner.setProgrammaticChangeEvents(false);
        heightLowToggleWidget.table1.add(heightLowSpinner).width(spinnerWidth);
        addIbeamListener(heightLowSpinner.getTextField());
        addHandListener(heightLowSpinner.getButtonPlus());
        addHandListener(heightLowSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(heightLowSpinner);
        addTooltip(heightLowSpinner, "The low value for the height of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var heightLowExpandButton = new Button(skin, "moveright");
        heightLowToggleWidget.table1.add(heightLowExpandButton);
        addHandListener(heightLowExpandButton);
        addTooltip(heightLowExpandButton, "Expand to define a range for the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(heightLowExpandButton, heightLowToggleWidget::swap);

        //Low range
        heightLowToggleWidget.table2.defaults().space(itemSpacing);
        var heightLowMinSpinner = new Spinner(valueHeight.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        heightLowMinSpinner.setProgrammaticChangeEvents(false);
        heightLowToggleWidget.table2.add(heightLowMinSpinner).width(spinnerWidth);
        addIbeamListener(heightLowMinSpinner.getTextField());
        addHandListener(heightLowMinSpinner.getButtonPlus());
        addHandListener(heightLowMinSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(heightLowMinSpinner);
        addTooltip(heightLowMinSpinner, "The minimum low value for the height of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var heightLowMaxSpinner = new Spinner(valueHeight.getLowMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        heightLowMaxSpinner.setProgrammaticChangeEvents(false);
        heightLowToggleWidget.table2.add(heightLowMaxSpinner).width(spinnerWidth);
        addIbeamListener(heightLowMaxSpinner.getTextField());
        addHandListener(heightLowMaxSpinner.getButtonPlus());
        addHandListener(heightLowMaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(heightLowMaxSpinner);
        addTooltip(heightLowMaxSpinner, "The maximum low value for the height of the spawn shape", Align.top, Align.top, tooltipBottomArrowStyle);

        var heightLowCollapseButton = new Button(skin, "moveleft");
        heightLowToggleWidget.table2.add(heightLowCollapseButton);
        addHandListener(heightLowCollapseButton);
        addTooltip(heightLowCollapseButton, "Collapse to define a single low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(heightLowCollapseButton, heightLowToggleWidget::swap);

        if (!MathUtils.isEqual(valueHeight.getLowMin(), valueHeight.getLowMax())) heightLowToggleWidget.swap();

        //Graph small
        var graphHeight = new LineGraph("Duration", lineGraphStyle);
        graphHeight.setNodes(valueHeight.getTimeline(), valueHeight.getScaling());
        graphHeight.setNodeListener(handListener);
        shapeToggleGroup.table2.add(graphHeight);

        var heightExpandGraphButton = new Button(skin, "plus");
        shapeToggleGroup.table2.add(heightExpandGraphButton).bottom();
        addHandListener(heightExpandGraphButton);
        addTooltip(heightExpandGraphButton, "Expand to large graph view", Align.top, Align.top, tooltipBottomArrowStyle);

        onChange(heightExpandGraphButton, () -> {
            graphToggleWidget.swap();
            graphExpanded.setNodes(valueHeight.getTimeline(), valueHeight.getScaling());
            expandedType = ExpandedType.EXPANDED_HEIGHT;
        });

        updateShownTable();

        var changeListener = onChange(widthHighSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setHigh(widthHighSpinner.getValue());
            UndoManager.add(undo);

            widthHighMinSpinner.setValue(widthHighSpinner.getValue());
            widthHighMaxSpinner.setValue(widthHighSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(widthHighSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(widthHighMinSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setHighMin(widthHighMinSpinner.getValue());
            UndoManager.add(undo);

            widthHighSpinner.setValue(widthHighMinSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(widthHighMinSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(widthHighMaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setHighMax(widthHighMaxSpinner.getValue());
            UndoManager.add(undo);

            widthHighSpinner.setValue(widthHighMaxSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(widthHighMaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        onChange(widthHighCollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setHigh(widthHighSpinner.getValue());
            UndoManager.add(undo);

            widthHighMinSpinner.setValue(widthHighSpinner.getValue());
            widthHighMaxSpinner.setValue(widthHighSpinner.getValue());
            particleEffect.reset();
        });

        changeListener = onChange(widthLowSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setLow(widthLowSpinner.getValue());
            UndoManager.add(undo);

            widthLowMinSpinner.setValue(widthLowSpinner.getValue());
            widthLowMaxSpinner.setValue(widthLowSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(widthLowSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(widthLowMinSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setLowMin(widthLowMinSpinner.getValue());
            UndoManager.add(undo);

            widthLowSpinner.setValue(widthLowMinSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(widthLowMinSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(widthLowMaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setLowMax(widthLowMaxSpinner.getValue());
            UndoManager.add(undo);

            widthLowSpinner.setValue(widthLowMaxSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(widthLowMaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        onChange(widthLowCollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueWidth, "change Spawn Width");
            undo.oldValue.set(valueWidth);
            undo.newValue.set(valueWidth);
            undo.newValue.setLow(widthLowSpinner.getValue());
            UndoManager.add(undo);

            widthLowMinSpinner.setValue(widthLowSpinner.getValue());
            widthLowMaxSpinner.setValue(widthLowSpinner.getValue());
            particleEffect.reset();
        });

        onChange(graphWidth, () -> {
            var nodes = graphWidth.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            addGraphUpdateAction(valueWidth, newTimeline, newScaling, "change Spawn Width");
            particleEffect.reset();
        });

        changeListener = onChange(heightHighSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setHigh(heightHighSpinner.getValue());
            UndoManager.add(undo);

            heightHighMinSpinner.setValue(heightHighSpinner.getValue());
            heightHighMaxSpinner.setValue(heightHighSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(heightHighSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(heightHighMinSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setHighMin(heightHighMinSpinner.getValue());
            UndoManager.add(undo);

            heightHighSpinner.setValue(heightHighMinSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(heightHighMinSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(heightHighMaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setHighMax(heightHighMaxSpinner.getValue());
            UndoManager.add(undo);

            heightHighSpinner.setValue(heightHighMaxSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(heightHighMaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(heightHighCollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setHigh(heightHighSpinner.getValue());
            UndoManager.add(undo);

            heightHighMinSpinner.setValue(heightHighSpinner.getValue());
            heightHighMaxSpinner.setValue(heightHighSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(heightLowSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(heightLowSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setLow(heightLowSpinner.getValue());
            UndoManager.add(undo);

            heightLowMinSpinner.setValue(heightLowSpinner.getValue());
            heightLowMaxSpinner.setValue(heightLowSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(heightLowMinSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(heightLowMinSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setLowMin(heightLowMinSpinner.getValue());
            UndoManager.add(undo);

            heightLowSpinner.setValue(heightLowMinSpinner.getValue());
            particleEffect.reset();
        });
        addInfiniteSlider(heightLowMaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        onChange(heightLowMaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setLowMax(heightLowMaxSpinner.getValue());
            UndoManager.add(undo);

            heightLowSpinner.setValue(heightLowMaxSpinner.getValue());
            particleEffect.reset();
        });

        onChange(heightLowCollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, valueHeight, "change Spawn Height");
            undo.oldValue.set(valueHeight);
            undo.newValue.set(valueHeight);
            undo.newValue.setLow(heightLowSpinner.getValue());
            UndoManager.add(undo);

            heightLowMinSpinner.setValue(heightLowSpinner.getValue());
            heightLowMaxSpinner.setValue(heightLowSpinner.getValue());
            particleEffect.reset();
        });

        onChange(graphHeight, () -> {
            var nodes = graphHeight.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            addGraphUpdateAction(valueHeight, newTimeline, newScaling, "change Spawn Height");
            particleEffect.reset();
        });

        onChange(graphExpanded, () -> {
            var nodes = graphExpanded.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            if (expandedType == ExpandedType.EXPANDED_WIDTH) {
                addGraphUpdateAction(valueWidth, newTimeline, newScaling, "change Spawn Width");
            } else {
                addGraphUpdateAction(valueHeight, newTimeline, newScaling, "change Spawn Height");
            }
            particleEffect.reset();
        });
    }

    private void updateShownTable() {
        if (spawnType == ELLIPSE) ellipseToggleGroup.showTable2();
        else ellipseToggleGroup.showTable1();

        if (spawnType == POINT) shapeToggleGroup.showTable1();
        else shapeToggleGroup.showTable2();
    }

    private void addGraphUpdateAction(ScaledNumericValue value, float[] newTimeline, float[] newScaling, String description) {
        var oldValue = new ScaledNumericValue();
        oldValue.set(value);

        value.setTimeline(newTimeline);
        value.setScaling(newScaling);

        if (graphUndoAction != null) graphUndoAction.restart();
        else {
            graphUndoAction = new TemporalAction(GRAPH_UNDO_DELAY) {
                @Override
                protected void update(float percent) {
                }

                @Override
                protected void end() {
                    var undo = new ScaledNumericValueUndoable(selectedEmitter, value, description);
                    undo.oldValue.set(oldValue);
                    undo.newValue.set(value);
                    UndoManager.add(undo);

                    graphUndoAction = null;
                }
            };
            stage.addAction(graphUndoAction);
        }
    }
}
