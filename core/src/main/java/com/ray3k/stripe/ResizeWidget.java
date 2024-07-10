/*
 * The MIT License
 *
 * Copyright (c) 2021 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.stripe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

/**
 * This widget allows the user to free transform of a single child by providing resize handles and allowing dragging.
 * The size and position of the child is restricted to the bounds of the ResizeWidget, so ensure that you set a
 * sufficient size ahead of time.
 */
public class ResizeWidget extends WidgetGroup {
    private ResizeWidgetStyle style;
    private Button topLeftHandle, topHandle, topRightHandle, rightHandle, bottomRightHandle, bottomHandle, bottomLeftHandle, leftHandle;
    private Actor actor;
    private Stack stack;
    private Image foreground;
    private float minWidth;
    private float minHeight;
    private boolean preventDrag;
    public boolean resizingFromCenter;
    public boolean allowDragging = true;
    public boolean centerOnLayout = true;

    public ResizeWidget(Actor actor, Skin skin) {
        this(actor, skin, "default");
    }

    public ResizeWidget(Actor actor, Skin skin, String styleName) {
        this(actor, skin.get(styleName, ResizeWidgetStyle.class));
    }

    public ResizeWidget(Actor actor, ResizeWidgetStyle style) {
        setTransform(false);
        setTouchable(Touchable.enabled);
        resizingFromCenter = false;
        this.actor = actor;

        if (style != null) {
            this.style = style;
        } else {
            this.style = new ResizeWidgetStyle();
            style = this.style;
        }

        if (style.handle != null || style.handleOver != null || style.handlePressed != null) {
            ButtonStyle handleStyle = new ButtonStyle();
            handleStyle.up = style.handle;
            handleStyle.down = style.handlePressed;
            handleStyle.checked = style.handlePressed;
            handleStyle.checkedOver = style.handlePressed;
            handleStyle.over = style.handleOver;

            topLeftHandle = new Button(handleStyle);
            DragListener dragListener = new DragListener() {
                private float startX, startY;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        topLeftHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        topLeftHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startX = x;
                    startY = y;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float width = startX - x + stack.getWidth();
                    float height = y - startY + stack.getHeight();
                    float xPos = x - startX + stack.getX();
                    float yPos = stack.getY();

                    if (resizingFromCenter) {
                        width += startX - x;
                        height += y - startY;

                        yPos -= y - startY;
                    }

                    if (width < getMinWidth()) {
                        width = getMinWidth();

                        if (resizingFromCenter) {
                            xPos = stack.getX() + (stack.getWidth() - getMinWidth()) / 2.0f;
                        } else {
                            xPos = stack.getX() + stack.getWidth() - getMinWidth();
                        }
                    }

                    if (height < getMinHeight()) {
                        height = getMinHeight();

                        if (resizingFromCenter) {
                            yPos = stack.getY() + (stack.getHeight() - getMinHeight()) / 2.0f;
                        }
                    }

                    if (xPos < 0) {
                        xPos = 0;
                    }
                    if (xPos + width > getWidth()) {
                        width = getWidth() - xPos;
                    }
                    if (yPos < 0) {
                        yPos = 0;
                    }
                    if (yPos + height > getHeight()) {
                        height = getHeight() - yPos;
                    }

                    stack.setPosition(MathUtils.round(xPos), MathUtils.round(yPos));
                    stack.setSize(width, height);
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            topLeftHandle.addListener(dragListener);

            topRightHandle = new Button(handleStyle);
            dragListener = new DragListener() {
                private float startX, startY;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        topRightHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        topRightHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startX = x;
                    startY = y;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float width = x - startX + stack.getWidth();
                    float height = y - startY + stack.getHeight();
                    float xPos = stack.getX();
                    float yPos = stack.getY();

                    if (resizingFromCenter) {
                        width += x - startX;
                        height += y - startY;

                        xPos -= x - startX;
                        yPos -= y - startY;
                    }

                    if (width < getMinWidth()) {
                        width = getMinWidth();

                        if (resizingFromCenter) {
                            xPos = stack.getX() + (stack.getWidth() - getMinWidth()) / 2.0f;
                        }
                    }

                    if (height < getMinHeight()) {
                        height = getMinHeight();

                        if (resizingFromCenter) {
                            yPos = stack.getY() + (stack.getHeight() - getMinHeight()) / 2.0f;
                        }
                    }

                    if (xPos < 0) {
                        xPos = 0;
                    }
                    if (xPos + width > getWidth()) {
                        width = getWidth() - xPos;
                    }
                    if (yPos < 0) {
                        yPos = 0;
                    }
                    if (yPos + height > getHeight()) {
                        height = getHeight() - yPos;
                    }

                    stack.setPosition(MathUtils.round(xPos), MathUtils.round(yPos));
                    stack.setSize(width, height);
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            topRightHandle.addListener(dragListener);

            bottomRightHandle = new Button(handleStyle);
            dragListener = new DragListener() {
                private float startX, startY;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        bottomRightHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        bottomRightHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startX = x;
                    startY = y;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float width = x - startX + stack.getWidth();
                    float height = startY - y + stack.getHeight();
                    float xPos = stack.getX();
                    float yPos = y - startY + stack.getY();

                    if (resizingFromCenter) {
                        width += x - startX;
                        height += startY - y;

                        xPos -= x - startX;
                    }

                    if (width < getMinWidth()) {
                        width = getMinWidth();

                        if (resizingFromCenter) {
                            xPos = stack.getX() + (stack.getWidth() - getMinWidth()) / 2.0f;
                        }
                    }

                    if (height < getMinHeight()) {
                        height = getMinHeight();

                        if (resizingFromCenter) {
                            yPos = stack.getY() + (stack.getHeight() - getMinHeight()) / 2.0f;
                        } else {
                            yPos = stack.getY() + stack.getHeight() - getMinHeight();
                        }
                    }

                    if (xPos < 0) {
                        xPos = 0;
                    }
                    if (xPos + width > getWidth()) {
                        width = getWidth() - xPos;
                    }
                    if (yPos < 0) {
                        yPos = 0;
                    }
                    if (yPos + height > getHeight()) {
                        height = getHeight() - yPos;
                    }

                    stack.setPosition(MathUtils.round(xPos), MathUtils.round(yPos));
                    stack.setSize(width, height);
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            bottomRightHandle.addListener(dragListener);

            bottomLeftHandle = new Button(handleStyle);
            dragListener = new DragListener() {
                private float startX, startY;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        bottomLeftHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        bottomLeftHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startX = x;
                    startY = y;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float width = startX - x + stack.getWidth();
                    float height = startY - y + stack.getHeight();
                    float xPos = x - startX + stack.getX();
                    float yPos = y - startY + stack.getY();

                    if (resizingFromCenter) {
                        width += startX - x;
                        height += startY - y;
                    }

                    if (width < getMinWidth()) {
                        width = getMinWidth();

                        if (resizingFromCenter) {
                            xPos = stack.getX() + (stack.getWidth() - getMinWidth()) / 2.0f;
                        } else {
                            xPos = stack.getX() + stack.getWidth() - getMinWidth();
                        }
                    }

                    if (height < getMinHeight()) {
                        height = getMinHeight();

                        if (resizingFromCenter) {
                            yPos = stack.getY() + (stack.getHeight() - getMinHeight()) / 2.0f;
                        } else {
                            yPos = stack.getY() + stack.getHeight() - getMinHeight();
                        }
                    }

                    if (xPos < 0) {
                        xPos = 0;
                    }
                    if (xPos + width > getWidth()) {
                        width = getWidth() - xPos;
                    }
                    if (yPos < 0) {
                        yPos = 0;
                    }
                    if (yPos + height > getHeight()) {
                        height = getHeight() - yPos;
                    }

                    stack.setPosition(MathUtils.round(xPos), MathUtils.round(yPos));
                    stack.setSize(width, height);
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            bottomLeftHandle.addListener(dragListener);
        }

        if (style.minorHandle != null || style.minorHandleOver != null || style.minorHandlePressed != null) {
            ButtonStyle minorHandleStyle = new ButtonStyle();
            minorHandleStyle.up = style.minorHandle;
            minorHandleStyle.down = style.minorHandlePressed;
            minorHandleStyle.checked = style.minorHandlePressed;
            minorHandleStyle.checkedOver = style.minorHandlePressed;
            minorHandleStyle.over = style.minorHandleOver;

            topHandle = new Button(minorHandleStyle);
            DragListener dragListener = new DragListener() {
                private float startY;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        topHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        topHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startY = y;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float height = y - startY + stack.getHeight();
                    if (resizingFromCenter) {
                        height += y - startY;
                    }

                    float yPos = stack.getY();
                    if (resizingFromCenter) {
                        yPos -= y - startY;
                    }

                    if (height < getMinHeight()) {
                        height = getMinHeight();

                        if (resizingFromCenter) {
                            yPos = stack.getY() + (stack.getHeight() - getMinHeight()) / 2.0f;
                        }
                    }

                    if (yPos < 0) {
                        yPos = 0;
                    }
                    if (yPos + height > getHeight()) {
                        height = getHeight() - yPos;
                    }

                    stack.setPosition(MathUtils.round(stack.getX()), MathUtils.round(yPos));
                    stack.setSize(stack.getWidth(), height);
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            topHandle.addListener(dragListener);

            rightHandle = new Button(minorHandleStyle);
            dragListener = new DragListener() {
                private float startX;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        rightHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        rightHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startX = x;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float width = x - startX + stack.getWidth();
                    if (resizingFromCenter) {
                        width += x - startX;
                    }
                    float xPos = stack.getX();
                    if (resizingFromCenter) {
                        xPos -= x - startX;
                    }

                    if (width < getMinWidth()) {
                        width = getMinWidth();

                        if (resizingFromCenter) {
                            xPos = stack.getX() + (stack.getWidth() - getMinWidth()) / 2.0f;
                        }
                    }

                    if (xPos < 0) {
                        xPos = 0;
                    }
                    if (xPos + width > getWidth()) {
                        width = getWidth() - xPos;
                    }

                    stack.setPosition(MathUtils.round(xPos), MathUtils.round(stack.getY()));
                    stack.setSize(width, stack.getHeight());
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            rightHandle.addListener(dragListener);

            bottomHandle = new Button(minorHandleStyle);
            dragListener = new DragListener() {
                private float startY;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        bottomHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        bottomHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startY = y;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float height = startY - y + stack.getHeight();
                    if (resizingFromCenter) {
                        height += startY - y;
                    }

                    float yPos = y - startY + stack.getY();

                    if (height < getMinHeight()) {
                        height = getMinHeight();

                        if (resizingFromCenter) {
                            yPos = stack.getY() + (stack.getHeight() - getMinHeight()) / 2.0f;
                        } else {
                            yPos = stack.getY() + stack.getHeight() - getMinHeight();
                        }
                    }

                    if (yPos < 0) {
                        yPos = 0;
                    }
                    if (yPos + height > getHeight()) {
                        height = getHeight() - yPos;
                    }

                    stack.setPosition(MathUtils.round(stack.getX()), MathUtils.round(yPos));
                    stack.setSize(stack.getWidth(), height);
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            bottomHandle.addListener(dragListener);

            leftHandle = new Button(minorHandleStyle);
            dragListener = new DragListener() {
                private float startX;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        leftHandle.setChecked(true);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Buttons.LEFT) {
                        leftHandle.setChecked(false);
                    }
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startX = x;
                    preventDrag = true;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    float width = startX - x + stack.getWidth();
                    if (resizingFromCenter) {
                        width += startX - x;
                    }

                    float xPos = x - startX + stack.getX();

                    if (width < getMinWidth()) {
                        width = getMinWidth();

                        if (resizingFromCenter) {
                            xPos = stack.getX() + (stack.getWidth() - getMinWidth()) / 2.0f;
                        } else {
                            xPos = stack.getX() + stack.getWidth() - getMinWidth();
                        }
                    }

                    if (xPos < 0) {
                        xPos = 0;
                    }
                    if (xPos + width > getWidth()) {
                        width = getWidth() - xPos;
                    }

                    stack.setPosition(MathUtils.round(xPos), MathUtils.round(stack.getY()));
                    stack.setSize(width, stack.getHeight());
                    stack.validate();
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    preventDrag = false;
                }
            };
            dragListener.setTapSquareSize(0);
            leftHandle.addListener(dragListener);
        }

        stack = new Stack();
        addActor(stack);
        stack.setTouchable(Touchable.enabled);

        if (style.foreground != null) {
            foreground = new Image(style.foreground);
            foreground.setTouchable(Touchable.disabled);
            foreground.setScaling(Scaling.stretch);
        } else {
            foreground = null;
        }

        if (topLeftHandle != null) topLeftHandle.setColor(1, 1, 1, 0);
        if (topHandle != null) topHandle.setColor(1, 1, 1, 0);
        if (topRightHandle != null) topRightHandle.setColor(1, 1, 1, 0);
        if (rightHandle != null) rightHandle.setColor(1, 1, 1, 0);
        if (bottomRightHandle != null) bottomRightHandle.setColor(1, 1, 1, 0);
        if (bottomHandle != null) bottomHandle.setColor(1, 1, 1, 0);
        if (bottomLeftHandle != null) bottomLeftHandle.setColor(1, 1, 1, 0);
        if (leftHandle != null) leftHandle.setColor(1, 1, 1, 0);
        if (foreground != null) foreground.setColor(1, 1, 1, 0);

        stack.addListener(new DragListener() {
            float xStart;
            float yStart;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                xStart = x;
                yStart = y;
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                float xPos = stack.getX() + x - xStart;
                float yPos = stack.getY() + y - yStart;

                xPos = MathUtils.clamp(xPos, 0, getWidth() - stack.getWidth());
                yPos = MathUtils.clamp(yPos, 0, getHeight() - stack.getHeight());

                if (!preventDrag && allowDragging) stack.setPosition(MathUtils.round(xPos), MathUtils.round(yPos));
            }
        });

        stack.addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (toActor != null && !toActor.isDescendantOf(stack)) {
                    stack.addAction(new Action() {
                        @Override
                        public boolean act(float delta) {
                            if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
                                return false;
                            } else {
                                if (!isOver()) {
                                    if (topLeftHandle != null) topLeftHandle.addAction(Actions.fadeOut(.25f));
                                    if (topHandle != null) topHandle.addAction(Actions.fadeOut(.25f));
                                    if (topRightHandle != null) topRightHandle.addAction(Actions.fadeOut(.25f));
                                    if (rightHandle != null) rightHandle.addAction(Actions.fadeOut(.25f));
                                    if (bottomRightHandle != null) bottomRightHandle.addAction(Actions.fadeOut(.25f));
                                    if (bottomHandle != null) bottomHandle.addAction(Actions.fadeOut(.25f));
                                    if (bottomLeftHandle != null) bottomLeftHandle.addAction(Actions.fadeOut(.25f));
                                    if (leftHandle != null) leftHandle.addAction(Actions.fadeOut(.25f));
                                    if (foreground != null) foreground.addAction(Actions.fadeOut(.25f));
                                }
                                return true;
                            }
                        }
                    });
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (fromActor != null && !fromActor.isDescendantOf(stack)) {
                    if (topLeftHandle != null) topLeftHandle.addAction(Actions.fadeIn(.25f));
                    if (topHandle != null) topHandle.addAction(Actions.fadeIn(.25f));
                    if (topRightHandle != null) topRightHandle.addAction(Actions.fadeIn(.25f));
                    if (rightHandle != null) rightHandle.addAction(Actions.fadeIn(.25f));
                    if (bottomRightHandle != null) bottomRightHandle.addAction(Actions.fadeIn(.25f));
                    if (bottomHandle != null) bottomHandle.addAction(Actions.fadeIn(.25f));
                    if (bottomLeftHandle != null) bottomLeftHandle.addAction(Actions.fadeIn(.25f));
                    if (leftHandle != null) leftHandle.addAction(Actions.fadeIn(.25f));
                    if (foreground != null) foreground.addAction(Actions.fadeIn(.25f));
                }
            }
        });

        populate();
    }

    private void populate() {
        stack.clearChildren();

        Table table = new Table();
        stack.add(table);

        if (style.background != null) {
            table.setBackground(style.background);
        }

        if (actor != null) {
            table.add(actor).grow();
        }

        if (foreground != null) {
            stack.add(foreground);
        }

        Table buttonTable = new Table();
        stack.add(buttonTable);

        buttonTable.add(topLeftHandle);
        buttonTable.add(topHandle).expandX();
        buttonTable.add(topRightHandle);

        buttonTable.row();
        buttonTable.add(leftHandle).expandY();
        buttonTable.add().expand();
        buttonTable.add(rightHandle).expandY();

        buttonTable.row();
        buttonTable.add(bottomLeftHandle);
        buttonTable.add(bottomHandle).expandX();
        buttonTable.add(bottomRightHandle);
    }

    @Override
    public void layout() {
        if (stack.getWidth() < getMinWidth()) {
            stack.setWidth(getMinWidth());
        }
        if (stack.getX() < 0) {
            stack.setX(0);
        }
        if (stack.getX() + stack.getWidth() > getWidth()) {
            stack.setWidth((int) (getWidth() - stack.getX()));
        }

        if (stack.getHeight() < getMinHeight()) {
            stack.setHeight(getMinHeight());
        }
        if (stack.getY() < 0) {
            stack.setY(0);
        }
        if (stack.getY() + stack.getHeight() > getHeight()) {
            stack.setHeight((int) (getHeight() - stack.getY()));
        }

        if (centerOnLayout) stack.setPosition(getWidth() / 2.0f, getHeight() / 2.0f, Align.center);
        stack.setPosition(MathUtils.round(stack.getX()), MathUtils.round(stack.getY()));
    }

    /**
     * Set to zero to differ to default
     *
     * @param minWidth
     */
    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    /**
     * Set to zero to differ to default
     *
     * @param minHeight
     */
    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    @Override
    public float getMinHeight() {
        return MathUtils.isZero(minHeight) ? stack.getMinHeight() : minHeight;
    }

    @Override
    public float getMinWidth() {
        return MathUtils.isZero(minWidth) ? stack.getMinWidth() : minWidth;
    }

    public void setActor(Actor actor) {
        this.actor = actor;

        populate();
    }

    public Actor getActor() {
        return actor;
    }

    public static class ResizeWidgetStyle {

        /**
         * Optional
         */
        public Drawable foreground, background, handle, handleOver, handlePressed;
        public Drawable minorHandle, minorHandleOver, minorHandlePressed;

        public ResizeWidgetStyle() {

        }

        public ResizeWidgetStyle(ResizeWidgetStyle style) {
            foreground = style.foreground;
            background = style.background;
            handle = style.handle;
            handleOver = style.handleOver;
            handlePressed = style.handlePressed;
            minorHandle = style.minorHandle;
            minorHandleOver = style.minorHandleOver;
            minorHandlePressed = style.minorHandlePressed;
        }
    }

    public ResizeWidgetStyle getStyle() {
        return style;
    }

    public Button getTopLeftHandle() {
        return topLeftHandle;
    }

    public Button getTopHandle() {
        return topHandle;
    }

    public Button getTopRightHandle() {
        return topRightHandle;
    }

    public Button getRightHandle() {
        return rightHandle;
    }

    public Button getBottomRightHandle() {
        return bottomRightHandle;
    }

    public Button getBottomHandle() {
        return bottomHandle;
    }

    public Button getBottomLeftHandle() {
        return bottomLeftHandle;
    }

    public Button getLeftHandle() {
        return leftHandle;
    }


    public Stack getStack() {
        return stack;
    }

    public Image getForeground() {
        return foreground;
    }

    /**
     * When the user scales the child using the provided resize handles, this setting controls whether the scaling will
     * be from the edge or the center (maintaining the center position in the stage).
     * @return
     */
    public boolean isResizingFromCenter() {
        return resizingFromCenter;
    }

    /**
     * When the user scales the child using the provided resize handles, this setting controls whether the scaling will
     * be from the edge or the center (maintaining the center position in the stage).
     * @param resizingFromCenter
     */
    public void setResizingFromCenter(boolean resizingFromCenter) {
        this.resizingFromCenter = resizingFromCenter;
    }

    /**
     * This setting controls if the user is allowed to drag the position of the child by initiating a click and drag
     * from inside the child's bounds.
     * @return
     */
    public boolean isAllowDragging() {
        return allowDragging;
    }

    /**
     * This setting controls if the user is allowed to drag the position of the child by initiating a click and drag
     * from inside the child's bounds.
     * @param allowDragging
     */
    public void setAllowDragging(boolean allowDragging) {
        this.allowDragging = allowDragging;
    }

    public boolean isCenterOnLayout() {
        return centerOnLayout;
    }

    public void setCenterOnLayout(boolean centerOnLayout) {
        this.centerOnLayout = centerOnLayout;
    }
}
