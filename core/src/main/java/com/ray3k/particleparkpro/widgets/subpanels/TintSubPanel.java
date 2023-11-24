package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.FloatArray;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.TintUndoable;
import com.ray3k.particleparkpro.widgets.ColorGraph;
import com.ray3k.particleparkpro.widgets.ColorGraph.ColorGraphListener;
import com.ray3k.particleparkpro.widgets.ColorGraph.NodeData;
import com.ray3k.particleparkpro.widgets.Panel;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.handListener;
import static com.ray3k.particleparkpro.widgets.styles.Styles.colorGraphStyle;

public class TintSubPanel extends Panel {
    private static final float GRAPH_UNDO_DELAY = .5f;
    private Action graphUndoAction;

    /**
     * A widget that allows modification of the tint value of the currently selected emitter.
     */
    public TintSubPanel() {
        var value = selectedEmitter.getTint();
        setTouchable(Touchable.enabled);

        tabTable.padRight(7);

        tabTable.left();
        var label = new Label("Tint", skin, "header");
        tabTable.add(label).spaceRight(3);

        var colorGraph = new ColorGraph(colorGraphStyle);
        colorGraph.setNodes(value.getTimeline(), value.getColors());
        bodyTable.add(colorGraph).growX();
        colorGraph.setNodeListener(handListener);

        colorGraph.addListener(new ColorGraphListener() {
            final FloatArray newTimeline = new FloatArray();
            final FloatArray newColors = new FloatArray();

            private void setTimeLine() {
                newTimeline.clear();
                newColors.clear();

                for (var node : colorGraph.getNodes()) {
                    var nodeData = (NodeData) node.getUserObject();
                    newTimeline.add(nodeData.value);
                    newColors.add(nodeData.color.r);
                    newColors.add(nodeData.color.g);
                    newColors.add(nodeData.color.b);
                }
            }

            @Override
            public void added(Color color) {
                setTimeLine();

                var undo = new TintUndoable(selectedEmitter, value, colorGraph, "add Color");
                undo.getOldValue().setTimeline(value.getTimeline());
                undo.getOldValue().setColors(value.getColors());
                undo.getNewValue().setTimeline(newTimeline.toArray());
                undo.getNewValue().setColors(newColors.toArray());
                UndoManager.add(undo);
            }

            @Override
            public void removed(Color color) {
                setTimeLine();

                var undo = new TintUndoable(selectedEmitter, value, colorGraph, "remove Color");
                undo.getOldValue().setTimeline(value.getTimeline());
                undo.getOldValue().setColors(value.getColors());
                undo.getNewValue().setTimeline(newTimeline.toArray());
                undo.getNewValue().setColors(newColors.toArray());
                UndoManager.add(undo);
            }

            @Override
            public void moved(Color color) {
                if (graphUndoAction != null) {
                    graphUndoAction.restart();
                } else {
                    var oldTimeline = value.getTimeline();
                    var oldColors = value.getColors();

                    graphUndoAction = new TemporalAction(GRAPH_UNDO_DELAY) {
                        @Override
                        protected void update(float percent) {
                        }

                        @Override
                        protected void end() {
                            setTimeLine();

                            var undo = new TintUndoable(selectedEmitter, value, colorGraph, "move Color");
                            undo.getOldValue().setTimeline(oldTimeline);
                            undo.getOldValue().setColors(oldColors);
                            undo.getNewValue().setTimeline(newTimeline.toArray());
                            undo.getNewValue().setColors(newColors.toArray());
                            UndoManager.add(undo);

                            graphUndoAction = null;
                        }
                    };
                    stage.addAction(graphUndoAction);
                }

                setTimeLine();
                value.setTimeline(newTimeline.toArray());
                value.setColors(newColors.toArray());
            }

            @Override
            public void changed(Color color) {
                setTimeLine();

                var undo = new TintUndoable(selectedEmitter, value, colorGraph, "change Color");
                undo.getOldValue().setTimeline(value.getTimeline());
                undo.getOldValue().setColors(value.getColors());
                undo.getNewValue().setTimeline(newTimeline.toArray());
                undo.getNewValue().setColors(newColors.toArray());
                UndoManager.add(undo);
            }

            @Override
            public void changeCancelled(Color color) {
                setTimeLine();

                value.setTimeline(newTimeline.toArray());
                value.setColors(newColors.toArray());
            }

            @Override
            public void previewed(Color color) {
                setTimeLine();

                value.setTimeline(newTimeline.toArray());
                value.setColors(newColors.toArray());
            }
        });
    }
}
