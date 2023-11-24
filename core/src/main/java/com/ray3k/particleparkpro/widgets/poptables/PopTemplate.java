package com.ray3k.particleparkpro.widgets.poptables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.runnables.SaveAsRunnable;
import com.ray3k.particleparkpro.runnables.SaveRunnable;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel;
import com.ray3k.stripe.PopTable;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.emitterPropertiesPanel;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomArrowStyle;

/**
 * PopTable used to open a template file.
 */
public class PopTemplate extends PopTable {
    public PopTemplate() {
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

        final int itemSpacing = 5;

        var scrollTable = new Table();
        var scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        add(scrollPane).grow();
        addForegroundScrollFocusListener(scrollPane);

        //Blank
        scrollTable.pad(5);
        scrollTable.defaults().space(itemSpacing).left();
        var textButton = new TextButton("Blank", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("blank.p"));
        var popTable = addTooltip(textButton, "An empty template perfect for starting a new project", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Explosion
        textButton = new TextButton("Explosion", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("explosion.p"));
        popTable = addTooltip(textButton, "Standard fire ball explosion", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Flame
        scrollTable.row();
        textButton = new TextButton("Flame", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("flame.p"));
        popTable = addTooltip(textButton, "The default template implementing the traditional ever-burning flame", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Firework
        textButton = new TextButton("Firework", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("firework.p"));
        popTable = addTooltip(textButton, "Fireworks that demonstrate emitters with multiple delays", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Sparks
        scrollTable.row();
        textButton = new TextButton("Sparks", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("sparks.p"));
        popTable = addTooltip(textButton, "A shower of sparks coming from an arc welder", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Infinity
        textButton = new TextButton("Infinity", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("infinity.p"));
        popTable = addTooltip(textButton, "An infinite four way pattern", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Smoke
        scrollTable.row();
        textButton = new TextButton("Smoke", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("smoke.p"));
        popTable = addTooltip(textButton, "A tower of smoke emanating from a chimney or wreckage", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Glitch
        textButton = new TextButton("Glitch", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("glitch.p"));
        popTable = addTooltip(textButton, "Use additive to create a glitchy logo effect", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Demolition
        scrollTable.row();
        textButton = new TextButton("Demolition", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("demolition.p"));
        popTable = addTooltip(textButton, "The explosion from a demolished building or self-destructing robot", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Rain
        textButton = new TextButton("Rain", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("rain.p"));
        popTable = addTooltip(textButton, "Cinematic rain to overlay an entire scene", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Trail
        scrollTable.row();
        textButton = new TextButton("Trail", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("trail.p"));
        popTable = addTooltip(textButton, "A simple trail to follow a missile or any other flying object", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Laser
        textButton = new TextButton("Laser", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("laser.p"));
        popTable = addTooltip(textButton, "A laser line that can be rotated in code", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Thruster
        scrollTable.row();
        textButton = new TextButton("Thruster", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("thruster.p"));
        popTable = addTooltip(textButton, "A thruster plume that sticks closely to an attached object like a rocket", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);

        //Splash
        textButton = new TextButton("Splash", skin);
        scrollTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> openTemplate("splash.p"));
        popTable = addTooltip(textButton, "A water splash as viewed from the side", Align.top, Align.top, tooltipBottomArrowStyle);
        popTable.setKeepSizedWithinStage(false);
    }

    private void openTemplate(String internalPath) {
        var saveFirstRunnable = new SaveRunnable();
        var saveAsFirstRunnable = new SaveAsRunnable();
        saveFirstRunnable.setSaveAsRunnable(saveAsFirstRunnable);
        saveAsFirstRunnable.setSaveRunnable(saveFirstRunnable);

        var runnable = new Runnable() {
            @Override
            public void run() {
                hide();
                var templateFileHandle = Gdx.files.internal(internalPath);
                Utils.loadParticle(templateFileHandle);
                selectedEmitter = particleEffect.getEmitters().first();

                EffectEmittersPanel.effectEmittersPanel.populateEmitters();
                EffectEmittersPanel.effectEmittersPanel.updateDisableableWidgets();
                emitterPropertiesPanel.populateScrollTable(null);

                UndoManager.clear();

                openFileFileHandle = null;
                unsavedChangesMade = false;
                updateWindowTitle();

                Utils.showToast("Loaded template " + templateFileHandle.name());
            }
        };
        saveFirstRunnable.setOnCompletionRunnable(runnable);

        if (unsavedChangesMade) {
            var pop = new PopConfirmLoad(saveFirstRunnable, runnable);
            pop.show(foregroundStage);
        } else {
            runnable.run();
        }
    }
}
