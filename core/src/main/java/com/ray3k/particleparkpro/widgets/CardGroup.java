package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Array;

/**
 * Widget that allows the user to flip through its children, displaying only one at a time.
 */
public class CardGroup extends Container<Actor> {
    public final Array<Actor> actors = new Array<>();
    private int shownIndex = -1;

    public CardGroup(Actor... actors) {
        if (actors.length > 0) {
            this.actors.addAll(actors);
            showIndex(0);
        }
        fill();
    }

    public void showIndex(int index) {
        shownIndex = index;
        super.setActor(actors.get(index));
    }

    public Actor getActor(int index) {
        return actors.get(index);
    }

    public int getShownIndex() {
        return shownIndex;
    }

    public Actor getShownActor() {
        return actors.get(shownIndex);
    }

    public void add(Actor actor) {
        actors.add(actor);
        if (shownIndex == -1) showIndex(0);
    }

    public void remove(Actor actor) {

    }

    @Override @Deprecated
    public void setActor(Actor actor) {
        super.setActor(actor);
    }
}
