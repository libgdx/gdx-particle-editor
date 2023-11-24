package com.ray3k.stripe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class DraggableList extends WidgetGroup {
    private DraggableListStyle style;
    private final Table table;
    protected Array<Actor> actors;
    private final ObjectMap<Actor, Actor> dragActors;
    private final ObjectMap<Actor, Actor> validDragActors;
    private final ObjectMap<Actor, Actor> invalidDragActors;
    private final DragAndDrop dragAndDrop;
    private final ButtonStyle dividerStyle;
    private final Array<Button> dividers;
    private final boolean vertical;
    private boolean draggable;
    private boolean allowRemoval = true;
    private Actor selectedActor;

    public DraggableList(boolean vertical, Skin skin) {
        this(vertical, skin, vertical ? "default-vertical" : "default-horizontal");
    }

    public DraggableList(boolean vertical, Skin skin, String style) {
        this(vertical, skin.get(style, DraggableListStyle.class));
    }

    public DraggableList(boolean vertical, DraggableListStyle style) {
        draggable = true;
        this.vertical = vertical;
        this.style = style;

        dragAndDrop = new DragAndDrop();

        dividerStyle = new ButtonStyle();
        dividerStyle.up = style.dividerUp;
        dividerStyle.over = style.dividerOver;
        dividerStyle.checked = style.dividerOver;

        dividers = new Array<Button>();
        dragActors = new ObjectMap<Actor, Actor>();
        validDragActors = new ObjectMap<Actor, Actor>();
        invalidDragActors = new ObjectMap<Actor, Actor>();

        table = new Table();
        table.setBackground(style.background);
        table.setTouchable(Touchable.enabled);
        addActor(table);

        actors = new Array<Actor>();
    }

    public void setStyle(DraggableListStyle style) {
        if (style == null) throw new NullPointerException("style cannot be null");
        this.style = style;

        dividerStyle.up = style.dividerUp;
        dividerStyle.over = style.dividerOver;
        dividerStyle.checked = style.dividerOver;
        for (Button divider : dividers) {
            divider.setStyle(dividerStyle);
        }

        table.setBackground(style.background);
    }

    public void add(Actor actor) {
        add(actor, null, null, null);
    }

    public void add(final Actor actor, Actor dragActor, Actor validDragActor, Actor invalidDragActor) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fire(new ChangeEvent());
                fire(new DraggableListSelectedEvent(actor));
                selectedActor = actor;
            }
        });
        actors.add(actor);
        dragActors.put(actor, dragActor);
        validDragActors.put(actor, validDragActor);
        invalidDragActors.put(actor, invalidDragActor);
        updateTable();
    }

    public void addAll(Array<Actor> actors) {
        addAll(actors, null, null, null);
    }

    public void addAll(Array<Actor> actors, Array<Actor> dragActors, Array<Actor> validDragActors, Array<Actor> invalidDragActors) {
        for (int i = 0; i < actors.size; i++) {
            final Actor actor = actors.get(i);
            Actor dragActor = null;
            Actor validDragActor = null;
            Actor invalidDragActor = null;

            if (dragActors != null && i < dragActors.size) {
                dragActor = dragActors.get(i);
            }

            if (validDragActors != null && i < validDragActors.size) {
                validDragActor = validDragActors.get(i);
            }

            if (invalidDragActors != null && i < invalidDragActors.size) {
                invalidDragActor = invalidDragActors.get(i);
            }

            add(actor, dragActor, validDragActor, invalidDragActor);
        }
    }

    @Override
    public void clearChildren(boolean unfocus) {
        actors.clear();
        dragActors.clear();
        validDragActors.clear();
        invalidDragActors.clear();
        updateTable();
    }

    protected void updateTable() {
        dragAndDrop.clear();
        dragAndDrop.setDragTime(0);
        for (Button divider : dividers) {
            removeActor(divider);
        }
        dividers.clear();

        table.clearChildren();
        for (final Actor actor : actors) {
            if (vertical) table.row();
            table.add(actor).growX();
        }
        if (vertical) table.row();

        for (int i = 0; i < actors.size + 1; i++) {
            Button button = new Button(dividerStyle);
            button.setProgrammaticChangeEvents(false);
            button.setVisible(false);
            addActor(button);
            dividers.add(button);
        }

        updateDragAndDrop();
    }

    private void updateDragAndDrop() {
        dragAndDrop.clear();
        dragAndDrop.setDragTime(0);

        if (!draggable) return;

        for (final Actor actor : actors) {
            dragAndDrop.addSource(new Source(actor) {
                Color previousColor;
                @Override
                public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    previousColor = new Color(actor.getColor());
                    actor.setColor(1, 1, 1, style.transparencyOnDrag);

                    for (Button divider : dividers) {
                        divider.setVisible(true);
                        divider.setChecked(false);
                    }

                    int index = actors.indexOf(getActor(), true);
                    if (index == 0) dividers.get(0).setVisible(false);
                    if (index == actors.size - 1) dividers.peek().setVisible(false);

                    Payload payload = new Payload();
                    payload.setDragActor(dragActors.get(actor));
                    payload.setValidDragActor(validDragActors.get(actor));
                    payload.setInvalidDragActor(invalidDragActors.get(actor));
                    payload.setObject(actor);

                    DraggableList.this.dragStart();
                    return payload;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    for (Button divider : dividers) {
                        divider.setChecked(false);
                    }
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                    actor.setColor(previousColor);

                    for (Button divider : dividers) {
                        divider.setVisible(false);
                    }

                    if (allowRemoval && target == null) {
                        Actor payloadActor = (Actor) payload.getObject();
                        var index = actors.indexOf(payloadActor, true);
                        actors.removeValue(payloadActor, true);
                        updateTable();
                        fire(new ChangeEvent());
                        fire(new DraggableListRemovedEvent(payloadActor, index));
                    }

                    DraggableList.this.dragStop();
                }
            });
            dragAndDrop.addTarget(new Target(actor) {
                @Override
                public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                    int index = actors.indexOf(getActor(), true);
                    if (vertical) {
                        if (y < getActor().getHeight() / 2) index++;
                    } else {
                        if (x > getActor().getWidth() / 2) index++;
                    }
                    for (Button divider : dividers) {
                        divider.setChecked(false);
                    }
                    dividers.get(index).setChecked(true);
                    return true;
                }

                @Override
                public void drop(Source source, Payload payload, float x, float y, int pointer) {
                    Actor payloadActor = (Actor) payload.getObject();
                    if (!getActor().equals(payloadActor)) {
                        int indexBefore = actors.indexOf(payloadActor, true);
                        actors.removeValue(payloadActor, true);
                        int indexAfter = actors.indexOf(getActor(), true);
                        if (vertical) {
                            if (y < getActor().getHeight() / 2) indexAfter++;
                        } else {
                            if (x > getActor().getWidth() / 2) indexAfter++;
                        }
                        actors.insert(Math.min(indexAfter, actors.size), payloadActor);
                        updateTable();
                        fire(new ChangeEvent());
                        fire(new DraggableListReorderedEvent(payloadActor, indexBefore, indexAfter));
                    }
                }
            });
        }

        for (int i = 0; i < dividers.size; i++) {
            Button divider = dividers.get(i);
            final int index = i;
            dragAndDrop.addTarget(new Target(divider) {
                @Override
                public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                    return true;
                }

                @Override
                public void drop(Source source, Payload payload, float x, float y, int pointer) {
                    int indexAfter = index;
                    Actor payloadActor = (Actor) payload.getObject();
                    int indexBefore = actors.indexOf(payloadActor, true);
                    if (indexBefore < indexAfter) indexAfter--;
                    actors.removeValue(payloadActor, true);
                    actors.insert(Math.min(indexAfter, actors.size), payloadActor);
                    updateTable();
                    fire(new ChangeEvent());
                    fire(new DraggableListReorderedEvent(payloadActor, indexBefore, indexAfter));
                }
            });
        }

        dragAndDrop.addTarget(new Target(table) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                if (actors.size > 1) {
                    int actorIndex = actors.indexOf((Actor) payload.getObject(),true);
                    boolean checkFirst = true;
                    if (vertical && y < table.getChildren().first().getY()) checkFirst = false;
                    else if (!vertical && x > table.getChildren().peek().getX() + table.getChildren().peek().getWidth()) checkFirst = false;

                    for (Button divider : dividers) {
                        divider.setChecked(false);
                    }
                    dividers.get(checkFirst ? (actorIndex == 0 ? 1 : 0) : (actorIndex == actors.size - 1 ? dividers.size - 2 : dividers.size - 1)).setChecked(true);
                }
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                if (actors.size <= 1) return;

                Actor payloadActor = (Actor) payload.getObject();
                int indexBefore = actors.indexOf(payloadActor, true);

                actors.removeValue(payloadActor, true);

                boolean addFirst = true;
                if (vertical && y < actors.peek().getY(Align.center)) addFirst = false;
                else if (!vertical && x > actors.peek().getX(Align.center)) addFirst = false;

                if (addFirst) actors.insert(0, payloadActor);
                else actors.add(payloadActor);

                updateTable();
                fire(new ChangeEvent());
                fire(new DraggableListReorderedEvent(payloadActor, indexBefore, actors.indexOf(payloadActor, true)));
            }
        });
    }

    public Actor getSelectedActor() {
        return selectedActor;
    }

    public void setSelectedActor(Actor selectedActor) {
        this.selectedActor = selectedActor;
    }

    @Override
    public float getMinWidth() {
        return table.getMinWidth();
    }

    @Override
    public float getMinHeight() {
        return table.getMinHeight();
    }

    @Override
    public float getPrefWidth() {
        return table.getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        return table.getPrefHeight();
    }

    @Override
    public float getMaxWidth() {
        return table.getMaxWidth();
    }

    @Override
    public float getMaxHeight() {
        return table.getMaxHeight();
    }

    @Override
    public void layout() {
        table.setSize(getWidth(), getHeight());
        table.layout();

        if (actors.size > 0) for (int i = 0; i < dividers.size; i++) {
            Button button = dividers.get(i);
            Actor actor;
            if (vertical) {
                if (i < actors.size) {
                    actor = actors.get(i);
                    button.setPosition(actor.getX(), actor.getY() + actor.getHeight() - button.getHeight() / 2.0f);
                } else {
                    actor = actors.get(actors.size - 1);
                    button.setPosition(actor.getX(), actor.getY() - button.getHeight() / 2.0f);
                }
                button.setWidth(actor.getWidth());
            } else {
                if (i < actors.size) {
                    actor = actors.get(i);
                    button.setPosition(actor.getX() - button.getWidth() / 2.0f, actor.getY());
                } else {
                    actor = actors.get(actors.size - 1);
                    button.setPosition(actor.getX() + actor.getWidth() - button.getWidth() / 2.0f, actor.getY());
                }
                button.setHeight(actor.getHeight());
            }
            button.layout();
        }
    }

    @Override
    @Deprecated
    public void addActor(Actor actor) {
        super.addActor(actor);
    }

    @Override
    @Deprecated
    public void addActorAt(int index, Actor actor) {
        super.addActorAt(index, actor);
    }

    @Override
    @Deprecated
    public void addActorBefore(Actor actorBefore, Actor actor) {
        super.addActorBefore(actorBefore, actor);
    }

    @Override
    @Deprecated
    public void addActorAfter(Actor actorAfter, Actor actor) {
        super.addActorAfter(actorAfter, actor);
    }

    @Override
    public boolean removeActor(Actor actor, boolean unfocus) {
        actors.removeValue(actor, true);
        return super.removeActor(actor, unfocus);
    }

    public Table getTable() {
        return table;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;

        updateDragAndDrop();
    }

    public boolean isAllowRemoval() {
        return allowRemoval;
    }

    public void setAllowRemoval(boolean allowRemoval) {
        this.allowRemoval = allowRemoval;
    }

    public DraggableListStyle getStyle() {
        return style;
    }

    protected void dragStart() {

    }

    protected void dragStop() {

    }

    public static class DraggableListRemovedEvent extends Event {
        public Actor actor;
        public int index;

        public DraggableListRemovedEvent(Actor actor, int index) {
            this.actor = actor;
            this.index = index;
        }
    }

    public static class DraggableListReorderedEvent extends Event {
        public Actor actor;
        public int indexBefore;
        public int indexAfter;

        public DraggableListReorderedEvent(Actor actor, int indexBefore, int indexAfter) {
            this.actor = actor;
            this.indexBefore = indexBefore;
            this.indexAfter = indexAfter;
        }
    }

    public static class DraggableListSelectedEvent extends Event {
        public Actor actor;

        public DraggableListSelectedEvent(Actor actor) {
            this.actor = actor;
        }
    }

    public abstract static class DraggableListListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof DraggableListRemovedEvent) {
                removed(((DraggableListRemovedEvent) event).actor, ((DraggableListRemovedEvent) event).index);
                return true;
            } else if (event instanceof DraggableListReorderedEvent) {
                DraggableListReorderedEvent reorderedEvent = (DraggableListReorderedEvent) event;
                reordered(reorderedEvent.actor, reorderedEvent.indexBefore, reorderedEvent.indexAfter);
                return true;
            } else if (event instanceof DraggableListSelectedEvent) {
                selected(((DraggableListSelectedEvent) event).actor);
                return true;
            } else return false;
        }

        public abstract void removed(Actor actor, int index);
        public abstract void reordered(Actor actor, int indexBefore, int indexAfter);
        public abstract void selected(Actor actor);
    }

    public static class DraggableListStyle {
        public Drawable dividerUp, dividerOver;
        /** Optional **/
        public Drawable background;
        public float transparencyOnDrag;

        public DraggableListStyle() {
        }

        public DraggableListStyle(DraggableListStyle style) {
            background = style.background;
            dividerUp = style.dividerUp;
            dividerOver = style.dividerOver;
            transparencyOnDrag = style.transparencyOnDrag;
        }
    }

    public void align(int alignment) {
        table.align(alignment);
    }

    public int getAlign() {
        return table.getAlign();
    }

    public DragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }
}
