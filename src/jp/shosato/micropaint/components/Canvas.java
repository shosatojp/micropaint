package jp.shosato.micropaint.components;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map.Entry;

import org.joml.Vector2d;
import org.joml.Vector4d;

import jp.shosato.micropaint.events.mouse.MouseClickEventListener;
import jp.shosato.micropaint.events.mouse.MouseEnterEventListener;
import jp.shosato.micropaint.events.mouse.MouseEvent;
import jp.shosato.micropaint.events.mouse.MouseEventListener;
import jp.shosato.micropaint.events.mouse.MouseLeaveEventListener;
import jp.shosato.micropaint.events.mouse.MouseMoveEventListener;
import jp.shosato.micropaint.tools.MoveTool;
import jp.shosato.micropaint.tools.Tool;
import jp.shosato.micropaint.utils.Colors;
import jp.shosato.micropaint.utils.Utility;

import static org.lwjgl.opengl.GL15.*;

/**
 * 描画する図形の親要素。ツールの有効・無効を管理する
 */
public class Canvas extends RectangleComponent
        implements MouseMoveEventListener, MouseClickEventListener, MouseEnterEventListener, MouseLeaveEventListener {
    /**
     * ツールの有効・無効化リスト
     */
    private HashMap<Tool, Boolean> tools = new HashMap<Tool, Boolean>();

    public Canvas(double w, double h) {
        this(new Vector2d(0, 0), w, h, Colors.GRAY);
    }

    public Canvas(double w, double h, Vector4d color) {
        this(new Vector2d(0, 0), w, h, color);
    }

    public Canvas(Vector2d translated, double w, double h, Vector4d color) {
        super(translated, w, h, color);
    }

    public void setTools(HashMap<Tool, Boolean> tools) {
        this.tools = tools;
    }

    /**
     * ビューポート座標を簡易的に計算
     */
    private Vector2d getViewportCoords() {
        Vector2d result = new Vector2d(this.translate);
        BasicComponent p = this;
        while (p.parent != null) {
            p = p.parent;
            result.add(p.translate);
        }
        return result;
    }

    @Override
    public void draw() {
        glPushMatrix();
        Utility.glTransform(dimension, translate, scale, rotate);
        {
            /**
             * フレームバッファに書き込む領域を制限
             * 図形がキャンバス外に行かないように
             */
            glEnable(GL_SCISSOR_TEST);
            Vector2d viewport = getViewportCoords();
            glScissor((int) viewport.x, (int) viewport.y, (int) dimension.x, (int) dimension.y);

            /* キャンバスを描画 */
            glColor3d(color.x, color.y, color.z);
            Utility.drawRectangleFill(dimension);

            /* 図形を描画 */
            for (BasicComponent child : children) {
                child.draw();
            }

            /* ツールを描画 */
            for (Entry<Tool, Boolean> e : tools.entrySet()) {
                if (e.getValue()) {
                    e.getKey().draw();
                }
            }

            glDisable(GL_SCISSOR_TEST);
        }
        glPopMatrix();
    }

    /**
     * 有効化されているツールを通過させる
     */
    @Override
    public void onMouseClicked(MouseEvent event) {
        for (Entry<Tool, Boolean> e : tools.entrySet()) {
            if (e.getValue() && e.getKey() instanceof MouseClickEventListener) {
                ((MouseClickEventListener) e.getKey()).onMouseClicked(event);
            }
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        for (Entry<Tool, Boolean> e : tools.entrySet()) {
            if (e.getValue() && e.getKey() instanceof MouseMoveEventListener) {
                ((MouseMoveEventListener) e.getKey()).onMouseMoved(event);
            }
        }
    }

    @Override
    public void onMouseEnter(MouseEvent event) {
        for (Entry<Tool, Boolean> e : tools.entrySet()) {
            if (e.getValue() && e.getKey() instanceof MouseEnterEventListener) {
                ((MouseEnterEventListener) e.getKey()).onMouseEnter(event);
            }
        }
    }

    @Override
    public void onMouseLeave(MouseEvent event) {
        for (Entry<Tool, Boolean> e : tools.entrySet()) {
            if (e.getValue() && e.getKey() instanceof MouseLeaveEventListener) {
                ((MouseLeaveEventListener) e.getKey()).onMouseLeave(event);
            }
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event, boolean captureing) {
    }

    @Override
    public void onMouseClicked(MouseEvent event, boolean captureing) {
    }

    @Override
    public void onMouseEnter(MouseEvent event, boolean captureing) {
    }

    @Override
    public void onMouseLeave(MouseEvent event, boolean captureing) {
    }
}