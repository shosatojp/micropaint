package jp.shosato.micropaint.components;

import org.joml.Vector2d;

import jp.shosato.micropaint.Window;
import jp.shosato.micropaint.controllers.Controller;
import jp.shosato.micropaint.utils.BoundingBox;

/**
 * GUIのルート要素
 */
public class RootComponent extends BasicComponent {

    public RootComponent(Window window, Controller controller) {
        this.dimension = new Vector2d(window.getWidth(), window.getHeight());
        this.window = window;
        this.controller = controller;
        this.parent = null;
    }

    /**
     * どんな座標も含まれているものとする
     */
    @Override
    public boolean contains(Vector2d pos) {
        return true;
    }

    @Override
    public BoundingBox getBB() {
        return null;
    }

    @Override
    public Vector2d getCenter() {
        return new Vector2d(dimension).div(2);
    }
}
