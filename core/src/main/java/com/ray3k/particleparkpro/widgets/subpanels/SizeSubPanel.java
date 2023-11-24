package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.DualScaledNumericValueRelativeUndoable;
import com.ray3k.particleparkpro.undo.undoables.DualScaledNumericValueUndoable;
import com.ray3k.particleparkpro.undo.undoables.ScaledNumericValueRelativeUndoable;
import com.ray3k.particleparkpro.undo.undoables.ScaledNumericValueUndoable;
import com.ray3k.particleparkpro.widgets.LineGraph;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.particleparkpro.widgets.ToggleGroup;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.*;

/**
 * A widget to modify the size value of the emitter specifially. Very much like a RangeSubPanel, but allows editing the
 * x and y values simultaneously.
 */
public class SizeSubPanel extends Panel {
    private enum ExpandedType {
        EXPANDED_X, EXPANDED_Y, EXPANDED_BOTH
    }
    private ExpandedType expandedType;
    private static final float GRAPH_UNDO_DELAY = .3f;
    private Action graphUndoAction;

    public SizeSubPanel() {
        final int spinnerWidth = 70;
        final int itemSpacing = 5;
        final int sectionPadding = 10;

        var xValue = selectedEmitter.getXScale();
        var yValue = selectedEmitter.getYScale();

        float sliderIncrement = 1f;
        float sliderRange = 10f;

        setTouchable(Touchable.enabled);

        tabTable.padRight(7);
        tabTable.left();
        var label = new Label("Size", skin, "header");
        tabTable.add(label);

        var graphToggleWidget = new ToggleGroup();
        bodyTable.add(graphToggleWidget).grow();

        //Normal view
        graphToggleWidget.table1.defaults().space(itemSpacing);
        graphToggleWidget.table1.left();

        //Split X and Y
        var splitXYcheckBox = new CheckBox("Split X and Y", skin);
        splitXYcheckBox.setChecked(yValue.isActive());
        graphToggleWidget.table1.add(splitXYcheckBox).left();
        addHandListener(splitXYcheckBox);
        addTooltip(splitXYcheckBox, "If true, the X and Y values can be set independently", Align.top, Align.top, tooltipBottomArrowStyle);

        //Split ToggleGroup
        graphToggleWidget.table1.row();
        var splitToggleWidget = new ToggleGroup();
        if (yValue.isActive()) splitToggleWidget.swap();
        graphToggleWidget.table1.add(splitToggleWidget);

        //Joined
        //Relative
        splitToggleWidget.table1.defaults().space(itemSpacing);
        var relativeCheckBox = new CheckBox("Relative", skin);
        relativeCheckBox.setProgrammaticChangeEvents(false);
        relativeCheckBox.setChecked(xValue.isRelative());
        splitToggleWidget.table1.add(relativeCheckBox).left();
        addHandListener(relativeCheckBox);
        addTooltip(relativeCheckBox, "If true, the high value is added to the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(relativeCheckBox, () -> UndoManager.add(new DualScaledNumericValueRelativeUndoable(selectedEmitter, xValue, yValue, relativeCheckBox.isChecked(), "change Size Relative")));

        //High
        splitToggleWidget.table1.row();
        var table = new Table();
        splitToggleWidget.table1.add(table).top();

        table.defaults().space(itemSpacing).left();
        label = new Label("High:", skin);
        table.add(label);

        var highToggleWidget = new ToggleGroup();
        table.add(highToggleWidget);

        //High single
        highToggleWidget.table1.defaults().space(itemSpacing);
        var highSpinner = new Spinner(xValue.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table1.add(highSpinner).width(spinnerWidth);
        addIbeamListener(highSpinner.getTextField());
        addHandListener(highSpinner.getButtonPlus());
        addHandListener(highSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highSpinner);
        addTooltip(highSpinner, "The high value for the particle size in world units", Align.top, Align.top, tooltipBottomArrowStyle);

        var highExpandButton = new Button(skin, "moveright");
        highToggleWidget.table1.add(highExpandButton);
        addHandListener(highExpandButton);
        addTooltip(highExpandButton, "Expand to define a range for the high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(highExpandButton, highToggleWidget::swap);

        //High range
        highToggleWidget.table2.defaults().space(itemSpacing);
        var highMinSpinner = new Spinner(xValue.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highMinSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table2.add(highMinSpinner).width(spinnerWidth);
        addIbeamListener(highMinSpinner.getTextField());
        addHandListener(highMinSpinner.getButtonPlus());
        addHandListener(highMinSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highMinSpinner);
        addTooltip(highMinSpinner, "The minimum high value for the particle size in world units", Align.top, Align.top, tooltipBottomArrowStyle);

        var highMaxSpinner = new Spinner(xValue.getHighMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highMaxSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table2.add(highMaxSpinner).width(spinnerWidth);
        addIbeamListener(highMaxSpinner.getTextField());
        addHandListener(highMaxSpinner.getButtonPlus());
        addHandListener(highMaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highMaxSpinner);
        addTooltip(highMaxSpinner, "The maximum high value for the particle size in world units", Align.top, Align.top, tooltipBottomArrowStyle);

        var highCollapseButton = new Button(skin, "moveleft");
        highToggleWidget.table2.add(highCollapseButton);
        addHandListener(highCollapseButton);
        addTooltip(highCollapseButton, "Collapse to define a single high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(highCollapseButton, highToggleWidget::swap);

        if (!MathUtils.isEqual(xValue.getHighMin(), xValue.getHighMax())) highToggleWidget.swap();

        //Low
        table.row();
        label = new Label("Low:", skin);
        table.add(label);

        var lowToggleWidget = new ToggleGroup();
        table.add(lowToggleWidget);

        //Low single
        lowToggleWidget.table1.defaults().space(itemSpacing);
        var lowSpinner = new Spinner(xValue.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowSpinner.setProgrammaticChangeEvents(false);
        lowToggleWidget.table1.add(lowSpinner).width(spinnerWidth);
        addIbeamListener(lowSpinner.getTextField());
        addHandListener(lowSpinner.getButtonPlus());
        addHandListener(lowSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowSpinner);
        addTooltip(lowSpinner, "The low value for the particle size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowExpandButton = new Button(skin, "moveright");
        lowToggleWidget.table1.add(lowExpandButton);
        addHandListener(lowExpandButton);
        addTooltip(lowExpandButton, "Expand to define a range for the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(lowExpandButton, lowToggleWidget::swap);

        //Low range
        lowToggleWidget.table2.defaults().space(itemSpacing);
        var lowMinSpinner = new Spinner(xValue.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowMinSpinner.setProgrammaticChangeEvents(false);
        lowToggleWidget.table2.add(lowMinSpinner).width(spinnerWidth);
        addIbeamListener(lowMinSpinner.getTextField());
        addHandListener(lowMinSpinner.getButtonPlus());
        addHandListener(lowMinSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowMinSpinner);
        addTooltip(lowMinSpinner, "The minimum low value for the particle size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowMaxSpinner = new Spinner(xValue.getLowMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowMaxSpinner.setProgrammaticChangeEvents(false);
        lowToggleWidget.table2.add(lowMaxSpinner).width(spinnerWidth);
        addIbeamListener(lowMaxSpinner.getTextField());
        addHandListener(lowMaxSpinner.getButtonPlus());
        addHandListener(lowMaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowMaxSpinner);
        addTooltip(lowMaxSpinner, "The maximum low value for the particle size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowCollapseButton = new Button(skin, "moveleft");
        lowToggleWidget.table2.add(lowCollapseButton);
        addHandListener(lowCollapseButton);
        addTooltip(lowCollapseButton, "Collapse to define a single low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(lowCollapseButton, lowToggleWidget::swap);

        if (!MathUtils.isEqual(xValue.getLowMin(), xValue.getLowMax())) lowToggleWidget.swap();

        //Graph small
        var graph = new LineGraph("Life", lineGraphStyle);
        graph.setNodes(xValue.getTimeline(), xValue.getScaling());
        graph.setNodeListener(handListener);
        splitToggleWidget.table1.add(graph);

        var graphExpandButton = new Button(skin, "plus");
        splitToggleWidget.table1.add(graphExpandButton).bottom();
        addHandListener(graphExpandButton);
        addTooltip(graphExpandButton, "Expand to large graph view", Align.top, Align.top, tooltipBottomArrowStyle);

        //Expanded graph view
        graphToggleWidget.table2.defaults().space(itemSpacing);
        var graphExpanded = new LineGraph("Life", lineGraphBigStyle);
        graph.setNodes(xValue.getTimeline(), xValue.getScaling());
        graphExpanded.setNodeListener(handListener);
        graphToggleWidget.table2.add(graphExpanded).grow();

        onChange(graphExpandButton, () -> {
            graphToggleWidget.swap();
            graphExpanded.setNodes(xValue.getTimeline(), xValue.getScaling());
            expandedType = ExpandedType.EXPANDED_BOTH;
        });

        var graphCollapseButton = new Button(skin, "minus");
        graphToggleWidget.table2.add(graphCollapseButton).bottom();
        addHandListener(graphCollapseButton);
        addTooltip(graphCollapseButton, "Collapse to normal view", Align.top, Align.top, tooltipBottomArrowStyle);

        //Separate
        //X size
        splitToggleWidget.table2.defaults().space(itemSpacing);
        label = new Label("X Size", skin, "header");
        splitToggleWidget.table2.add(label).left().padTop(sectionPadding);

        //Relative
        splitToggleWidget.table2.row();
        var relativeXcheckBox = new CheckBox("Relative", skin);
        relativeXcheckBox.setProgrammaticChangeEvents(false);
        splitToggleWidget.table2.add(relativeXcheckBox).left();
        addHandListener(relativeXcheckBox);
        addTooltip(relativeXcheckBox, "If true, the high value is added to the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(relativeXcheckBox, () -> UndoManager.add(new ScaledNumericValueRelativeUndoable(selectedEmitter, xValue, relativeXcheckBox.isChecked(), "change X Size Relative")));

        //High
        splitToggleWidget.table2.row();
        table = new Table();
        splitToggleWidget.table2.add(table).top();

        table.defaults().space(itemSpacing).left();
        label = new Label("High:", skin);
        table.add(label);

        var highXtoggleWidget = new ToggleGroup();
        table.add(highXtoggleWidget);

        //High single
        highXtoggleWidget.table1.defaults().space(itemSpacing);
        var highXspinner = new Spinner(xValue.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highXspinner.setProgrammaticChangeEvents(false);
        highXtoggleWidget.table1.add(highXspinner).width(spinnerWidth);
        addIbeamListener(highXspinner.getTextField());
        addHandListener(highXspinner.getButtonPlus());
        addHandListener(highXspinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highXspinner);
        addTooltip(highXspinner, "The high value for the particle X size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var highXexpandButton = new Button(skin, "moveright");
        highXtoggleWidget.table1.add(highXexpandButton);
        addHandListener(highXexpandButton);
        addTooltip(highXexpandButton, "Expand to define a range for the high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(highXexpandButton, highXtoggleWidget::swap);

        //High range
        highXtoggleWidget.table2.defaults().space(itemSpacing);
        var highXminSpinner = new Spinner(xValue.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highXminSpinner.setProgrammaticChangeEvents(false);
        highXtoggleWidget.table2.add(highXminSpinner).width(spinnerWidth);
        addIbeamListener(highXminSpinner.getTextField());
        addHandListener(highXminSpinner.getButtonPlus());
        addHandListener(highXminSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highXminSpinner);
        addTooltip(highXminSpinner, "The minimum high value for the particle X size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var highXmaxSpinner = new Spinner(xValue.getHighMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highXmaxSpinner.setProgrammaticChangeEvents(false);
        highXtoggleWidget.table2.add(highXmaxSpinner).width(spinnerWidth);
        addIbeamListener(highXmaxSpinner.getTextField());
        addHandListener(highXmaxSpinner.getButtonPlus());
        addHandListener(highXmaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highXmaxSpinner);
        addTooltip(highXmaxSpinner, "The maximum high value for the particle X size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var highXcollapseButton = new Button(skin, "moveleft");
        highXtoggleWidget.table2.add(highXcollapseButton);
        addHandListener(highXcollapseButton);
        addTooltip(highXcollapseButton, "Collapse to define a single high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(highXcollapseButton, highXtoggleWidget::swap);

        //Low
        table.row();
        label = new Label("Low:", skin);
        table.add(label);

        var lowXtoggleWidget = new ToggleGroup();
        table.add(lowXtoggleWidget);

        //Low single
        lowXtoggleWidget.table1.defaults().space(itemSpacing);
        var lowXspinner = new Spinner(xValue.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowXspinner.setProgrammaticChangeEvents(false);
        lowXtoggleWidget.table1.add(lowXspinner).width(spinnerWidth);
        addIbeamListener(lowXspinner.getTextField());
        addHandListener(lowXspinner.getButtonPlus());
        addHandListener(lowXspinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowXspinner);
        addTooltip(lowXspinner, "The low value for the particle X size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowXexpandButton = new Button(skin, "moveright");
        lowXtoggleWidget.table1.add(lowXexpandButton);
        addHandListener(lowXexpandButton);
        addTooltip(lowXexpandButton, "Expand to define a range for the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(lowXexpandButton, lowXtoggleWidget::swap);

        //Low range
        lowXtoggleWidget.table2.defaults().space(itemSpacing);
        var lowXminSpinner = new Spinner(xValue.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowXminSpinner.setProgrammaticChangeEvents(false);
        lowXtoggleWidget.table2.add(lowXminSpinner).width(spinnerWidth);
        addIbeamListener(lowXminSpinner.getTextField());
        addHandListener(lowXminSpinner.getButtonPlus());
        addHandListener(lowXminSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowXminSpinner);
        addTooltip(lowXminSpinner, "The minimum low value for the particle X size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowXmaxSpinner = new Spinner(xValue.getLowMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowXmaxSpinner.setProgrammaticChangeEvents(false);
        lowXtoggleWidget.table2.add(lowXmaxSpinner).width(spinnerWidth);
        addIbeamListener(lowXmaxSpinner.getTextField());
        addHandListener(lowXmaxSpinner.getButtonPlus());
        addHandListener(lowXmaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowXmaxSpinner);
        addTooltip(lowXmaxSpinner, "The maximum low value for the particle X size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowXcollapseButton = new Button(skin, "moveleft");
        lowXtoggleWidget.table2.add(lowXcollapseButton);
        addHandListener(lowXcollapseButton);
        addTooltip(lowXcollapseButton, "Collapse to define a single low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(lowXcollapseButton, lowXtoggleWidget::swap);

        //Graph small
        var graphX = new LineGraph("Life", lineGraphStyle);
        graphX.setNodes(xValue.getTimeline(), xValue.getScaling());
        graphX.setNodeListener(handListener);
        splitToggleWidget.table2.add(graphX);

        var lowXGraphExpandButton = new Button(skin, "plus");
        splitToggleWidget.table2.add(lowXGraphExpandButton).bottom();
        addHandListener(lowXGraphExpandButton);
        addTooltip(lowXGraphExpandButton, "Expand to large graph view", Align.top, Align.top, tooltipBottomArrowStyle);

        onChange(lowXGraphExpandButton, () -> {
            graphToggleWidget.swap();
            graphExpanded.setNodes(xValue.getTimeline(), xValue.getScaling());
            expandedType = ExpandedType.EXPANDED_X;
        });

        //Y size
        splitToggleWidget.table2.row();
        label = new Label("Y Size", skin, "header");
        splitToggleWidget.table2.add(label).left().padTop(sectionPadding);

        //Relative
        splitToggleWidget.table2.row();
        var relativeYcheckBox = new CheckBox("Relative", skin);
        relativeYcheckBox.setProgrammaticChangeEvents(false);
        splitToggleWidget.table2.add(relativeYcheckBox).left();
        addHandListener(relativeYcheckBox);
        addTooltip(relativeYcheckBox, "If true, the high value is added to the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(relativeYcheckBox, () -> UndoManager.add(new ScaledNumericValueRelativeUndoable(selectedEmitter, yValue, relativeYcheckBox.isChecked(), "change Y Size Relative")));

        //High
        splitToggleWidget.table2.row();
        table = new Table();
        splitToggleWidget.table2.add(table).top();

        table.defaults().space(itemSpacing).left();
        label = new Label("High:", skin);
        table.add(label);

        var highYtoggleWidget = new ToggleGroup();
        table.add(highYtoggleWidget);

        //High single
        highYtoggleWidget.table1.defaults().space(itemSpacing);
        var highYspinner = new Spinner(yValue.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highYspinner.setProgrammaticChangeEvents(false);
        highYtoggleWidget.table1.add(highYspinner).width(spinnerWidth);
        addIbeamListener(highYspinner.getTextField());
        addHandListener(highYspinner.getButtonPlus());
        addHandListener(highYspinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highYspinner);
        addTooltip(highYspinner, "The high value for the particle Y size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var highYexpandButton = new Button(skin, "moveright");
        highYtoggleWidget.table1.add(highYexpandButton);
        addHandListener(highYexpandButton);
        addTooltip(highYexpandButton, "Expand to define a range for the high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(highYexpandButton, highYtoggleWidget::swap);

        //High range
        highYtoggleWidget.table2.defaults().space(itemSpacing);
        var highYminSpinner = new Spinner(yValue.getHighMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highYminSpinner.setProgrammaticChangeEvents(false);
        highYtoggleWidget.table2.add(highYminSpinner).width(spinnerWidth);
        addIbeamListener(highYminSpinner.getTextField());
        addHandListener(highYminSpinner.getButtonPlus());
        addHandListener(highYminSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highYminSpinner);
        addTooltip(highYminSpinner, "The minimum high value for the particle Y size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var highYmaxSpinner = new Spinner(yValue.getHighMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        highYmaxSpinner.setProgrammaticChangeEvents(false);
        highYtoggleWidget.table2.add(highYmaxSpinner).width(spinnerWidth);
        addIbeamListener(highYmaxSpinner.getTextField());
        addHandListener(highYmaxSpinner.getButtonPlus());
        addHandListener(highYmaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(highYmaxSpinner);
        addTooltip(highYmaxSpinner, "The maximum high value for the particle Y size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var highYcollapseButton = new Button(skin, "moveleft");
        highYtoggleWidget.table2.add(highYcollapseButton);
        addHandListener(highYcollapseButton);
        addTooltip(highYcollapseButton, "Collapse to define a single high value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(highYcollapseButton, highYtoggleWidget::swap);

        //Low
        table.row();
        label = new Label("Low:", skin);
        table.add(label);

        var lowYtoggleWidget = new ToggleGroup();
        table.add(lowYtoggleWidget);

        //Low single
        lowYtoggleWidget.table1.defaults().space(itemSpacing);
        var lowYspinner = new Spinner(yValue.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowYspinner.setProgrammaticChangeEvents(false);
        lowYtoggleWidget.table1.add(lowYspinner).width(spinnerWidth);
        addIbeamListener(lowYspinner.getTextField());
        addHandListener(lowYspinner.getButtonPlus());
        addHandListener(lowYspinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowYspinner);
        addTooltip(lowYspinner, "The low value for the particle Y size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowYexpandButton = new Button(skin, "moveright");
        lowYtoggleWidget.table1.add(lowYexpandButton);
        addHandListener(lowYexpandButton);
        addTooltip(lowYexpandButton, "Expand to define a range for the low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(lowYexpandButton, lowYtoggleWidget::swap);

        //Low range
        lowYtoggleWidget.table2.defaults().space(itemSpacing);
        var lowYminSpinner = new Spinner(yValue.getLowMin(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowYminSpinner.setProgrammaticChangeEvents(false);
        lowYtoggleWidget.table2.add(lowYminSpinner).width(spinnerWidth);
        addIbeamListener(lowYminSpinner.getTextField());
        addHandListener(lowYminSpinner.getButtonPlus());
        addHandListener(lowYminSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowYminSpinner);
        addTooltip(lowYminSpinner, "The minimum low value for the particle Y size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowYmaxSpinner = new Spinner(yValue.getLowMax(), 1, SPINNER_DECIMAL_PLACES, Orientation.RIGHT_STACK, spinnerStyle);
        lowYmaxSpinner.setProgrammaticChangeEvents(false);
        lowYtoggleWidget.table2.add(lowYmaxSpinner).width(spinnerWidth);
        addIbeamListener(lowYmaxSpinner.getTextField());
        addHandListener(lowYmaxSpinner.getButtonPlus());
        addHandListener(lowYmaxSpinner.getButtonMinus());
        addUnfocusOnEnterKeyListener(lowYmaxSpinner);
        addTooltip(lowYmaxSpinner, "The maximum low value for the particle Y size in world units.", Align.top, Align.top, tooltipBottomArrowStyle);

        var lowYcollapseButton = new Button(skin, "moveleft");
        lowYtoggleWidget.table2.add(lowYcollapseButton);
        addHandListener(lowYcollapseButton);
        addTooltip(lowYcollapseButton, "Collapse to define a single low value", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(lowYcollapseButton, lowYtoggleWidget::swap);

        //Graph small
        var graphY = new LineGraph("Life", lineGraphStyle);
        graphY.setNodes(yValue.getTimeline(), yValue.getScaling());
        graphY.setNodeListener(handListener);
        splitToggleWidget.table2.add(graphY);

        var lowYgraphExpandButton = new Button(skin, "plus");
        splitToggleWidget.table2.add(lowYgraphExpandButton).bottom();
        addHandListener(lowYgraphExpandButton);
        addTooltip(lowYgraphExpandButton, "Expand to large graph view", Align.top, Align.top, tooltipBottomArrowStyle);

        onChange(lowYgraphExpandButton, () -> {
            graphToggleWidget.swap();
            graphExpanded.setNodes(yValue.getTimeline(), yValue.getScaling());
            expandedType = ExpandedType.EXPANDED_Y;
        });

        onChange(splitXYcheckBox, () -> {
            if (splitXYcheckBox.isChecked()) {
                yValue.setActive(true);
                highXspinner.setValue(xValue.getHighMin());
                highXminSpinner.setValue(xValue.getHighMin());
                highXmaxSpinner.setValue(xValue.getHighMax());
                if (MathUtils.isEqual(xValue.getHighMin(), xValue.getHighMax())) highXtoggleWidget.showTable1();
                else highXtoggleWidget.showTable2();

                lowXspinner.setValue(xValue.getLowMin());
                lowXminSpinner.setValue(xValue.getLowMin());
                lowXmaxSpinner.setValue(xValue.getLowMax());
                if (MathUtils.isEqual(xValue.getLowMin(), xValue.getLowMax())) lowXtoggleWidget.showTable1();
                else lowXtoggleWidget.showTable2();

                highYspinner.setValue(yValue.getHighMin());
                highYminSpinner.setValue(yValue.getHighMin());
                highYmaxSpinner.setValue(yValue.getHighMax());
                if (MathUtils.isEqual(yValue.getHighMin(), yValue.getHighMax())) highYtoggleWidget.showTable1();
                else highYtoggleWidget.showTable2();

                lowYspinner.setValue(yValue.getLowMin());
                lowYminSpinner.setValue(yValue.getLowMin());
                lowYmaxSpinner.setValue(yValue.getLowMax());
                if (MathUtils.isEqual(yValue.getLowMin(), yValue.getLowMax())) lowYtoggleWidget.showTable1();
                else lowYtoggleWidget.showTable2();
            } else {
                yValue.setActive(false);

                highSpinner.setValue(xValue.getHighMin());
                highMinSpinner.setValue(xValue.getHighMin());
                highMaxSpinner.setValue(xValue.getHighMax());
                if (MathUtils.isEqual(xValue.getHighMin(), xValue.getHighMax())) highToggleWidget.showTable1();
                else highToggleWidget.showTable2();

                lowSpinner.setValue(xValue.getLowMin());
                lowMinSpinner.setValue(xValue.getLowMin());
                lowMaxSpinner.setValue(xValue.getLowMax());
                if (MathUtils.isEqual(xValue.getLowMin(), xValue.getLowMax())) lowToggleWidget.showTable1();
                else lowToggleWidget.showTable2();
            }

            graph.setNodes(xValue.getTimeline(), xValue.getScaling());
            graphX.setNodes(xValue.getTimeline(), xValue.getScaling());
            graphY.setNodes(yValue.getTimeline(), yValue.getScaling());

            splitToggleWidget.swap();
        });

        onChange(graphCollapseButton, () -> {
            graph.setNodes(xValue.getTimeline(), xValue.getScaling());
            graphX.setNodes(xValue.getTimeline(), xValue.getScaling());
            graphY.setNodes(yValue.getTimeline(), yValue.getScaling());
            graphToggleWidget.swap();
        });

        var unsplitUndoTemplate = DualScaledNumericValueUndoable
            .builder()
            .emitter(selectedEmitter)
            .xValue(xValue)
            .yValue(yValue)
            .description("change Scale")
            .build();

        var changeListener = onChange(highSpinner, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setHigh(highSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setHigh(highSpinner.getValue());
            UndoManager.add(undo);

            highMinSpinner.setValue(highSpinner.getValue());
            highMaxSpinner.setValue(highSpinner.getValue());
        });
        addInfiniteSlider(highSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(highMinSpinner, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setHighMin(highMinSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setHighMin(highMinSpinner.getValue());
            UndoManager.add(undo);

            highSpinner.setValue(highMinSpinner.getValue());
        });
        addInfiniteSlider(highMinSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(highMaxSpinner, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setHighMax(highMaxSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setHighMax(highMaxSpinner.getValue());
            UndoManager.add(undo);

            highSpinner.setValue(highMaxSpinner.getValue());
        });
        addInfiniteSlider(highMaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        onChange(highCollapseButton, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setHigh(highSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setHigh(highSpinner.getValue());
            UndoManager.add(undo);

            highMinSpinner.setValue(highSpinner.getValue());
            highMaxSpinner.setValue(highSpinner.getValue());
        });

        changeListener = onChange(lowSpinner, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setLow(lowSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setLow(lowSpinner.getValue());
            UndoManager.add(undo);

            lowMinSpinner.setValue(lowSpinner.getValue());
            lowMaxSpinner.setValue(lowSpinner.getValue());
        });
        addInfiniteSlider(lowSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(lowMinSpinner, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setLowMin(lowMinSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setLowMin(lowMinSpinner.getValue());
            UndoManager.add(undo);

            lowSpinner.setValue(lowMinSpinner.getValue());
        });
        addInfiniteSlider(lowMinSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(lowMaxSpinner, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setLowMax(lowMaxSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setLowMax(lowMaxSpinner.getValue());
            UndoManager.add(undo);

            lowSpinner.setValue(lowMaxSpinner.getValue());
        });
        addInfiniteSlider(lowMaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        onChange(lowCollapseButton, () -> {
            var undo = unsplitUndoTemplate.toBuilder().build();
            undo.oldXvalue.set(xValue);
            undo.newXvalue.set(xValue);
            undo.newXvalue.setLow(lowSpinner.getValue());

            undo.oldYvalue.set(yValue);
            undo.newYvalue.set(yValue);
            undo.newYvalue.setLow(lowSpinner.getValue());
            UndoManager.add(undo);

            lowMinSpinner.setValue(lowSpinner.getValue());
            lowMaxSpinner.setValue(lowSpinner.getValue());
        });

        onChange(graph, () -> {
            var nodes = graph.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            addDualGraphUpdateAction(xValue, yValue, newTimeline, newScaling, unsplitUndoTemplate);
        });

        changeListener = onChange(highXspinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setHigh(highXspinner.getValue());
            UndoManager.add(undo);

            highXminSpinner.setValue(highXspinner.getValue());
            highXmaxSpinner.setValue(highXspinner.getValue());
        });
        addInfiniteSlider(highXspinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(highXminSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setHighMin(highXminSpinner.getValue());
            UndoManager.add(undo);

            highXspinner.setValue(highXminSpinner.getValue());
        });
        addInfiniteSlider(highXminSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(highXmaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setHighMax(highXmaxSpinner.getValue());
            UndoManager.add(undo);

            highSpinner.setValue(highXmaxSpinner.getValue());
        });
        addInfiniteSlider(highXmaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        onChange(highXcollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setHigh(highXspinner.getValue());
            UndoManager.add(undo);

            highXmaxSpinner.setValue(highXspinner.getValue());
            highXmaxSpinner.setValue(highXspinner.getValue());
        });

        onChange(lowXspinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setLow(lowXspinner.getValue());
            UndoManager.add(undo);

            lowXminSpinner.setValue(lowXspinner.getValue());
            lowXmaxSpinner.setValue(lowXspinner.getValue());
        });
        addInfiniteSlider(lowXspinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(lowXminSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setLowMin(lowXminSpinner.getValue());
            UndoManager.add(undo);

            lowXspinner.setValue(lowXminSpinner.getValue());
        });
        addInfiniteSlider(lowXminSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(lowXmaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setLowMax(lowXmaxSpinner.getValue());
            UndoManager.add(undo);

            lowSpinner.setValue(lowXmaxSpinner.getValue());
        });
        addInfiniteSlider(lowXmaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(lowXcollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, xValue, "change X Scale");
            undo.oldValue.set(xValue);
            undo.newValue.set(xValue);
            undo.newValue.setLow(lowXspinner.getValue());
            UndoManager.add(undo);

            lowXmaxSpinner.setValue(lowXspinner.getValue());
            lowXmaxSpinner.setValue(lowXspinner.getValue());
        });

        changeListener = onChange(graphX, () -> {
            var nodes = graphX.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            addGraphUpdateAction(xValue, newTimeline, newScaling, "change X Scale");
        });

        changeListener = onChange(highYspinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setHigh(highYspinner.getValue());
            UndoManager.add(undo);

            highYminSpinner.setValue(highYspinner.getValue());
            highYmaxSpinner.setValue(highYspinner.getValue());
        });
        addInfiniteSlider(highYspinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(highYminSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setHighMin(highYminSpinner.getValue());
            UndoManager.add(undo);

            highYspinner.setValue(highYminSpinner.getValue());
        });
        addInfiniteSlider(highYminSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(highYmaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setHighMax(highYmaxSpinner.getValue());
            UndoManager.add(undo);

            highSpinner.setValue(highYmaxSpinner.getValue());
        });
        addInfiniteSlider(highYmaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(highYcollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setHigh(highYspinner.getValue());
            UndoManager.add(undo);

            highYmaxSpinner.setValue(highYspinner.getValue());
            highYmaxSpinner.setValue(highYspinner.getValue());
        });

        changeListener = onChange(lowYspinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setLow(lowYspinner.getValue());
            UndoManager.add(undo);

            lowYminSpinner.setValue(lowYspinner.getValue());
            lowYmaxSpinner.setValue(lowYspinner.getValue());
        });
        addInfiniteSlider(lowYspinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(lowYminSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setLowMin(lowYminSpinner.getValue());
            UndoManager.add(undo);

            lowYspinner.setValue(lowYminSpinner.getValue());
        });
        addInfiniteSlider(lowYminSpinner, sliderIncrement, sliderRange, true, changeListener);

        changeListener = onChange(lowYmaxSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setLowMax(lowYmaxSpinner.getValue());
            UndoManager.add(undo);

            lowSpinner.setValue(lowYmaxSpinner.getValue());
        });
        addInfiniteSlider(lowYmaxSpinner, sliderIncrement, sliderRange, true, changeListener);

        onChange(lowYcollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, yValue, "change Y Scale");
            undo.oldValue.set(yValue);
            undo.newValue.set(yValue);
            undo.newValue.setLow(lowYspinner.getValue());
            UndoManager.add(undo);

            lowYmaxSpinner.setValue(lowYspinner.getValue());
            lowYmaxSpinner.setValue(lowYspinner.getValue());
        });

        onChange(graphY, () -> {
            var nodes = graphY.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            addGraphUpdateAction(yValue, newTimeline, newScaling, "change Y scale");
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

            if (expandedType == ExpandedType.EXPANDED_X) {
                addGraphUpdateAction(xValue, newTimeline, newScaling, "change X scale");
            } else if (expandedType == ExpandedType.EXPANDED_Y) {
                addGraphUpdateAction(yValue, newTimeline, newScaling, "change Y scale");
            } else if (expandedType == ExpandedType.EXPANDED_BOTH) {
                addDualGraphUpdateAction(xValue, yValue, newTimeline, newScaling, unsplitUndoTemplate);
            }
        });
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

    private void addDualGraphUpdateAction(ScaledNumericValue xValue, ScaledNumericValue yValue, float[] newTimeline, float[] newScaling, DualScaledNumericValueUndoable undoTemplate) {
        var oldXvalue = new ScaledNumericValue();
        oldXvalue.set(xValue);

        xValue.setTimeline(newTimeline);
        xValue.setScaling(newScaling);

        var oldYvalue = new ScaledNumericValue();
        oldYvalue.set(yValue);

        yValue.setTimeline(newTimeline);
        yValue.setScaling(newScaling);

        if (graphUndoAction != null) graphUndoAction.restart();
        else {
            graphUndoAction = new TemporalAction(GRAPH_UNDO_DELAY) {
                @Override
                protected void update(float percent) {
                }

                @Override
                protected void end() {
                    var undo = undoTemplate.toBuilder().build();
                    undo.oldXvalue.set(oldXvalue);
                    undo.newXvalue.set(xValue);
                    undo.oldYvalue.set(oldYvalue);
                    undo.newYvalue.set(yValue);
                    UndoManager.add(undo);

                    graphUndoAction = null;
                }
            };
            stage.addAction(graphUndoAction);
        }
    }
}
