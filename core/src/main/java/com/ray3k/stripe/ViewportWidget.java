package com.ray3k.stripe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A widget that allows you to embed an entire viewport within your UI. This is an excellent tool to integrate your
 * games into a UI instead of just overlaying the UI on top of it. Make sure to render your game assets after calling
 * stage.act() in the render method to prevent position lag.
 */
public class ViewportWidget extends Widget {
    public Viewport viewport;
    private final static Vector2 temp = new Vector2();

    public ViewportWidget(Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Updates the viewport's bounds to match the size and position of the widget. Applies the viewport. Call before
     * drawing to the viewport.
     * @param centerCamera
     */
    public void updateViewport(boolean centerCamera) {
        temp.set(MathUtils.round(getWidth()), MathUtils.round(getHeight()));
        if (getStage() != null) getStage().getViewport().project(temp);

        viewport.update(MathUtils.round(temp.x), MathUtils.round(temp.y), centerCamera);

        int viewportOriginalX = viewport.getScreenX();
        int viewportOriginalY = viewport.getScreenY();
        temp.set(0, 0);
        localToScreenCoordinates(temp);
        viewport.setScreenPosition(viewportOriginalX + MathUtils.round(temp.x), viewportOriginalY + MathUtils.round(Gdx.graphics.getHeight() - temp.y));
        viewport.apply(centerCamera);
    }
}
