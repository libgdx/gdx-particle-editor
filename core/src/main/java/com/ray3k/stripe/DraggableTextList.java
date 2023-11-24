package com.ray3k.stripe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class DraggableTextList extends DraggableList {
    private DraggableTextListStyle style;
    private final TextButtonStyle textButtonStyle;
    private final TextButtonStyle dragButtonStyle;
    private final TextButtonStyle validButtonStyle;
    private final TextButtonStyle invalidButtonStyle;
    private final ButtonGroup<TextButton> buttonGroup;
    private final Array<TextButton> dragButtons;
    private final Array<TextButton> validButtons;
    private final Array<TextButton> invalidButtons;
    private int textAlignment;
    private boolean programmaticChangeEvents = true;

    public DraggableTextList(boolean vertical, Skin skin) {
        this(vertical, skin, vertical ? "default-vertical" : "default-horizontal");
    }

    public DraggableTextList(boolean vertical, Skin skin, String style) {
        this(vertical, skin.get(style, DraggableTextListStyle.class));
    }

    public DraggableTextList(boolean vertical, DraggableTextListStyle style) {
        super(vertical, style);
        this.style = style;

        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = style.font;
        textButtonStyle.up = style.textBackgroundUp;
        textButtonStyle.over = style.textBackgroundOver;
        textButtonStyle.down = style.textBackgroundDown;
        textButtonStyle.checked = style.textBackgroundChecked;
        textButtonStyle.checkedOver = style.textBackgroundCheckedOver;
        textButtonStyle.fontColor = style.fontColor;
        textButtonStyle.overFontColor = style.overFontColor;
        textButtonStyle.downFontColor = style.downFontColor;
        textButtonStyle.checkedFontColor = style.checkedFontColor;
        textButtonStyle.checkedOverFontColor = style.checkedOverFontColor;

        dragButtonStyle = new TextButtonStyle();
        dragButtonStyle.font = style.font;
        dragButtonStyle.up = style.dragBackgroundUp != null ? style.dragBackgroundUp : style.textBackgroundUp;
        dragButtonStyle.fontColor = style.dragFontColor != null ? style.dragFontColor : style.fontColor;

        validButtonStyle = new TextButtonStyle();
        validButtonStyle.font = style.font;
        validButtonStyle.up = style.validBackgroundUp != null ? style.validBackgroundUp : dragButtonStyle.up;
        validButtonStyle.fontColor = style.validFontColor != null ? style.validFontColor : dragButtonStyle.fontColor;

        invalidButtonStyle = new TextButtonStyle();
        invalidButtonStyle.font = style.font;
        invalidButtonStyle.up = style.invalidBackgroundUp != null ? style.invalidBackgroundUp : dragButtonStyle.up;
        invalidButtonStyle.fontColor = style.invalidFontColor != null ? style.invalidFontColor : dragButtonStyle.fontColor;

        buttonGroup = new ButtonGroup<TextButton>();
        dragButtons = new Array<TextButton>();
        validButtons = new Array<TextButton>();
        invalidButtons = new Array<TextButton>();

        addListener(new DraggableListListener() {
            @Override
            public void removed(Actor actor, int index) {
                fire(new DraggableTextListRemovedEvent(((TextButton) actor).getText().toString(), index));
            }

            @Override
            public void reordered(Actor actor, int indexBefore, int indexAfter) {
                fire(new DraggableTextListReorderedEvent(((TextButton) actor).getText().toString(), indexBefore, indexAfter));
            }

            @Override
            public void selected(Actor actor) {
                fire(new DraggableTextListSelectedEvent(((TextButton) actor).getText().toString()));
            }
        });
    }

    @Override
    public void setStyle(DraggableListStyle style) {
        if (style == null) throw new NullPointerException("style cannot be null");
        if (!(style instanceof DraggableTextListStyle)) throw new IllegalArgumentException("style must be a DraggableTextListStyle.");
        super.setStyle(style);
        this.style = (DraggableTextListStyle) style;

        textButtonStyle.font = this.style.font;
        textButtonStyle.up = this.style.textBackgroundUp;
        textButtonStyle.over = this.style.textBackgroundOver;
        textButtonStyle.down = this.style.textBackgroundDown;
        textButtonStyle.checked = this.style.textBackgroundChecked;
        textButtonStyle.checkedOver = this.style.textBackgroundCheckedOver;
        textButtonStyle.fontColor = this.style.fontColor;
        textButtonStyle.overFontColor = this.style.overFontColor;
        textButtonStyle.downFontColor = this.style.downFontColor;
        textButtonStyle.checkedFontColor = this.style.checkedFontColor;
        textButtonStyle.checkedOverFontColor = this.style.checkedOverFontColor;
        for (TextButton textButton : buttonGroup.getButtons()) {
            textButton.setStyle(textButtonStyle);
        }

        dragButtonStyle.font = this.style.font;
        dragButtonStyle.up = this.style.dragBackgroundUp != null ? this.style.dragBackgroundUp : this.style.textBackgroundUp;
        dragButtonStyle.fontColor = this.style.dragFontColor != null ? this.style.dragFontColor : this.style.fontColor;
        for (TextButton textButton : dragButtons) {
            textButton.setStyle(dragButtonStyle);
        }

        validButtonStyle.font = this.style.font;
        validButtonStyle.up = this.style.validBackgroundUp != null ? this.style.validBackgroundUp : dragButtonStyle.up;
        validButtonStyle.fontColor = this.style.validFontColor != null ? this.style.validFontColor : dragButtonStyle.fontColor;
        for (TextButton textButton : validButtons) {
            textButton.setStyle(invalidButtonStyle);
        }

        invalidButtonStyle.font = this.style.font;
        invalidButtonStyle.up = this.style.invalidBackgroundUp != null ? this.style.invalidBackgroundUp : dragButtonStyle.up;
        invalidButtonStyle.fontColor = this.style.invalidFontColor != null ? this.style.invalidFontColor : dragButtonStyle.fontColor;
        for (TextButton textButton : invalidButtons) {
            textButton.setStyle(invalidButtonStyle);
        }
    }

    public void addText(String text) {
        TextButton actor = new TextButton(text, new TextButtonStyle(textButtonStyle));
        actor.setProgrammaticChangeEvents(false);
        actor.getLabel().setAlignment(textAlignment);
        buttonGroup.add(actor);
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.stop();
            }
        });

        TextButton dragActor = new TextButton(text, dragButtonStyle);
        dragButtons.add(dragActor);

        TextButton validDragActor = new TextButton(text, validButtonStyle);
        validButtons.add(validDragActor);

        TextButton invalidDragActor = new TextButton(text, invalidButtonStyle);
        invalidButtons.add(invalidDragActor);

        super.add(actor, dragActor, validDragActor, invalidDragActor);
    }

    public void addAllTexts(Array<String> texts) {
        for (String text : texts) {
            addText(text);
        }
    }

    public void addAllTexts(String... texts) {
        for (String text : texts) {
            addText(text);
        }
    }

    public Array<String> getTexts() {
        Array<String> returnValue = new Array<String>();
        for (Actor actor : actors) {
            TextButton textButton = (TextButton) actor;
            returnValue.add(textButton.getText().toString());
        }
        return returnValue;
    }

    @Override
    @Deprecated
    public void add(Actor actor) {
        super.add(actor);
    }

    @Override
    @Deprecated
    public void add(Actor actor, Actor dragActor, Actor validDragActor, Actor invalidDragActor) {
        super.add(actor, dragActor, validDragActor, invalidDragActor);
    }

    @Override
    @Deprecated
    public void addAll(Array<Actor> actors) {
        super.addAll(actors);
    }

    @Override
    @Deprecated
    public void addAll(Array<Actor> actors, Array<Actor> dragActors, Array<Actor> validDragActors, Array<Actor> invalidDragActors) {
        super.addAll(actors, dragActors, validDragActors, invalidDragActors);
    }

    public void setProgrammaticChangeEvents(boolean programmaticChangeEvents) {
        for (Actor actor : actors) {
            TextButton textButton = (TextButton) actor;
            textButton.setProgrammaticChangeEvents(programmaticChangeEvents);
        }
    }

    public boolean isProgrammaticChangeEvents() {
        return programmaticChangeEvents;
    }

    public CharSequence getSelected() {
        return buttonGroup.getChecked().getText();
    }

    public int getSelectedIndex() {
        return buttonGroup.getCheckedIndex();
    }

    public void setSelected(String text) {
        for (int i = 0; i < buttonGroup.getButtons().size; i++) {
            TextButton textButton = buttonGroup.getButtons().get(i);
            if (textButton.getText().equals(text)) {
                setSelected(i);
                break;
            }
        }
    }

    @Override
    public Actor getSelectedActor() {
        return buttonGroup.getChecked();
    }

    @Override
    public void setSelectedActor(Actor selectedActor) {
        Array<TextButton> buttons = buttonGroup.getButtons();
        for (int i = 0; i < buttons.size; i++) {
            if (buttons.get(i) == selectedActor) {
                setSelected(i);
                break;
            }
        }
    }

    public void setSelected(int index) {
        TextButton textButton = buttonGroup.getButtons().get(index);
        textButton.setChecked(true);
        if (programmaticChangeEvents) {
            fire(new DraggableListSelectedEvent(textButton));
            fire(new ChangeEvent());
        }
    }

    public ButtonGroup getButtonGroup() {
        return buttonGroup;
    }

    @Override
    public void clearChildren(boolean unfocus) {
        super.clearChildren(unfocus);
        buttonGroup.clear();
        dragButtons.clear();
        validButtons.clear();
        invalidButtons.clear();
    }

    @Override
    protected void updateTable() {
        super.updateTable();
        buttonGroup.clear();
        for (Actor actor : actors) {
            buttonGroup.add((TextButton) actor);
        }
    }

    public void setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
        for (TextButton button : buttonGroup.getButtons()) {
            button.getLabel().setAlignment(textAlignment);
        }
    }

    public static class DraggableTextListStyle extends DraggableListStyle {
        public BitmapFont font;
        /** Optional. */
        public Drawable textBackgroundUp, textBackgroundDown, textBackgroundOver, textBackgroundChecked,
            textBackgroundCheckedOver, dragBackgroundUp, validBackgroundUp, invalidBackgroundUp;
        public Color fontColor, downFontColor, overFontColor, checkedFontColor, checkedOverFontColor, dragFontColor,
            validFontColor, invalidFontColor;

        public DraggableTextListStyle() {
        }

        public DraggableTextListStyle(DraggableTextListStyle style) {
            background = style.background;
            dividerUp = style.dividerUp;
            dividerOver = style.dividerOver;
            font = style.font;
            textBackgroundUp = style.textBackgroundUp;
            textBackgroundDown = style.textBackgroundDown;
            textBackgroundOver = style.textBackgroundOver;
            textBackgroundChecked = style.textBackgroundChecked;
            textBackgroundCheckedOver = style.textBackgroundCheckedOver;
            dragBackgroundUp = style.dragBackgroundUp;
            validBackgroundUp = style.validBackgroundUp;
            invalidBackgroundUp = style.invalidBackgroundUp;
            fontColor = style.fontColor;
            downFontColor = style.downFontColor;
            overFontColor = style.overFontColor;
            checkedFontColor = style.checkedFontColor;
            checkedOverFontColor = style.checkedOverFontColor;
            dragFontColor = style.dragFontColor;
            validFontColor = style.validFontColor;
            invalidFontColor = style.invalidFontColor;
        }
    }

    @Override
    protected void dragStart() {
        super.dragStart();
        for (TextButton textButton : buttonGroup.getButtons()) {
            textButton.getStyle().over = null;
        }
    }

    @Override
    protected void dragStop() {
        super.dragStop();
        for (TextButton textButton : buttonGroup.getButtons()) {
            textButton.getStyle().over = style.textBackgroundOver;
        }
    }

    public static class DraggableTextListRemovedEvent extends Event {
        public String text;
        public int index;

        public DraggableTextListRemovedEvent(String text, int index) {
            this.text = text;
            this.index = index;
        }
    }

    public static class DraggableTextListReorderedEvent extends Event {
        public String text;
        public int indexBefore;
        public int indexAfter;

        public DraggableTextListReorderedEvent(String text, int indexBefore, int indexAfter) {
            this.text = text;
            this.indexBefore = indexBefore;
            this.indexAfter = indexAfter;
        }
    }

    public static class DraggableTextListSelectedEvent extends Event {
        public String text;

        public DraggableTextListSelectedEvent(String text) {
            this.text = text;
        }
    }

    public abstract static class DraggableTextListListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof DraggableTextListRemovedEvent) {
                removed(((DraggableTextListRemovedEvent) event).text, ((DraggableTextListRemovedEvent) event).index);
                return true;
            } else if (event instanceof DraggableTextListReorderedEvent) {
                DraggableTextListReorderedEvent reorderedEvent = (DraggableTextListReorderedEvent) event;
                reordered(reorderedEvent.text, reorderedEvent.indexBefore, reorderedEvent.indexAfter);
                return true;
            } else if (event instanceof DraggableTextListSelectedEvent) {
                selected(((DraggableTextListSelectedEvent) event).text);
                return true;
            } else return false;
        }

        public abstract void removed(String text, int indexRemoved);
        public abstract void reordered(String text, int indexBefore, int indexAfter);
        public abstract void selected(String text);
    }
}
