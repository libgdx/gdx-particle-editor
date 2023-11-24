package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/**
 * A hack to signify that the widget this listener is attached to should not trigger the keyboard focus changed event
 * in Particle Park Pro.
 */
public class NoCaptureKeyboardFocusListener implements EventListener {
    @Override
    public boolean handle(Event event) {
        return false;
    }
}
