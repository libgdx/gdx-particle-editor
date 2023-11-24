package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

/**
 * Add ScrollFocusListener to any scrollable widget that you want to set as the Stage's scrollFocus once the user mouses
 * over the widget.
 */
public class ScrollFocusListener extends InputListener {
    private Stage stage;

    public ScrollFocusListener(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        var scrollFocus = stage.getScrollFocus();
        var actor = event.getListenerActor();
        if (actor instanceof ScrollPane) {
            var scrollPane = (ScrollPane) actor;
            if (!scrollPane.isScrollY() && !scrollPane.isScrollX()) return;
        }
        if (scrollFocus == null || scrollFocus != actor && !scrollFocus.isDescendantOf(actor)) {
            stage.setScrollFocus(event.getListenerActor());
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        var actor = event.getListenerActor();
        if (stage.getScrollFocus() == actor &&  (toActor == null || !toActor.isDescendantOf(actor))) {
            stage.setScrollFocus(null);
        }
    }
}
