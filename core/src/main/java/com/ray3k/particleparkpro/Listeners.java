package com.ray3k.particleparkpro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.ImagesAddUndoable;
import com.ray3k.particleparkpro.widgets.InfSlider;
import com.ray3k.particleparkpro.widgets.NoCaptureKeyboardFocusListener;
import com.ray3k.particleparkpro.widgets.poptables.PopConfirmClose;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTable.PopTableStyle;
import com.ray3k.stripe.PopTable.TableShowHideListener;
import com.ray3k.stripe.ScrollFocusListener;
import com.ray3k.stripe.Spinner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Locale;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.PreviewSettings.*;
import static com.ray3k.particleparkpro.Utils.showToast;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.effectEmittersPanel;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;
import static com.ray3k.particleparkpro.widgets.styles.Styles.infSliderStyle;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomRightArrowStyle;
import static com.ray3k.particleparkpro.widgets.subpanels.ImagesSubPanel.imagesSubPanel;

/**
 * A convenience class to organize and initialize the custom listeners for Particle Park Pro.
 */
public class Listeners {
    public static SystemCursorListener handListener;
    public static SystemCursorListener ibeamListener;
    public static SystemCursorListener horizontalResizeListener;
    public static SystemCursorListener verticalResizeListener;
    public static SystemCursorListener neswResizeListener;
    public static SystemCursorListener nwseResizeListener;
    public static SystemCursorListener allResizeListener;
    public static SplitPaneSystemCursorListener splitPaneHorizontalSystemCursorListener;
    public static SplitPaneSystemCursorListener splitPaneVerticalSystemCursorListener;
    public static NoCaptureKeyboardFocusListener noCaptureKeyboardFocusListener;
    public static InputListener unfocusOnEnterKeyListener;
    static ScrollFocusListener scrollFocusListener;
    static ScrollFocusListener foregroundScrollFocusListener;

    public static void initializeListeners() {
        handListener = new SystemCursorListener(SystemCursor.Hand);
        ibeamListener = new SystemCursorListener(SystemCursor.Ibeam);
        neswResizeListener = new SystemCursorListener(SystemCursor.NESWResize);
        nwseResizeListener = new SystemCursorListener(SystemCursor.NWSEResize);
        horizontalResizeListener = new SystemCursorListener(SystemCursor.HorizontalResize);
        verticalResizeListener = new SystemCursorListener(SystemCursor.VerticalResize);
        allResizeListener = new SystemCursorListener(SystemCursor.AllResize);
        splitPaneHorizontalSystemCursorListener = new SplitPaneSystemCursorListener(SystemCursor.HorizontalResize);
        splitPaneVerticalSystemCursorListener = new SplitPaneSystemCursorListener(SystemCursor.VerticalResize);
        scrollFocusListener = new ScrollFocusListener(stage);
        foregroundScrollFocusListener = new ScrollFocusListener(foregroundStage);
        noCaptureKeyboardFocusListener = new NoCaptureKeyboardFocusListener();

        unfocusOnEnterKeyListener = new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) event.getStage().setKeyboardFocus(null);
                return super.keyDown(event, keycode);
            }
        };
    }

    public static ChangeListener onChange(Actor actor, Runnable runnable) {
        var changeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        };
        actor.addListener(changeListener);
        return changeListener;
    }

    public static void onClick(Actor actor, Runnable runnable) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        });
    }

    public static void onTouchDown(Actor actor, Runnable runnable) {
        actor.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                runnable.run();
                return false;
            }
        });
    }

    public static void addHandListener(Actor actor) {
        actor.addListener(handListener);
    }

    public static void addIbeamListener(Actor actor) {
        actor.addListener(ibeamListener);
    }

    public static void addScrollFocusListener(Actor actor) {
        actor.addListener(scrollFocusListener);
    }

    public static void addForegroundScrollFocusListener(Actor actor) {
        actor.addListener(foregroundScrollFocusListener);
    }

    public static void addHorizontalResizeListener(Actor actor) {
        actor.addListener(horizontalResizeListener);
    }

    public static void addVerticalResizeListener(Actor actor) {
        actor.addListener(verticalResizeListener);
    }

    public static void addNESWresizeListener(Actor actor) {
        actor.addListener(neswResizeListener);
    }

    public static void addNWSEresizeListener(Actor actor) {
        actor.addListener(nwseResizeListener);
    }

    public static void addAllResizeListener(Actor actor) {
        actor.addListener(allResizeListener);
    }

    public static void addSplitPaneHorizontalSystemCursorListener(Actor actor) {
        actor.addListener(splitPaneHorizontalSystemCursorListener);
    }

    public static void addSplitPaneVerticalSystemCursorListener(Actor actor) {
        actor.addListener(splitPaneVerticalSystemCursorListener);
    }

    public static void addUnfocusOnEnterKeyListener(Actor actor) {
        actor.addListener(unfocusOnEnterKeyListener);
    }

    public static PopTable addTooltip(Actor actor, String text, int edge, int align, float width, PopTableStyle popTableStyle) {
        return addTooltip(actor, text, edge, align, width, true, popTableStyle);
    }

    public static PopTable addTooltip(Actor actor, String text, int edge, int align, PopTableStyle popTableStyle) {
        return addTooltip(actor, text, edge, align, 0, false, popTableStyle);
    }

    public static PopTable addTooltip(Actor actor, String text, int edge, int align, PopTableStyle popTableStyle, boolean foreground) {
        return addTooltip(actor, text, edge, align, 0, false, popTableStyle, foreground);
    }

    private static PopTable addTooltip(Actor actor, String text, int edge, int align, float width, boolean defineWidth, PopTableStyle popTableStyle) {
        return addTooltip(actor, text, edge, align, width, defineWidth, popTableStyle, true);
    }

    private static PopTable addTooltip(Actor actor, String text, int edge, int align, float width, boolean defineWidth, PopTableStyle popTableStyle, boolean foreground) {
        PopTable popTable = new PopTable(popTableStyle);
        var inputListener = new ClickListener() {
            boolean dismissed;
            Action showTableAction;

            {
                popTable.setModal(false);
                popTable.setTouchable(Touchable.disabled);

                var label = new Label(text, skin);
                if (defineWidth) {
                    label.setWrap(true);
                    popTable.add(label).width(width);
                } else {
                    popTable.add(label);
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!Utils.isWindowFocused()) return;
                if (Gdx.input.isButtonPressed(Buttons.LEFT) || Gdx.input.isButtonPressed(Buttons.RIGHT) || Gdx.input.isButtonPressed(Buttons.MIDDLE)) return;
                if (pointer == -1 && popTable.isHidden() && !dismissed) {
                    if (fromActor == null || !event.getListenerActor().isAscendantOf(fromActor)) {
                        if (showTableAction == null) {
                            showTableAction = Actions.delay(.5f,
                                Actions.run(() -> {
                                    showTable(actor);
                                    showTableAction = null;
                                }));
                            actor.addAction(showTableAction);
                        }
                    }
                }
            }

            private void showTable(Actor actor) {
                if (actor instanceof Disableable) {
                    if (((Disableable) actor).isDisabled()) return;
                }

                popTable.show(foreground ? foregroundStage : stage);
                popTable.attachToActor(actor, edge, align);
                if (popTableStyle == tooltipBottomRightArrowStyle) popTable.setAttachOffsetX(7);

                popTable.moveToInsideStage();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    if (toActor == null || !toActor.isDescendantOf(event.getListenerActor())) {
                        if (!popTable.isHidden()) popTable.hide();
                        dismissed = false;
                        if (showTableAction != null) {
                            actor.removeAction(showTableAction);
                            showTableAction = null;
                        }
                    }

                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dismissed = true;
                popTable.hide();
                if (showTableAction != null) {
                    actor.removeAction(showTableAction);
                    showTableAction = null;
                }
                return false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        };
        actor.addListener(inputListener);
        return popTable;
    }

    public static void addInfiniteSlider(Spinner valueSpinner, float increment, float range, boolean adjustByPPM, ChangeListener changeListener) {
        var sliderPop = new PopTable();
        sliderPop.attachToActor(valueSpinner, Align.bottom, Align.bottom);

        var slider = new InfSlider(infSliderStyle);
        slider.setRange(adjustByPPM ? range / getPixelsPerMeter() : range);
        slider.setIncrement(adjustByPPM ? increment / getPixelsPerMeter() : increment);
        slider.addListener(noCaptureKeyboardFocusListener);
        slider.getKnob().addListener(noCaptureKeyboardFocusListener);
        slider.getBackground().addListener(noCaptureKeyboardFocusListener);

        slider.setValue(valueSpinner.getValueAsInt());
        sliderPop.add(slider).width(100);
        addHandListener(slider.getKnob());
        onChange(slider, () -> valueSpinner.setValue(slider.getValue()));
        slider.addListener(changeListener);

        valueSpinner.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (event.isFocused()) {
                    sliderPop.show(stage);
                    slider.setValue(valueSpinner.getValueAsInt());
                }
                else sliderPop.hide();
            }
        });
        onChange(valueSpinner, () -> slider.setValue(valueSpinner.getValue()));
    }

    public static class WindowListener extends Lwjgl3WindowAdapter {
        private PopConfirmClose pop;
        @Override
        public boolean closeRequested() {
            if (!allowClose && pop == null) {
                pop = new PopConfirmClose();
                pop.addListener(new TableShowHideListener() {
                    @Override
                    public void tableShown(Event event) {

                    }

                    @Override
                    public void tableHidden(Event event) {
                        pop = null;
                    }
                });
                pop.show(foregroundStage);
            }

            return allowClose;
        }

        @Override
        public void filesDropped(String[] files) {
            var imageFileHandles = new Array<FileHandle>();
            var textFileHandles = new Array<FileHandle>();

            for (var file : files) {
                var fileHandle = Gdx.files.absolute(file);
                if (fileHandle.extension().toLowerCase(Locale.ROOT).matches("^png$|^jpg$")) imageFileHandles.add(fileHandle);
                else textFileHandles.add(fileHandle);
            }

            if (imageFileHandles.size > 0) {
                if (effectEmittersPanel == null || emitterPropertiesPanel == null) return;

                UndoManager.add(new ImagesAddUndoable(selectedEmitter, imageFileHandles, "Add Images"));
                imagesSubPanel.updateList();
                imagesSubPanel.updateDisabled();

                showToast(imageFileHandles.size == 1 ? "Added image " + imageFileHandles.first().name() : "Added " + imageFileHandles.size + " images");
            } else if (textFileHandles.size > 0) {
                Utils.openDroppedParticleFile(textFileHandles.first());
            }
        }
    }
}
