/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2021 Raymond Buckley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.ray3k.stripe;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Spinner extends Table implements Disableable {
    private BigDecimal value;
    private Float minimum = -Float.MAX_VALUE;
    private Float maximum = Float.MAX_VALUE;
    private final BigDecimal increment;
    private int decimalPlaces;
    private TextField textField;
    private Button buttonMinus;
    private Button buttonPlus;
    private Actor transversalNext, transversalPrevious;
    public enum Orientation {
        HORIZONTAL, HORIZONTAL_FLIPPED, VERTICAL, VERTICAL_FLIPPED, RIGHT_STACK, LEFT_STACK
    }
    private Orientation orientation;
    private SpinnerStyle style;
    private Action holdAction;
    private static final float HOLD_ACTION_START_DELAY = .2f;
    private static final float HOLD_ACTION_REPEAT_DELAY = .075f;
    private boolean stopNormalPress;

    public Spinner(float value, float increment, int decimalPlaces, Orientation orientation, SpinnerStyle style) {
        this.value = BigDecimal.valueOf(value);
        this.decimalPlaces = decimalPlaces;
        this.orientation = orientation;
        this.increment = BigDecimal.valueOf(increment);
        this.style = style;

        addWidgets();
    }

    public Spinner(float value, float increment, int decimalPlaces, Orientation orientation, Skin skin, String style) {
        this(value, increment, decimalPlaces, orientation, skin.get(style, SpinnerStyle.class));
    }

    public Spinner(float value, float increment, int decimalPlaces, Orientation orientation, Skin skin) {
        this(value, increment, decimalPlaces, orientation, skin, "default");
    }

    private void addWidgets() {
        buttonMinus = new Button(style.buttonMinusStyle);
        buttonPlus = new Button(style.buttonPlusStyle);
        textField = new TextField("", style.textFieldStyle) {
            @Override
            public void next(boolean up) {
                if (up) {
                    if (transversalPrevious != null) {
                        getStage().setKeyboardFocus(transversalPrevious);
                        if (transversalPrevious instanceof TextField) {
                            ((TextField) transversalPrevious).selectAll();
                        }
                    } else {
                        super.next(up);
                    }
                } else {
                    if (transversalNext != null) {
                        getStage().setKeyboardFocus(transversalNext);
                        if (transversalNext instanceof TextField) {
                            ((TextField) transversalNext).selectAll();
                        }
                    } else {
                        super.next(up);
                    }
                }
            }

        };

        textField.setAlignment(Align.center);

        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.UP) {
                    addValue();
                    fire(new ChangeListener.ChangeEvent());
                } else if (keycode == Keys.DOWN) {
                    subtractValue();
                    fire(new ChangeListener.ChangeEvent());
                }
                return false;
            }
        });

        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField1, char c) {
                boolean returnValue = false;
                if ((c >= 48 && c <= 57) || c == 45/* || (decimalPlaces > 0 && c == 46)*/ || c == 46) {
                    returnValue = true;
                }
                return returnValue;
            }
        });
        updateText();

        if (null != orientation) switch (orientation) {
            case HORIZONTAL:
                add(buttonMinus);
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                add(buttonPlus);
                break;
            case HORIZONTAL_FLIPPED:
                add(buttonPlus);
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                add(buttonMinus);
                break;
            case VERTICAL:
                add(buttonPlus);
                row();
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                row();
                add(buttonMinus);
                break;
            case VERTICAL_FLIPPED:
                add(buttonMinus);
                row();
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                row();
                add(buttonPlus);
                break;
            case RIGHT_STACK:
            {
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();

                VerticalGroup group = new VerticalGroup();
                add(group);

                group.addActor(buttonPlus);
                group.addActor(buttonMinus);
                break;
            }
            case LEFT_STACK:
            {
                VerticalGroup group = new VerticalGroup();
                add(group);

                group.addActor(buttonPlus);
                group.addActor(buttonMinus);

                add(textField).prefWidth(35.0f).minWidth(35.0f).growX();
                break;
            }
            default:
                break;
        }

        buttonMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!stopNormalPress) subtractValue();
            }
        });

        buttonMinus.addListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                removeHoldAction();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                addHoldAction(false);
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                removeHoldAction();
            }
        });

        buttonPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!stopNormalPress) addValue();
            }
        });

        buttonPlus.addListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                removeHoldAction();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                addHoldAction(true);
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                removeHoldAction();
            }
        });

        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Spinner parent = (Spinner) actor;
                String text = textField.getText();

                if (text.matches("\\-?(\\d+\\.?\\d*)|(\\.\\d+)")) {
                    float value = Float.parseFloat(text);
                    if (value < parent.minimum) {
                        value = parent.minimum;
                    } else if (value > parent.maximum) {
                        value = parent.maximum;
                    }
                    parent.value = BigDecimal.valueOf(value);
                    parent.value = parent.value.setScale(decimalPlaces, RoundingMode.HALF_UP);
                } else {
                    parent.value = BigDecimal.valueOf(0);
                }
            }
        });

        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) textField.selectAll();

                Spinner parent = (Spinner) textField.getParent();
                if (parent.value.floatValue() < parent.minimum) {
                    parent.value = BigDecimal.valueOf(parent.minimum);
                }
                if (parent.value.floatValue() > parent.maximum) {
                    parent.value = BigDecimal.valueOf(parent.maximum);
                }
                parent.updateText();
            }

        });

        final Spinner spinner = this;
        addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.setTarget(spinner);
            }
        });
    }

    private void addHoldAction(boolean increase) {
        stopNormalPress = false;
        holdAction = Actions.delay(HOLD_ACTION_START_DELAY, Actions.forever(Actions.delay(HOLD_ACTION_REPEAT_DELAY, Actions.run(() -> {
            fire(new ChangeEvent());
            stopNormalPress = true;
            if (increase) addValue();
            else subtractValue();
        }))));
        addAction(holdAction);
    }

    private void removeHoldAction() {
        stopNormalPress = false;
        if (holdAction == null) return;

        removeAction(holdAction);
        holdAction = null;
    }

    private void subtractValue() {
        value = value.subtract(increment);
        if (value.floatValue() < minimum) {
            value = BigDecimal.valueOf(minimum);
        }
        if (value.floatValue() > maximum) {
            value = BigDecimal.valueOf(maximum);
        }
        updateText();
    }

    private void addValue() {
        value = value.add(increment);
        if (value.floatValue() < minimum) {
            value = BigDecimal.valueOf(minimum);
        }
        if (value.floatValue() > maximum) {
            value = BigDecimal.valueOf(maximum);
        }
        updateText();
    }

    public float getValue() {
        return value.floatValue();
    }

    public boolean isInt() {
        return isIntegerValue(value);
    }

    public int getValueAsInt() {
        return value.intValue();
    }

    public void setValue(float value) {
        setValue(BigDecimal.valueOf(value));
    }

    public void setValue(BigDecimal value) {
        this.value = value.setScale(decimalPlaces, RoundingMode.HALF_UP);
        updateText();
    }

    public float getMinimum() {
        return minimum;
    }

    public void setMinimum(float minimum) {
        this.minimum = minimum;
    }

    public float getMaximum() {
        return maximum;
    }

    public void setMaximum(float maximum) {
        this.maximum = maximum;
    }

    private void updateText() {
        value = value.setScale(decimalPlaces, RoundingMode.HALF_UP);
        textField.setText(value.toString());
    }

    static public class SpinnerStyle {
        public ButtonStyle buttonMinusStyle, buttonPlusStyle;
        public TextFieldStyle textFieldStyle;

        public SpinnerStyle() {

        }

        public SpinnerStyle(ButtonStyle buttonMinusStyle, ButtonStyle buttonPlusStyle, TextFieldStyle textFieldStyle) {
            this.buttonMinusStyle = buttonMinusStyle;
            this.buttonPlusStyle = buttonPlusStyle;
            this.textFieldStyle = textFieldStyle;
        }

        public SpinnerStyle(SpinnerStyle style) {
            buttonMinusStyle = style.buttonMinusStyle;
            buttonPlusStyle = style.buttonPlusStyle;
            textFieldStyle = style.textFieldStyle;
        }
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButtonMinus() {
        return buttonMinus;
    }

    public Button getButtonPlus() {
        return buttonPlus;
    }

    public Actor getTransversalNext() {
        return transversalNext;
    }

    public void setTransversalNext(Actor transversalNext) {
        this.transversalNext = transversalNext;
    }

    public Actor getTransversalPrevious() {
        return transversalPrevious;
    }

    public void setTransversalPrevious(Actor transversalPrevious) {
        this.transversalPrevious = transversalPrevious;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;

        clear();
        addWidgets();
    }

    public SpinnerStyle getStyle() {
        return style;
    }

    public void setStyle(SpinnerStyle style) {
        this.style = style;

        clear();
        addWidgets();
    }

    public static boolean isIntegerValue(BigDecimal bigDecimal) {
        return bigDecimal.signum() == 0 || bigDecimal.scale() <= 0 || bigDecimal.stripTrailingZeros().scale() <= 0;
    }

    private boolean disabled;

    @Override
    public void setDisabled(boolean isDisabled) {
        disabled = isDisabled;
        buttonMinus.setDisabled(disabled);
        buttonPlus.setDisabled(disabled);
        textField.setDisabled(disabled);
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setProgrammaticChangeEvents(boolean programmaticChangeEvents) {
        buttonMinus.setProgrammaticChangeEvents(programmaticChangeEvents);
        buttonPlus.setProgrammaticChangeEvents(programmaticChangeEvents);
        textField.setProgrammaticChangeEvents(programmaticChangeEvents);
    }
}
