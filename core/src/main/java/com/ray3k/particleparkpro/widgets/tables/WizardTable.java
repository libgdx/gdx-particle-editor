package com.ray3k.particleparkpro.widgets.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.Utils;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.widgets.Carousel;
import com.ray3k.particleparkpro.widgets.panels.*;
import com.ray3k.particleparkpro.widgets.poptables.PopEditorSettings;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.PresetActions.transition;
import static com.ray3k.particleparkpro.undo.UndoManager.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomArrowStyle;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipBottomRightArrowStyle;

/**
 * The widget layout that attempts to simplify the process of creating a particle effect.
 */
public class WizardTable extends Table {

    public static WizardTable wizardTable;
    private Table undoTable;
    private static int carouselIndex;
    private static float splitPanePercent = .5f;
    private SplitPane verticalSplitPane;

    public WizardTable() {
        wizardTable = this;
        pad(20).padBottom(5);

        var startPanel = new StartPanel();
        var previewPanel = new PreviewPanel();
        var effectEmittersPanel = new EffectEmittersPanel();
        var emitterPropertiesPanel = new EmitterPropertiesPanel();
        var summaryPanel = new SummaryPanel();

        var carousel = new Carousel(startPanel, effectEmittersPanel, emitterPropertiesPanel, summaryPanel);
        carousel.showIndex(carouselIndex, false);
        carousel.setTouchable(Touchable.enabled);
        addHandListener(carousel.previousButton);
        addHandListener(carousel.nextButton);
        for (var button : carousel.buttonGroup.getButtons()) addHandListener(button);
        carousel.buttonTable.padTop(10).padBottom(20);
        onChange(carousel, () -> {
            carouselIndex = carousel.getShownIndex();
            if (carousel.getShownActor() == summaryPanel) summaryPanel.populateScrollTable();
        });

        verticalSplitPane = new SplitPane(previewPanel, carousel, true, skin);
        add(verticalSplitPane).grow();
        verticalSplitPane.setSplitAmount(splitPanePercent);
        addSplitPaneVerticalSystemCursorListener(verticalSplitPane);

        row();
        var table = new Table();
        add(table).growX().padTop(5);

        var label = new Label(version, skin);
        table.add(label);

        var textButton = new TextButton("-Update Available-", skin, "no-bg");
        textButton.setVisible(false);
        table.add(textButton).spaceLeft(10);
        addHandListener(textButton);
        addTooltip(textButton, "Open browser to download page", Align.top, Align.top, tooltipBottomArrowStyle);
        onChange(textButton, () -> Gdx.net.openURI("https://github.com/raeleus/Particle-Park-Pro/releases"));
        Utils.checkVersion((String newVersion) -> {
            if (!versionRaw.equals(newVersion)) textButton.setVisible(true);
        });

        var button = new Button(skin, "home");
        table.add(button).expandX().right();
        addHandListener(button);
        addTooltip(button, "Return to the Home Screen", Align.top, Align.topLeft, tooltipBottomRightArrowStyle, false);
        onChange(button, () -> transition(this, new WelcomeTable(), Align.bottom));

        button = new Button(skin, "settings");
        table.add(button);
        addHandListener(button);
        addTooltip(button, "Open the Editor Settings dialog", Align.top, Align.topLeft, tooltipBottomRightArrowStyle, false);
        onChange(button, () -> {
            Gdx.input.setInputProcessor(foregroundStage);
            Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
            var pop = new PopEditorSettings();
            pop.show(foregroundStage);
        });

        undoTable = new Table();
        table.add(undoTable);

        refreshUndo();
    }

    public void refreshUndo() {
        undoTable.clearChildren();

        var button = new Button(skin, hasUndo() ? "undo-active" : "undo");
        button.setDisabled(!hasUndo());
        undoTable.add(button);
        addHandListener(button);
        if (hasUndo()) {
            var pop = addTooltip(button, "Undo " + getUndoDescription(), Align.top, Align.topLeft, tooltipBottomRightArrowStyle, false);
            pop.setAttachOffsetX(8);
        }
        onChange(button, UndoManager::undo);

        button = new Button(skin, hasRedo() ? "redo-active" : "redo");
        button.setDisabled(!hasRedo());
        undoTable.add(button);
        addHandListener(button);
        if (hasRedo()) {
            var pop = addTooltip(button, "Redo " + getRedoDescription(), Align.top, Align.topLeft, tooltipBottomRightArrowStyle, false);
            pop.setAttachOffsetX(8);
        }
        onChange(button, UndoManager::redo);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) splitPanePercent = verticalSplitPane.getSplitAmount();
    }
}
