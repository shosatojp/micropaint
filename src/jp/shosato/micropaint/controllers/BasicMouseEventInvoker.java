package jp.shosato.micropaint.controllers;

import java.lang.annotation.Target;
import java.util.Iterator;

import org.joml.Vector2d;

import jp.shosato.micropaint.components.BasicComponent;
import jp.shosato.micropaint.events.mouse.MouseClickEventListener;
import jp.shosato.micropaint.events.mouse.MouseEvent;
import jp.shosato.micropaint.events.mouse.MouseEventListener;
import jp.shosato.micropaint.utils.Runnable;
import jp.shosato.micropaint.utils.Utility;

/**
 * マウス関連イベントを発行する処理を共通化
 */
public class BasicMouseEventInvoker {
    /**
     * イベント発行条件
     */
    public interface Condition {
        public boolean run(BasicComponent component, Vector2d relPos);
    }

    /**
     * イベント発行
     */
    public interface Invoker {
        public void run(BasicComponent component, MouseEvent event, boolean captureing);
    }

    /**
     * イベント発行前の処理
     */
    public interface Modifier {
        public void run(BasicComponent component);
    }

    /**
     * ターゲットに対する処理
     */
    public interface TargetRunnable {
        public void run(BasicComponent component);
    }

    private final Condition cond;
    private final Class cls;
    private final Invoker invoker;
    private final boolean mustContain;
    private final Modifier modifier;
    private final TargetRunnable targetRunnable;

    public BasicMouseEventInvoker(Class cls, Invoker invoker) {
        this(cls, invoker, (BasicComponent component, Vector2d relPos) -> true, true, null);
    }

    public BasicMouseEventInvoker(Class cls, Invoker invoker, TargetRunnable targetRunnable) {
        this(cls, invoker, (BasicComponent component, Vector2d relPos) -> true, true, null, targetRunnable);
    }

    public BasicMouseEventInvoker(Class cls, Invoker invoker, Condition cond, boolean mustContain, Modifier modifier) {
        this(cls, invoker, cond, mustContain, modifier, null);
    }

    public BasicMouseEventInvoker(Class cls, Invoker invoker, Condition cond, boolean mustContain, Modifier modifier,
            TargetRunnable targetRunnable) {
        this.cls = cls;
        this.cond = cond;
        this.invoker = invoker;
        this.mustContain = mustContain;
        this.modifier = modifier;
        this.targetRunnable = targetRunnable;
    }

    public void invoke(BasicComponent component, MouseEvent event, Vector2d relPos) {
        /**
         * キャプチャリングフェーズ
         */
        if (cls.isInstance(component) && !event.cancelled() && cond.run(component, relPos)) {
            if (modifier != null)
                modifier.run(component);
            event.setCurrentTarget(component);
            event.setTarget(component);
            event.setPos(relPos);
            invoker.run(component, event, true);
        }

        /**
         * イベント再帰
         */
        /* 座標逆変換 */
        Vector2d untransformed = Utility.untransform(relPos, component.getCenter(), component.translate,
                component.scale, component.rotate);
        /* 描画順と逆に */
        for (Iterator<BasicComponent> iter = component.getChildren().descendingIterator(); iter.hasNext();) {
            BasicComponent child = iter.next();
            if ((!mustContain || child.contains(untransformed)) && !event.cancelled()) {
                event.setPos(untransformed);
                /* 再帰呼び出し */
                invoke(child, event, untransformed);
            }
        }

        /**
         * バブリングフェーズ
         */
        if (event.getTarget() != null && cls.isInstance(component) && !event.cancelled()) {
            if (modifier != null)
                modifier.run(component);
            event.setCurrentTarget(component);
            event.setPos(relPos);
            invoker.run(component, event, false);
        }

        /**
         * ターゲット要素に対する処理
         */
        if (targetRunnable != null && event.getTarget() == component) {
            targetRunnable.run(component);
        }
    }
}
