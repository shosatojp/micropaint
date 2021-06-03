package jp.shosato.draw.views;

import jp.shosato.draw.components.InputComponent;
import jp.shosato.draw.events.handlers.TextInputEvent;
import jp.shosato.draw.models.StrokeWidthModel;
import jp.shosato.draw.utils.SingleValueObserver;

public class StrokeWidthView extends InputComponent {
    public StrokeWidthView(StrokeWidthModel model) {
        super(100, 40);

        model.strokeWidthObservable.addObserver((Double stroke) -> {
            this.setText(String.valueOf(stroke));
        });

        this.onTextInput.addEventHandler((TextInputEvent event) -> {
            try {
                double width = Double.valueOf(event.getText());
                model.strokeWidthObservable.setValue(width);
            } catch (Exception e) {
                System.err.println(e);
            }
        });
    }
}
