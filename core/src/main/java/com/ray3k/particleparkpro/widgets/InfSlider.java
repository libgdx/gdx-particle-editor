package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

/**
 * A slider that automatically repositions back to the center when the knob is released. When the knob is moved beyond
 * its normal limits, it continues to receive input. This enables the user to input values in any direction and without
 * any maximum/minimum value.
 */
public class InfSlider extends WidgetGroup implements Disableable {
    private float internalMinimum;
    private float internalMaximum;
    private float range;
    private float baseValue;
    private float internalValue;
    private float visualValue;
    private float increment;
    private final InfSliderStyle style;
    private final Image background;
    private final Button knob;
    private final Image progressKnob;
    private boolean vertical;
    private final boolean lockToIntegerPositions;
    private static final Vector2 temp = new Vector2();
    private boolean disabled;

    public InfSlider(Skin skin) {
        this(skin, "default-horizontal");
    }

    public InfSlider(Skin skin, String style) {
        this(skin.get(style, InfSliderStyle.class));
    }

    public InfSlider(final InfSliderStyle style) {
        setTouchable(Touchable.enabled);

        internalMinimum = -1;
        internalMaximum = 1;
        internalValue = 0f;
        visualValue = internalValue;
        increment = .1f;
        this.style = style;
        vertical = false;
        lockToIntegerPositions = true;

        background = new Image(style.background);
        background.setScaling(Scaling.stretch);
        addActor(background);

        progressKnob = new Image(style.progressKnob);
        progressKnob.setScaling(Scaling.stretch);
        addActor(progressKnob);

        ButtonStyle knobStyle = new ButtonStyle();
        knobStyle.up = style.knobUp;
        knobStyle.over = style.knobOver;
        knobStyle.down = style.knobDown;
        knobStyle.checked = style.knobDown;
        knobStyle.disabled = style.knobDisabled;
        knob = new Button(knobStyle);
        knob.setProgrammaticChangeEvents(false);
        addActor(knob);
        ChangeListener disableCheckingListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Button) actor).setChecked(false);
                event.cancel();
            }
        };
        knob.addListener(disableCheckingListener);
        knob.addListener(new DragListener() {
            {
                setTapSquareSize(0);
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    knob.setTouchable(Touchable.disabled);
                    knob.setChecked(true);
                    swapActor(getChildren().indexOf(knob, true), getChildren().size - 1);
                }
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    float previousValue = internalValue;

                    calculateInternalValue();
                    internalValue = snap(internalValue, increment);
                    visualValue = MathUtils.clamp(internalValue, internalMinimum, internalMaximum);
                    if (!MathUtils.isEqual(internalValue, previousValue)) {
                        updateKnobs();
                        fire(new ValueBeginChangeEvent(baseValue + internalValue));
                        fire(new ChangeEvent());
                    }
                }
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    knob.setTouchable(Touchable.enabled);
                    knob.setChecked(false);

                    baseValue += internalValue;
                    internalValue = 0;
                    visualValue = MathUtils.clamp(internalValue, internalMinimum, internalMaximum);
                    updateKnobs();
                }
            }
        });
    }

    private void calculateInternalValue() {
        float padLeft = style.background == null ? 0 : style.background.getLeftWidth();
        float padRight = style.background == null ? 0 : style.background.getRightWidth();
        float padBottom = style.background == null ? 0 : style.background.getTopHeight();
        float padTop = style.background == null ? 0 : style.background.getBottomHeight();
        float innerWidth = MathUtils.clamp(getWidth() - padLeft - padRight, 0, getWidth());
        float innerHeight = MathUtils.clamp(getHeight() - padBottom - padTop, 0, getHeight());
        temp.set(Gdx.input.getX(), Gdx.input.getY());
        background.screenToLocalCoordinates(temp);

        if (!vertical) {
            internalValue = (temp.x - padLeft) / innerWidth * (internalMaximum - internalMinimum) + internalMinimum;
        } else {
            internalValue = (temp.y - padBottom) / innerHeight * (internalMaximum - internalMinimum) + internalMinimum;
        }
    }

    @Override
    public void layout() {
        background.setSize(getWidth(), getHeight());

        updateKnobs();
    }

    private void updateKnobs() {
        float padLeft = style.background == null ? 0 : style.background.getLeftWidth();
        float padRight = style.background == null ? 0 : style.background.getRightWidth();
        float padBottom = style.background == null ? 0 : style.background.getBottomHeight();
        float padTop = style.background == null ? 0 : style.background.getTopHeight();
        float innerWidth = MathUtils.clamp(getWidth() - padLeft - padRight, 0, getWidth());
        float innerHeight = MathUtils.clamp(getHeight() - padBottom - padTop, 0, getHeight());

        if (!vertical) {
            progressKnob.setX(padLeft + (visualValue - internalMinimum) / (internalMaximum - internalMinimum) * innerWidth);
            progressKnob.setY(padBottom + MathUtils.round(innerHeight / 2f - progressKnob.getPrefHeight() / 2f));
            progressKnob.setWidth(padLeft + visualValue / (internalMaximum - internalMinimum) * innerWidth);

            knob.setX(padLeft + (visualValue - internalMinimum) / (internalMaximum - internalMinimum) * innerWidth - knob.getPrefWidth() / 2f);
            knob.setY(padBottom + innerHeight / 2f - knob.getPrefHeight() / 2f);
            if (lockToIntegerPositions) knob.setPosition(MathUtils.round(knob.getX()),
                MathUtils.round(knob.getY()));
        } else {
            progressKnob.setX(padLeft + innerWidth / 2f - progressKnob.getPrefWidth() / 2f);
            progressKnob.setY(padBottom + (visualValue - internalMinimum) / (internalMaximum - internalMinimum) * innerHeight - knob.getPrefHeight() / 2f);
            progressKnob.setHeight(padBottom + visualValue * innerHeight);

            knob.setX(padLeft + innerWidth / 2f - knob.getPrefWidth() / 2f);
            knob.setY(padBottom + (visualValue - internalMinimum) / (internalMaximum - internalMinimum) * innerHeight - knob.getPrefHeight() / 2f);
            if (lockToIntegerPositions) knob.setPosition(MathUtils.round(knob.getX()),
                MathUtils.round(knob.getY()));
        }

        if (disabled) {
            background.setDrawable(style.backgroundDisabled);
            knob.setDisabled(true);
            progressKnob.setDrawable(style.progressKnobDisabled);
        } else {
            background.setDrawable(style.background);
            knob.setDisabled(false);
            progressKnob.setDrawable(style.progressKnob);
        }
    }

    @Override
    public float getPrefWidth() {
        return style.background == null ? 0 : style.background.getMinWidth();
    }

    @Override
    public float getPrefHeight() {
        return style.background == null ? 0 : style.background.getMinHeight();
    }

    public float getValue() {
        return baseValue + internalValue;
    }

    public void setValue(float value) {
        this.baseValue = value;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
        internalMinimum = -range / 2;
        internalMaximum = range / 2;
    }

    public float getIncrement() {
        return increment;
    }

    public void setIncrement(float increment) {
        this.increment = increment;
    }

    public float getVisualValue() {
        return visualValue;
    }

    public Button getKnob() {
        return knob;
    }

    public Image getBackground() {
        return background;
    }

    private static float snap(float value, float increment) {
        int whole = MathUtils.floor(value / increment);
        float first = whole * increment;
        float second = first + increment;
        return value - first < second - value ? first : second;
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        disabled = isDisabled;
        updateKnobs();
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public static class ValueBeginChangeEvent extends Event {
        public float value;
        public ValueBeginChangeEvent(float value) {
            this.value = value;
        }
    }

    public static class ValueEndChangeEvent extends Event {
        public float value;
        public ValueEndChangeEvent(float value) {
            this.value = value;
        }
    }

    public static abstract class ValueBeginChangeListener implements EventListener {
        public boolean handle (Event event) {
            if (!(event instanceof ValueBeginChangeEvent)) return false;
            changed((ValueBeginChangeEvent) event, ((ValueBeginChangeEvent) event).value, event.getTarget());
            return false;
        }

        /**
         * Event
         * @param event The event associated with the value change.
         * @param valueBegin The beginning value.
         * @param actor The event target, which is the actor that emitted the change event.
         */
        abstract public void changed (ValueBeginChangeEvent event, float valueBegin, Actor actor);
    }

    public static abstract class ValueEndChangeListener implements EventListener {
        public boolean handle (Event event) {
            if (!(event instanceof ValueEndChangeEvent)) return false;
            changed((ValueEndChangeEvent)event, ((ValueEndChangeEvent) event).value, event.getTarget());
            return false;
        }


        /**
         *
         * @param event The event associated with the value change.
         * @param valueEnd The ending value
         * @param actor The event target, which is the actor that emitted the change event.
         */
        abstract public void changed (ValueEndChangeEvent event, float valueEnd, Actor actor);
    }

    public static class InfSliderStyle {
        /**Optional**/
        public Drawable background;
        public Drawable progressKnob;
        public Drawable knobUp;
        public Drawable knobOver;
        public Drawable knobDown;
        public Drawable knobDisabled;
        public Drawable backgroundDisabled;
        public Drawable progressKnobDisabled;
    }
}

