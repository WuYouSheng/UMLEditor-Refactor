package Canvas;
import java.awt.*;
import Shapes.*;

/**
 * 實作模式處理器
 * 協調不同模式的處理邏輯
 */
class ConcreteModeHandler implements CanvasPanel.ModeHandler {
    private final SelectModeHandler selectHandler;
    private final ShapeModeHandler shapeHandler;
    private final LinkModeHandler linkHandler;

    /**
     * 建構函數
     * 初始化各種模式處理器
     */
    public ConcreteModeHandler() {
        this.selectHandler = new SelectModeHandler();
        this.shapeHandler = new ShapeModeHandler();
        this.linkHandler = new LinkModeHandler();
    }

    @Override
    public void handlePress(CanvasPanel.Mode mode, Point point, CanvasPanel canvas) {
        switch (mode) {
            case SELECT:
                selectHandler.handlePress(point, canvas);
                break;
            case RECT:
            case OVAL:
                shapeHandler.handlePress(mode, point, canvas);
                break;
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                linkHandler.handlePress(mode, point, canvas);
                break;
        }
    }

    @Override
    public void handleDrag(CanvasPanel.Mode mode, Point point, CanvasPanel canvas) {
        switch (mode) {
            case SELECT:
                selectHandler.handleDrag(point, canvas);
                break;
            case RECT:
            case OVAL:
                shapeHandler.handleDrag(point, canvas);
                break;
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                linkHandler.handleDrag(point, canvas);
                break;
        }
    }

    @Override
    public void handleRelease(CanvasPanel.Mode mode, Point point, CanvasPanel canvas) {
        switch (mode) {
            case SELECT:
                selectHandler.handleRelease(point, canvas);
                break;
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                linkHandler.handleRelease(mode, point, canvas);
                break;
        }
    }
}