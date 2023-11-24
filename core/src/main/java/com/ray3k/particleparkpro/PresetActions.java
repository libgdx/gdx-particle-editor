package com.ray3k.particleparkpro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.ray3k.particleparkpro.Core.*;

/**
 * A convenience class to organize the Actions used for transitions throughout Particle Park Pro.
 */
public class PresetActions {
    private final static Color CLEAR_WHITE = new Color(1, 1, 1, 0);

    public static Action moveOutAction(Actor actor, int alignDirection) {
        float targetX = actor.getX();
        float targetY = actor.getY();

        if (Align.isLeft(alignDirection)) targetX -= stage.getWidth();
        else if (Align.isRight(alignDirection)) targetX += stage.getWidth();

        if (Align.isBottom(alignDirection)) targetY -= stage.getHeight();
        else if (Align.isTop(alignDirection)) targetY += stage.getHeight();

        var movementAction = moveTo(targetX, targetY, 1, Interpolation.fade);

        var resizeCheckAction = new Action() {
            @Override
            public boolean act(float delta) {
                if (windowResized) {
                    movementAction.setActor(null);
                    return true;
                }
                return movementAction.isComplete();
            }
        };

        return sequence(parallel(movementAction, resizeCheckAction), removeActor());
    }

    public static Action moveInAction(Actor actor, int alignDirection) {
        float startX = actor.getX();
        float startY = actor.getY();

        if (Align.isLeft(alignDirection)) startX += stage.getWidth();
        else if (Align.isRight(alignDirection)) startX -= stage.getWidth();

        if (Align.isBottom(alignDirection)) startY += stage.getHeight();
        else if (Align.isTop(alignDirection)) startY -= stage.getHeight();

        var initializeAction = moveTo(startX, startY);

        var movementAction = moveTo(actor.getX(), actor.getY(), 1, Interpolation.fade);

        var resizeCheckAction = new Action() {
            @Override
            public boolean act(float delta) {
                if (windowResized) {
                    movementAction.setActor(null);
                    return true;
                }
                return movementAction.isComplete();
            }
        };

        return sequence(initializeAction, parallel(movementAction, resizeCheckAction));
    }

    public static Action welcomeAction(Table table) {
        ParticlePreview.pause = true;
        Gdx.app.postRunnable(() -> {
            table.setTransform(true);
            table.setOrigin(Align.center);
        });
        return sequence(
            color(CLEAR_WHITE),
            scaleTo(1.1f, 1.1f),
            delay(.5f),
            parallel(fadeIn(1f), scaleTo(1f, 1f, .75f, Interpolation.smooth)),
            run(() -> {
                table.setTransform(false);
                ParticlePreview.pause = false;
            }));
    }

    public static void transition(Actor first, Actor second, int alignDirection) {
        particleEffect.setPosition(0, 0);

        stage.addActor(first);
        first.addAction(moveOutAction(first, alignDirection));

        root.setActor(second);
        root.validate();

        second.addAction(moveInAction(second, alignDirection));
    }

    public static Action showEmitterPropertyInTable(Actor actor) {
        var duration = .5f;
        var moveAction = sequence(moveTo(actor.getX() + actor.getWidth(), actor.getY()), moveTo(actor.getX(), actor.getY(), duration, Interpolation.smooth));
        var fadeAction = sequence(color(CLEAR_WHITE), fadeIn(duration));
        return parallel(moveAction, fadeAction);
    }

    public static Action showEmitterPropertyInHorizontalGroup(Actor actor) {
        if (actor instanceof Group) ((Group) actor).setTransform(true);

        var duration = .5f;
        actor.setOrigin(Align.center);
        actor.setScale(1.5f);
        actor.setColor(1, 1, 1, 0);
        return parallel(scaleTo(1, 1, duration, Interpolation.smooth), fadeIn(duration, Interpolation.smooth));
    }

    public static Action hideEmitterPropertyInTable(Actor actor) {
        return moveTo(actor.getX() + actor.getWidth(), actor.getY(), .5f, Interpolation.smooth);
    }

    public static Action hideEmitterPropertyInHorizontalGroup(Actor actor) {
        if (actor instanceof Group) ((Group) actor).setTransform(true);

        var duration = .25f;
        var scaleTarget = 1.5f;
        actor.setOrigin(Align.center);
        return parallel(scaleTo(scaleTarget, scaleTarget, duration), fadeOut(duration));
    }
}
