package com.ray3k.particleparkpro.widgets.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.particleparkpro.widgets.poptables.PopAddProperty;
import com.ray3k.particleparkpro.widgets.subpanels.*;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;
import static com.ray3k.particleparkpro.PresetActions.hideEmitterPropertyInTable;
import static com.ray3k.particleparkpro.PresetActions.showEmitterPropertyInTable;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.ShownProperty.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.tooltipRightArrowStyle;

/**
 * A widget to display the properties of the selected emitter. A set of default properties are always available while
 * additional properties must be added manually. Each property is represented by a subpanel and have their own controls
 * used to display and modify their values.
 */
public class EmitterPropertiesPanel extends Panel {
    public enum ShownProperty {
        DELAY("Delay"), LIFE_OFFSET("Life Offset"), X_OFFSET("X Offset"), Y_OFFSET("Y Offset"), VELOCITY("Velocity"),
        ANGLE("Angle"), ROTATION("Rotation"), WIND("Wind"), GRAVITY("Gravity");

        public String name;
        ShownProperty(String name) {
            this.name = name;
        }
    }
    private Table scrollTable;
    public static EmitterPropertiesPanel emitterPropertiesPanel;
    private ScrollPane scrollPane;
    private static final Vector2 temp = new Vector2();

    public EmitterPropertiesPanel() {
        emitterPropertiesPanel = this;
        setTouchable(Touchable.enabled);

        var label = new Label("Emitter Properties", skin, "header");
        tabTable.add(label);

        bodyTable.defaults().space(5);
        scrollTable = new Table();
        scrollTable.top();
        scrollPane = new ScrollPane(scrollTable, skin, "emitter-properties");
        scrollPane.setFlickScroll(false);
        bodyTable.add(scrollPane).grow();
        addScrollFocusListener(scrollPane);

        populateScrollTable(null);

        bodyTable.row();
        var addPropertyTextButton = new TextButton("Add Property", skin, "add");
        bodyTable.add(addPropertyTextButton).right();
        addHandListener(addPropertyTextButton);
        onChange(addPropertyTextButton, () -> {
            Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
            var pop = new PopAddProperty();
            pop.attachToActor(addPropertyTextButton, Align.top, Align.top);
            pop.show(foregroundStage);
        });
        addTooltip(addPropertyTextButton, "Activate an optional emitter property", Align.left, Align.left, tooltipRightArrowStyle);
    }

    public void populateScrollTable(ShownProperty newProperty) {
        Actor scrollToActor = null;

        scrollTable.clearChildren(true);
        scrollTable.defaults().growX().space(10);

        //Images
        var imagesSubPanel = new ImagesSubPanel();
        scrollTable.add(imagesSubPanel);

        //Count
        scrollTable.row();
        var countSubPanel = new CountSubPanel();
        scrollTable.add(countSubPanel);

        //Delay
        if (selectedEmitter.getDelay().isActive()) {
            scrollTable.row();
            var delaySubPanel = new RangeSubPanel("Delay", selectedEmitter.getDelay(), "time from beginning of the effect to emission start in milliseconds", "change Delay", DELAY, 20, 500, 0, false, true);
            delaySubPanel.setUserObject(DELAY);
            scrollTable.add(delaySubPanel);
            if (newProperty == DELAY) scrollToActor = delaySubPanel;
        }

        //Duration
        scrollTable.row();
        var durationSubPanel = new RangeSubPanel("Duration", selectedEmitter.getDuration(), "time particles will be emitted in milliseconds", "change Duration", null, 20, 500, 0, false, true);
        scrollTable.add(durationSubPanel);

        //Emission
        scrollTable.row();
        var emissionSubPanel = new GraphSubPanel("Emission", selectedEmitter.getEmission(), true, false, "the number of particles emitted per second", "change Emission", "Duration", null, 1, 20, 0, false, true);
        scrollTable.add(emissionSubPanel);

        //Life
        scrollTable.row();
        var lifeSubPanel = new GraphSubPanel("Life", selectedEmitter.getLife(), true, true, "the time particles will live in milliseconds", "change Life", "Duration", null, 20, 500, 0, false, true);
        scrollTable.add(lifeSubPanel);

        //Life Offset
        if (selectedEmitter.getLifeOffset().isActive()) {
            scrollTable.row();
            var lifeOffsetSubPanel = new GraphSubPanel("Life Offset", selectedEmitter.getLifeOffset(), true, true, "the life duration consumed upon particle emission in milliseconds", "change Life Offset", "Duration", LIFE_OFFSET, 20, 500, 0, false, true);
            lifeOffsetSubPanel.setUserObject(LIFE_OFFSET);
            scrollTable.add(lifeOffsetSubPanel);
            if (newProperty == LIFE_OFFSET) scrollToActor = lifeOffsetSubPanel;
        }

        //X Offset
        if (selectedEmitter.getXOffsetValue().isActive()) {
            scrollTable.row();
            var xOffsetSubPanel = new RangeSubPanel("X Offset", selectedEmitter.getXOffsetValue(), "amount to offset a particle's starting X location in world units", "change X Offset", X_OFFSET, 5, 50, SPINNER_DECIMAL_PLACES, true, false);
            xOffsetSubPanel.setUserObject(X_OFFSET);
            scrollTable.add(xOffsetSubPanel);
            if (newProperty == X_OFFSET) scrollToActor = xOffsetSubPanel;
        }

        //Y Offset
        if (selectedEmitter.getYOffsetValue().isActive()) {
            scrollTable.row();
            var yOffsetSubPanel = new RangeSubPanel("Y Offset", selectedEmitter.getYOffsetValue(), "amount to offset a particle's starting Y location in world units", "change Y Offset", Y_OFFSET, 5, 50, SPINNER_DECIMAL_PLACES, true, false);
            yOffsetSubPanel.setUserObject(Y_OFFSET);
            scrollTable.add(yOffsetSubPanel);
            if (newProperty == Y_OFFSET) scrollToActor = yOffsetSubPanel;
        }

        //Spawn
        scrollTable.row();
        var spawnSubPanel = new SpawnSubPanel();
        scrollTable.add(spawnSubPanel);

        //Size
        scrollTable.row();
        var sizeSubPanel = new SizeSubPanel();
        scrollTable.add(sizeSubPanel);

        //Velocity
        if (selectedEmitter.getVelocity().isActive()) {
            scrollTable.row();
            var velocitySubPanel = new GraphSubPanel("Velocity", selectedEmitter.getVelocity(), true, false, "the particle speed in world units per second", "change Velocity", "Life", VELOCITY, 5, 50, SPINNER_DECIMAL_PLACES, true, false);
            velocitySubPanel.setUserObject(VELOCITY);
            scrollTable.add(velocitySubPanel);
            if (newProperty == VELOCITY) scrollToActor = velocitySubPanel;
        }

        //Angle
        if (selectedEmitter.getAngle().isActive()) {
            scrollTable.row();
            var angleSubPanel = new GraphSubPanel("Angle", selectedEmitter.getAngle(), true, false, "the particle emission angle in degrees", "change Angle", "Life", ANGLE, 5, 45, SPINNER_DECIMAL_PLACES, true, false);
            angleSubPanel.setUserObject(ANGLE);
            scrollTable.add(angleSubPanel);
            if (newProperty == ANGLE) scrollToActor = angleSubPanel;
        }

        //Rotation
        if (selectedEmitter.getRotation().isActive()) {
            scrollTable.row();
            var rotationSubPanel = new GraphSubPanel("Rotation", selectedEmitter.getRotation(), true, false, "the particle rotation in degrees", "change Rotation", "Life", ROTATION, 15, 90, SPINNER_DECIMAL_PLACES, false, false);
            rotationSubPanel.setUserObject(ROTATION);
            scrollTable.add(rotationSubPanel);
            if (newProperty == ROTATION) scrollToActor = rotationSubPanel;
        }

        //Wind
        if (selectedEmitter.getWind().isActive()) {
            scrollTable.row();
            var windSubPanel = new GraphSubPanel("Wind", selectedEmitter.getWind(), true, false, "the wind strength in world units per second", "change Wind", "Life", WIND, 5, 50, SPINNER_DECIMAL_PLACES, true, false);
            windSubPanel.setUserObject(WIND);
            scrollTable.add(windSubPanel);
            if (newProperty == WIND) scrollToActor = windSubPanel;
        }

        //Gravity
        if (selectedEmitter.getGravity().isActive()) {
            scrollTable.row();
            var gravitySubPanel = new GraphSubPanel("Gravity", selectedEmitter.getGravity(), true, false, "the gravity strength in world units per second", "change Gravity", "Life", GRAVITY, 5, 50, SPINNER_DECIMAL_PLACES, true, false);
            gravitySubPanel.setUserObject(GRAVITY);
            scrollTable.add(gravitySubPanel);
            if (newProperty == GRAVITY) scrollToActor = gravitySubPanel;
        }

        //Tint
        scrollTable.row();
        var tintSubPanel = new TintSubPanel();
        scrollTable.add(tintSubPanel);

        //Transparency
        scrollTable.row();
        var transparencySubPanel = new TransparencySubPanel();
        scrollTable.add(transparencySubPanel);

        //Options
        scrollTable.row();
        var optionsSubPanel = new OptionsSubPanel();
        scrollTable.add(optionsSubPanel);

        if (scrollToActor != null) {
            scrollTable.layout();
            scrollPane.layout();

            temp.set(0, 0);
            scrollToActor.localToActorCoordinates(scrollTable, temp);
            scrollPane.scrollTo(0, temp.y + scrollToActor.getHeight(), scrollToActor.getWidth(),
                scrollToActor.getHeight());

            scrollToActor.addAction(showEmitterPropertyInTable(scrollToActor));

            var foundScrollToActor = false;
            float distance = 0;
            var children = scrollTable.getChildren();

            for (int i = 0; i < children.size; i++) {
                var child = children.get(i);
                if (child == scrollToActor) {
                    foundScrollToActor = true;

                    if (i < children.size - 1) {
                        var nextActor = children.get(i + 1);
                        distance = scrollToActor.getY() + scrollToActor.getHeight() - nextActor.getY() - nextActor.getHeight();
                    }
                    continue;
                }
                if (!foundScrollToActor) continue;
                child.addAction(Actions.sequence(Actions.moveBy(0, distance), Actions.moveTo(child.getX(), child.getY(), .5f, Interpolation.smooth)));
            }
        }
    }

    public void removeProperty(ShownProperty shownProperty) {
        var foundProperty = false;
        float distance = 0;
        var children = scrollTable.getChildren();

        for (int i = 0; i < children.size; i++) {
            var child = children.get(i);
            if (child.getUserObject() == shownProperty) {
                temp.set(0, 0);
                child.localToActorCoordinates(scrollTable, temp);
                scrollPane.scrollTo(0, temp.y + child.getHeight(), child.getWidth(), child.getHeight());

                child.addAction(Actions.sequence(hideEmitterPropertyInTable(child), Actions.run(() -> populateScrollTable(null))));
                foundProperty = true;

                if (i < children.size - 1) {
                    var nextActor = children.get(i + 1);
                    distance = child.getY() + child.getHeight() - nextActor.getY() - nextActor.getHeight();
                }
                continue;
            }

            if (!foundProperty) continue;

            child.addAction(Actions.sequence(Actions.moveBy(0, distance, .5f, Interpolation.smooth)));
        }
    }
}
