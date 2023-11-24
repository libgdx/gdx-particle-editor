package com.ray3k.particleparkpro.widgets.styles;

import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.ray3k.particleparkpro.widgets.ColorGraph.ColorGraphStyle;
import com.ray3k.particleparkpro.widgets.EditableLabel.EditableLabelStyle;
import com.ray3k.particleparkpro.widgets.InfSlider.InfSliderStyle;
import com.ray3k.particleparkpro.widgets.LineGraph.LineGraphStyle;
import com.ray3k.stripe.DraggableList.DraggableListStyle;
import com.ray3k.stripe.DraggableTextList.DraggableTextListStyle;
import com.ray3k.stripe.PopColorPicker.PopColorPickerStyle;
import com.ray3k.stripe.PopTable.PopTableStyle;
import com.ray3k.stripe.ResizeWidget.ResizeWidgetStyle;
import com.ray3k.stripe.Spinner.SpinnerStyle;

import static com.ray3k.particleparkpro.Core.skin;

/**
 * Collection of styles for custom widgets that are not provided in the skin JSON.
 */
public class Styles {
    public static LineGraphStyle lineGraphStyle;
    public static LineGraphStyle lineGraphBigStyle;
    public static ColorGraphStyle colorGraphStyle;
    public static SpinnerStyle spinnerStyle;
    public static ResizeWidgetStyle resizeWidgetStyle;
    public static DraggableListStyle draggableListStyle;
    public static DraggableTextListStyle draggableTextListStyle;
    public static DraggableTextListStyle draggableTextListNoBgStyle;
    public static InfSliderStyle infSliderStyle;
    public static PopTableStyle tooltipBottomArrowStyle;
    public static PopTableStyle tooltipBottomLeftArrowStyle;
    public static PopTableStyle tooltipBottomRightArrowStyle;
    public static PopTableStyle tooltipTopArrowStyle;
    public static PopTableStyle tooltipRightArrowStyle;
    public static PopTableStyle tooltipLeftArrowStyle;
    public static EditableLabelStyle editableLabelStyle;
    public static PopColorPickerStyle popColorPickerStyle;

    public static void initializeStyles() {
        popColorPickerStyle = new PPcolorPickerStyle();
        lineGraphStyle = new PPlineGraphStyle();
        lineGraphBigStyle = new PPlineGraphBigStyle();
        colorGraphStyle = new PPcolorGraphStyle();
        spinnerStyle = new PPspinnerStyle();
        resizeWidgetStyle = new PPresizeWidgetStyle();
        draggableListStyle = new PPdraggableListStyle();
        draggableTextListStyle = new PPdraggableTextListStyle();
        draggableTextListNoBgStyle = new PPdraggableTextListNoBGStyle();
        infSliderStyle = new PPinfSliderStyle();
        tooltipBottomArrowStyle = new PopTableStyle(skin.get("tooltip-bottom-arrow", WindowStyle.class));
        tooltipBottomLeftArrowStyle = new PopTableStyle(skin.get("tooltip-bottom-left-arrow", WindowStyle.class));
        tooltipBottomRightArrowStyle = new PopTableStyle(skin.get("tooltip-bottom-right-arrow", WindowStyle.class));
        tooltipTopArrowStyle = new PopTableStyle(skin.get("tooltip-top-arrow", WindowStyle.class));
        tooltipRightArrowStyle = new PopTableStyle(skin.get("tooltip-right-arrow", WindowStyle.class));
        tooltipLeftArrowStyle = new PopTableStyle(skin.get("tooltip-left-arrow", WindowStyle.class));
        editableLabelStyle = new PPeditableLabelStyle();
    }
}
