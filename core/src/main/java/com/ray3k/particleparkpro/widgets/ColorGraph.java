package com.ray3k.particleparkpro.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.colorful.FloatColors;
import com.github.tommyettinger.colorful.rgb.ColorTools;
import com.ray3k.stripe.PopColorPicker;
import com.ray3k.stripe.PopColorPicker.PopColorPickerListener;
import com.ray3k.stripe.PopTable.TableShowHideListener;
import com.ray3k.tenpatch.TenPatchDrawable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.ray3k.particleparkpro.Core.foregroundStage;
import static com.ray3k.particleparkpro.Core.stage;
import static com.ray3k.particleparkpro.Listeners.handListener;
import static com.ray3k.particleparkpro.Listeners.ibeamListener;
import static com.ray3k.particleparkpro.widgets.ColorGraph.ColorGraphEventType.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.popColorPickerStyle;

/**
 * A widget that allows the user to modify colors over a timeline. Nodes can be added and removed from the timeline,
 * with each being able to be individually repositioned. Clicking a node opens a color pop that allows the user to
 * select a new color.
 */
public class ColorGraph extends Table {
    private ColorGraphStyle style;
    private ImageButtonStyle nodeStartStyle;
    private ImageButtonStyle nodeStyle;
    private ImageButtonStyle nodeEndStyle;
    @Getter private EventListener nodeListener;
    @Getter private final Array<ImageButton> nodes = new Array<>();
    private boolean createNewNode;
    private boolean openColorPicker;
    private boolean allowDrag;
    private static final Vector2 temp = new Vector2();
    private Action colorPickerAction;
    private Table colorTable;
    private Table nodeTable;

    public ColorGraph(ColorGraphStyle style) {
        nodeStartStyle = new ImageButtonStyle();
        nodeStyle = new ImageButtonStyle();
        nodeEndStyle = new ImageButtonStyle();
        setStyle(style);

        var stack = new Stack();
        add(stack).grow();

        colorTable = new Table();
        stack.add(colorTable);

        nodeTable = new Table();
        stack.add(nodeTable);

        initialize();
    }

    public ColorGraph(Skin skin) {
        this(skin.get(ColorGraphStyle.class));
    }

    public ColorGraph(Skin skin, String style) {
        this(skin.get(style, ColorGraphStyle.class));
    }

    public void setStyle(ColorGraphStyle style) {
        this.style = style;
        setBackground(style.background);

        nodeStartStyle.up = style.nodeStartUp;
        nodeStartStyle.down = style.nodeStartDown;
        nodeStartStyle.over = style.nodeStartOver;
        nodeStartStyle.imageUp = style.nodeStartFill;

        nodeStyle.up = style.nodeUp;
        nodeStyle.down = style.nodeDown;
        nodeStyle.over = style.nodeOver;
        nodeStyle.imageUp = style.nodeFill;

        nodeEndStyle.up = style.nodeEndUp;
        nodeEndStyle.down = style.nodeEndDown;
        nodeEndStyle.over = style.nodeEndOver;
        nodeEndStyle.imageUp = style.nodeEndFill;
    }

    public void initialize() {
        setTouchable(Touchable.enabled);
        nodeTable.setTouchable(Touchable.enabled);
        nodeTable.addListener(createDragListener());

        createNode(0, null, true);
    }

    @Override
    public void layout() {
        super.layout();

        for (int i = 0; i < nodes.size; i++) {
            var node = nodes.get(i);
            var nodeData = (NodeData) node.getUserObject();
            node.setX(MathUtils.round(nodeData.value * nodeTable.getWidth() - node.getWidth() / 2));
            node.setY(getPadBottom() + (getHeight() - getPadTop() - getPadBottom()) / 2, Align.center);

            var image = (Image) colorTable.getChild(i);

            image.setX(MathUtils.round(nodeData.value * colorTable.getWidth()));

            float widthValue;
            if (i + 1 < nodes.size) {
                var nextNode = nodes.get(i + 1);
                var nextNodeData = (NodeData) nextNode.getUserObject();

                widthValue = nextNodeData.value - nodeData.value;
                nodeData.tenPatch.setColor1(nodeData.color);
                nodeData.tenPatch.setColor2(nodeData.color);
                nodeData.tenPatch.setColor3(nextNodeData.color);
                nodeData.tenPatch.setColor4(nextNodeData.color);
            } else {
                widthValue = 1 - nodeData.value;
                nodeData.tenPatch.setColor1(nodeData.color);
                nodeData.tenPatch.setColor2(nodeData.color);
                nodeData.tenPatch.setColor3(nodeData.color);
                nodeData.tenPatch.setColor4(nodeData.color);
            }
            image.setSize(MathUtils.round(widthValue * colorTable.getWidth()), colorTable.getHeight());
        }
    }

    private DragListener createDragListener() {
        return new DragListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                createNewNode = true;
                allowDrag = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (createNewNode) {
                    var nodeData = createNode(x / nodeTable.getWidth(), null, false);
                    fire(new ChangeEvent());
                    fire(new ColorGraphEvent(ADD, nodeData.color));
                }
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                createNewNode = false;
                openColorPicker = false;
            }
        };
    }

    private NodeData createNode(float value, Color color, boolean stationary) {
        final var tapCountInterval = .4f;

        var node = new ImageButton(nodeStyle);
        var nodeData = new NodeData();
        nodeData.value = value;
        nodeData.color = color;
        float x = MathUtils.isZero(getWidth()) ? 0 : value * nodeTable.getWidth();
        node.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.stop();
            }
        });

        ImageButton previousNode = null, nextNode = null;
        for (int i = 0; i < nodes.size; i++) {
            var testNode = nodes.get(i);
            if (x > testNode.getX()) previousNode = testNode;
            else if (x < testNode.getX()) {
                nextNode = testNode;
                break;
            }
        }

        if (nodeData.color == null) {
            var previousColor = previousNode != null ? ((NodeData) previousNode.getUserObject()).color : Color.RED;
            if (nextNode == null) nodeData.color = new Color(previousColor);
            else {
                var nextColor = ((NodeData) nextNode.getUserObject()).color;
                var previousX = previousNode.getX();
                var nextX = nextNode.getX();
                var packed = FloatColors.lerpFloatColors(
                    ColorTools.fromColor(previousColor),
                    ColorTools.fromColor(nextColor),
                    (x - previousX) / (nextX - previousX));
                nodeData.color = new Color();
                ColorTools.toColor(nodeData.color, packed);
            }
        }

        nodeData.tenPatch = new TenPatchDrawable(style.white);
        node.setUserObject(nodeData);

        if (nodeListener != null) node.addListener(nodeListener);

        nodeTable.addActor(node);
        nodes.add(node);

        node.setPosition(x, nodeTable.getHeight() / 2, Align.center);
        sortNodes();
        updateColors();

        var clickListener = new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                createNewNode = false;
                if (pointer == -1) openColorPicker = true;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() == 1 && event.getButton() == Buttons.LEFT && openColorPicker) {
                    openColorPicker = false;
                    colorPickerAction = Actions.delay(tapCountInterval,
                        Actions.run(() -> Gdx.app.postRunnable(() -> {
                            colorPickerAction = null;
                            allowDrag = false;
                            Gdx.input.setInputProcessor(foregroundStage);
                            var cp = new PopColorPicker(nodeData.color, popColorPickerStyle);
                            cp.setHideOnUnfocus(true);
                            cp.setButtonListener(handListener);
                            cp.setTextFieldListener(ibeamListener);
                            cp.setDraggable(true);
                            cp.setKeepCenteredInWindow(false);
                            cp.addListener(new PopColorPickerListener() {
                                @Override
                                public void picked(Color color) {
                                    color.a = 1;
                                    nodeData.color.set(color);
                                    updateColors();
                                    fire(new ChangeEvent());
                                    fire(new ColorGraphEvent(CHANGE, nodeData.color));
                                }

                                @Override
                                public void updated(Color color) {
                                    color.a = 1;
                                    nodeData.color.set(color);
                                    updateColors();
                                    fire(new ChangeEvent());
                                    fire(new ColorGraphEvent(PREVIEW, nodeData.color));
                                }

                                @Override
                                public void cancelled(Color oldColor) {
                                    nodeData.color.set(oldColor);
                                    updateColors();
                                    fire(new ChangeEvent());
                                    fire(new ColorGraphEvent(CHANGE_CANCEL, nodeData.color));
                                }
                            });
                            cp.show(foregroundStage);
                            cp.addListener(new TableShowHideListener() {
                                @Override
                                public void tableShown(Event event) {

                                }

                                @Override
                                public void tableHidden(Event event) {
                                    Gdx.input.setInputProcessor(stage);
                                }
                            });
                        })));
                    node.addAction(colorPickerAction);
                }

                if (event.getButton() == Buttons.RIGHT || getTapCount() >= 2) {
                    if (nodes.indexOf(node, true) != 0) {
                        node.remove();
                        nodes.removeValue(node, true);
                        sortNodes();
                        updateColors();
                        fire(new ChangeEvent());
                        fire(new ColorGraphEvent(REMOVE, nodeData.color));
                    } else {
                        node.removeAction(colorPickerAction);
                    }
                }
            }
        };
        clickListener.setButton(-1);
        clickListener.setTapCountInterval(tapCountInterval);
        node.addListener(clickListener);

        var dragListener = new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                clickListener.cancel();
                openColorPicker = false;
                if (allowDrag && !stationary) {
                    temp.set(x, y);
                    node.localToActorCoordinates(nodeTable, temp);

                    nodeData.value = MathUtils.clamp(temp.x / nodeTable.getWidth(), 0, 1);

                    sortNodes();
                    updateColors();
                    fire(new ChangeEvent());
                    fire(new ColorGraphEvent(MOVE, nodeData.color));
                }
            }
        };
        dragListener.setTapSquareSize(5);
        node.addListener(dragListener);
        return nodeData;
    }

    public void setNodes(float[] timeline, float[] colors) {
        while (nodes.size > 0) {
            nodes.get(0).remove();
            nodes.removeIndex(0);
        }

        for (int i = 0; i < timeline.length; i++) {
            var colorIndex = i * 3;
            var color = new Color(colors[colorIndex], colors[colorIndex + 1], colors[colorIndex + 2], 1);
            createNode(timeline[i], color, i == 0);
        }
    }

    private void sortNodes() {
        nodes.sort((o1, o2) -> Float.compare(o1.getX(), o2.getX()));

        for (int i = 0; i < nodes.size; i++) {
            var node = nodes.get(i);
            if (i == 0) node.setStyle(nodeStartStyle);
            else if (i == nodes.size - 1) node.setStyle(nodeEndStyle);
            else node.setStyle(nodeStyle);
        }
    }

    private void updateColors() {
        colorTable.clearChildren();

        for (int i = 0; i < nodes.size; i++) {
            var node =  nodes.get(i);
            var nodeData = (NodeData) node.getUserObject();

            node.getImage().setColor(nodeData.color);

            var image = new Image(nodeData.tenPatch);
            colorTable.addActor(image);
        }
    }

    public void setNodeListener(EventListener nodeListener) {
        if (this.nodeListener != null) {
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                node.removeListener(this.nodeListener);
            }
        }
        this.nodeListener = nodeListener;
        for (int i = 0; i < nodes.size; i++) {
            var node = nodes.get(i);
            node.addListener(nodeListener);
        }
    }

    public static class NodeData {
        public Color color;
        public TenPatchDrawable tenPatch;
        public float value;
    }

    public static class ColorGraphStyle {
        public Drawable background;
        public Drawable nodeStartUp;
        public Drawable nodeStartOver;
        public Drawable nodeStartDown;
        public Drawable nodeStartFill;
        public Drawable nodeUp;
        public Drawable nodeOver;
        public Drawable nodeDown;
        public Drawable nodeFill;
        public Drawable nodeEndUp;
        public Drawable nodeEndOver;
        public Drawable nodeEndDown;
        public Drawable nodeEndFill;
        public TenPatchDrawable white;
    }

    public enum ColorGraphEventType {
        ADD, REMOVE, MOVE, CHANGE, CHANGE_CANCEL, PREVIEW
    }

    @AllArgsConstructor
    public static class ColorGraphEvent extends Event {
        public ColorGraphEventType type;
        public Color color;
    }

    public static abstract class ColorGraphListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof ColorGraphEvent) {
                var colorGraphEvent = (ColorGraphEvent) event;
                switch (colorGraphEvent.type) {
                    case ADD:
                        added(colorGraphEvent.color);
                        break;
                    case REMOVE:
                        removed(colorGraphEvent.color);
                        break;
                    case MOVE:
                        moved(colorGraphEvent.color);
                        break;
                    case CHANGE:
                        changed(colorGraphEvent.color);
                        break;
                    case CHANGE_CANCEL:
                        changeCancelled(colorGraphEvent.color);
                        break;
                    case PREVIEW:
                        previewed(colorGraphEvent.color);
                        break;
                }
                return true;
            }
            return false;
        }

        public abstract void added(Color color);
        public abstract void removed(Color color);
        public abstract void moved(Color color);
        public abstract void changed(Color color);
        public abstract void changeCancelled(Color color);
        public abstract void previewed(Color color);
    }
}
