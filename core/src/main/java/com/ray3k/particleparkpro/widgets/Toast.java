package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.ray3k.stripe.PopTable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Toast extends PopTable {
    private float transitionTime;
    private float showTime;

    public Toast(float transitionTime, float showTime) {
        this.transitionTime = transitionTime;
        this.showTime = showTime;
    }

    public Toast(Skin skin, float transitionTime, float showTime) {
        super(skin);
        this.transitionTime = transitionTime;
        this.showTime = showTime;
    }

    public Toast(Skin skin, String style, float transitionTime, float showTime) {
        super(skin, style);
        this.transitionTime = transitionTime;
        this.showTime = showTime;
    }

    public Toast(WindowStyle style, float transitionTime, float showTime) {
        super(style);
        this.transitionTime = transitionTime;
        this.showTime = showTime;
    }

    public Toast(PopTableStyle style, float transitionTime, float showTime) {
        super(style);
        this.transitionTime = transitionTime;
        this.showTime = showTime;
    }

    @Override
    public void show(Stage stage) {
        super.show(stage, null);
        setKeepSizedWithinStage(false);
        getParentGroup().setColor(1, 1, 1, 1);
        addAction(sequence(
            moveTo(getStage().getWidth() / 2f - getWidth() / 2f, -getHeight()),
            moveTo(getStage().getWidth() / 2f - getWidth() / 2f, 0, transitionTime, Interpolation.exp5Out),
            delay(showTime),
            run(this::hide)
        ));
    }

    @Override
    public void hide() {
        addAction(sequence(
            moveTo(getStage().getWidth() / 2f - getWidth() / 2f, -getHeight(), transitionTime, Interpolation.exp5In),
            run(() -> super.hide(null))
        ));
    }
}
