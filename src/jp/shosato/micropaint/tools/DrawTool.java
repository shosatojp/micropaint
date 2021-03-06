package jp.shosato.micropaint.tools;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.joml.Vector2d;
import org.joml.Vector4d;

import jp.shosato.micropaint.components.Canvas;
import jp.shosato.micropaint.components.FigureComponent;
import jp.shosato.micropaint.events.mouse.MouseClickEventListener;
import jp.shosato.micropaint.events.mouse.MouseEvent;
import jp.shosato.micropaint.events.mouse.MouseMoveEventListener;

/**
 * 図形を描画するツール
 */
public class DrawTool extends Tool implements MouseMoveEventListener, MouseClickEventListener {

    /**
     * 現在描画中の図形
     */
    private FigureComponent current;

    private Vector4d defaultPointColor = new Vector4d(1, 1, 1, 1);
    private Vector4d highlightPointColor = new Vector4d(1, 0, 0, 1);
    private Vector4d snapPointColor = new Vector4d(0, 1, 0, 1);
    private Vector4d firstPointColor = highlightPointColor;

    public DrawTool(Canvas canvas) {
        super(canvas);
    }

    /**
     * 描画中の図形を描画する責任を持つ
     */
    @Override
    public void draw() {
        if (current != null) {
            current.draw();

            // 描画中はわかりやすくするため頂点も描画
            ArrayList<Vector2d> vertices = current.getVertices();
            if (vertices.size() > 0) {
                Vector2d v = vertices.get(0);
                glPointSize(10.0f);
                glBegin(GL_POINTS);
                glColor4d(firstPointColor.x, firstPointColor.y, firstPointColor.z, firstPointColor.w);
                glVertex2d(v.x, v.y);
                glEnd();
            }

            glPointSize(5.0f);
            glColor4d(defaultPointColor.x, defaultPointColor.y, defaultPointColor.z, defaultPointColor.w);
            glBegin(GL_POINTS);
            current.getVertices().stream().skip(1).forEach((Vector2d v) -> {
                glVertex2d(v.x, v.y);
            });
            glEnd();
        }
    }

    public void setFill(Vector4d color) {
        if (current != null) {
            current.setFill(color);
        }
    }

    /**
     * マウスクリックしたときどうするかは図形に聞く
     */
    @Override
    public void onMouseClicked(MouseEvent event) {
        if (current != null) {
            current.onMouseClickDrawing(canvas, event);
        }
    }

    /**
     * マウス移動時も各図形に聞く
     */
    @Override
    public void onMouseMoved(MouseEvent event) {
        if (current != null) {
            current.onMouseMoveDrawing(canvas, event);

            ArrayList<Vector2d> vertices = current.getVertices();
            if (vertices.size() > 0 && event.getPos().distance(vertices.get(0)) < 10) {
                firstPointColor = snapPointColor;
            } else {
                firstPointColor = highlightPointColor;
            }
        }
    }

    public void setCurrentFigure(FigureComponent figure) {
        this.current = figure;
    }

    @Override
    public void onMouseClicked(MouseEvent event, boolean captureing) {
    }

    @Override
    public void onMouseMoved(MouseEvent event, boolean captureing) {
    }
}
