package com.ray3k.particleparkpro.widgets.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.particleparkpro.widgets.poptables.PopPreviewSettings;
import com.ray3k.stripe.ResizeWidget;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.PreviewSettings.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.*;

/**
 * The UI controls to accompany the particle effect preview. The viewport of the ParticlePreview is adjusted to match
 * the dimensions of this widget. Controls effect the zoom, position of the camera, placement of the particle effect,
 * and playing the effect in the preview. There are various options controlling grid, shaders, preview image, live
 * statistics, background color, PPM, and delta multiplier.
 */
public class PreviewPanel extends Panel {
    public static final FloatArray zoomLevels = new FloatArray(new float[] {1/6f, 1/4f, 1/3f, .5f, 2/3f, 1f, 1.5f, 2f, 3f, 4f, 6f});
    public static int zoomLevelIndex = 5;
    private static final Vector2 temp = new Vector2();
    public static Image previewBackgroundImage;
    public static ResizeWidget resizeWidget;
    public static Label statsLabel;

    public PreviewPanel() {
        zoomLevelIndex = 5;
        setTouchable(Touchable.enabled);

        var label = new Label("Preview", skin, "header");
        tabTable.add(label);

        var stack = new Stack();
        bodyTable.add(stack).grow();

        //Background
        previewBackgroundImage = new Image(skin, "white");
        stack.add(previewBackgroundImage);
        previewBackgroundImage.setColor(getBackgroundColor());

        //Preview viewport
        stack.add(viewportWidget);

        //UI
        var table = new Table();
        table.setTouchable(Touchable.enabled);
        stack.add(table);

        table.top().pad(5);
        var settingsButton = new Button(skin, "settings");
        table.add(settingsButton).expandX().left();
        addHandListener(settingsButton);
        var popTooltip = addTooltip(settingsButton, "Change the preview settings", Align.top, Align.topRight, tooltipBottomLeftArrowStyle);
        popTooltip.setAttachOffsetX(-8);
        onChange(settingsButton, () -> {
            var pop = new PopPreviewSettings();
            pop.attachToActor(settingsButton, Align.topLeft, Align.topRight);
            pop.show(foregroundStage);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        });

        var button = new Button(skin, "zoom-full");
        table.add(button);
        addHandListener(button);
        addTooltip(button, "Reset the zoom and center the camera at (0,0)", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(button, () -> {
            zoomLevelIndex = 5;
            previewViewport.setUnitsPerPixel(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());
            previewCamera.position.set(0, 0, 0);
        });

        button = new Button(skin, "zoom-out");
        table.add(button);
        addHandListener(button);
        addTooltip(button, "Zoom in.", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(button, () -> {
            zoomLevelIndex = MathUtils.clamp(zoomLevelIndex + 1, 0, zoomLevels.size - 1);
            previewViewport.setUnitsPerPixel(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());
        });

        button = new Button(skin, "zoom-in");
        table.add(button);
        addHandListener(button);
        addTooltip(button, "Zoom out.", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(button, () -> {
            zoomLevelIndex = MathUtils.clamp(zoomLevelIndex - 1, 0, zoomLevels.size - 1);
            previewViewport.setUnitsPerPixel(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());
        });

        var dragListener = new DragListener() {
            final Array<ParticleEmitter> resetEmitters = new Array<>();
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Buttons.RIGHT) {
                    temp.set(x, y);
                    stack.localToScreenCoordinates(temp);
                    previewViewport.unproject(temp);
                    particleEffect.setPosition(temp.x, temp.y);
                    for (var emitter : particleEffect.getEmitters()) {
                        if (!emitter.isContinuous()) {
                            emitter.setContinuous(true);
                            resetEmitters.add(emitter);
                        }
                        emitter.reset();
                    }
                    Gdx.graphics.setSystemCursor(SystemCursor.None);
                }

                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                for (var emitter : resetEmitters) {
                    emitter.setContinuous(false);
                }
                resetEmitters.clear();

                if (button == Buttons.RIGHT) Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                var oldZoom = 1 / zoomLevels.get(zoomLevelIndex);
                if (amountY > 0) {
                    zoomLevelIndex = MathUtils.clamp(zoomLevelIndex + 1, 0, zoomLevels.size - 1);
                } else if (amountY < 0) {
                    zoomLevelIndex = MathUtils.clamp(zoomLevelIndex - 1, 0, zoomLevels.size - 1);
                }
                var newZoom = 1 / zoomLevels.get(zoomLevelIndex);

                var pixelsDifferenceW = (table.getWidth() / oldZoom) - (table.getWidth() / newZoom);
                var sideRatioX = (x - (table.getWidth() / 2)) / table.getWidth();
                previewCamera.position.x += pixelsDifferenceW * sideRatioX  / getPixelsPerMeter();

                var pixelsDifferenceH = (table.getHeight() / oldZoom) - (table.getHeight() / newZoom);
                var sideRatioH = (y - (table.getHeight() / 2)) / table.getHeight();
                previewCamera.position.y += pixelsDifferenceH * sideRatioH / getPixelsPerMeter();

                previewViewport.setUnitsPerPixel(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());
                return true;
            }

            float startX, startY;
            float cameraStartX, cameraStartY;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                startX = x;
                startY = y;
                cameraStartX = previewCamera.position.x;
                cameraStartY = previewCamera.position.y;
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (Gdx.input.isButtonPressed(Buttons.LEFT) || Gdx.input.isButtonPressed(Buttons.MIDDLE)) {
                    temp.set(startX - x, startY - y);
                    temp.scl(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());
                    previewCamera.position.set(cameraStartX + temp.x, cameraStartY + temp.y, 0);
                }

                if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
                    temp.set(x, y);
                    stack.localToScreenCoordinates(temp);
                    previewViewport.unproject(temp);
                    particleEffect.setPosition(temp.x, temp.y);
                }
            }
        };
        dragListener.setButton(-1);
        dragListener.setTapSquareSize(5);
        table.addListener(dragListener);
        addScrollFocusListener(table);

        //Resize widget
        var container = new Container<>();
        container.setTouchable(Touchable.enabled);
        resizeWidget = new ResizeWidget(container, resizeWidgetStyle) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (isVisible() && previewImageTexture != null) {
                    temp.set(0, 0);
                    getActor().localToScreenCoordinates(temp);
                    previewViewport.unproject(temp);
                    previewImageX = temp.x;
                    previewImageY = temp.y;

                    temp.set(getActor().getWidth(), getActor().getHeight());
                    getActor().localToScreenCoordinates(temp);
                    previewViewport.unproject(temp);
                    previewImageWidth = temp.x - previewImageX;
                    previewImageHeight = temp.y - previewImageY;
                }
            }
        };
        resizeWidget.setAllowDragging(true);
        resizeWidget.setTouchable(Touchable.childrenOnly);
        resizeWidget.setVisible(false);
        addHorizontalResizeListener(resizeWidget.getRightHandle());
        addNWSEresizeListener(resizeWidget.getBottomRightHandle());
        addVerticalResizeListener(resizeWidget.getBottomHandle());
        addNESWresizeListener(resizeWidget.getBottomLeftHandle());
        addHorizontalResizeListener(resizeWidget.getLeftHandle());
        addNWSEresizeListener(resizeWidget.getTopLeftHandle());
        addVerticalResizeListener(resizeWidget.getTopHandle());
        addNESWresizeListener(resizeWidget.getTopRightHandle());
        addAllResizeListener(resizeWidget.getActor());
        stack.add(resizeWidget);

        //stats
        statsLabel = new Label("", skin) {
            float countdown = -1f;
            final float MAX_TIME = 5f;

            @Override
            public void act(float delta) {
                super.act(delta);
                var count = Utils.calcParticleCount();
                maxParticleCount = Math.max(maxParticleCount, count);
                if (count >= maxParticleCount) countdown = MAX_TIME;
                else {
                    countdown -= delta;
                    if (countdown < 0) {
                        countdown = MAX_TIME;
                        maxParticleCount = count;
                    }
                }

                if (isVisible()) {
                    var fps = Gdx.graphics.getFramesPerSecond();
                    var percentage = particleEffect.getEmitters().size > 0 ? (int) (particleEffect.getEmitters().first().getPercentComplete() * 100) : 100;
                    setText("FPS: " + fps + "\nCount: " + count + "\nMax: " + maxParticleCount + "\n" + percentage + "%");
                }
            }
        };
        statsLabel.setLayoutEnabled(false);
        container = new Container<>(statsLabel);
        container.bottom().left();
        container.pad(10);
        stack.add(container);
        statsLabel.addAction(Actions.delay(1f, Actions.run(() -> statsLabel.setLayoutEnabled(true))));
        statsLabel.setVisible(isStatisticsEnabled());
        statsLabel.setColor(getStatisticsColor());
    }
}
