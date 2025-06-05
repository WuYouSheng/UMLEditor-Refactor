package Canvas;
import java.awt.*;
import Shapes.*;

/**
 * 形狀模式處理器
 * 負責處理矩形和橢圓形狀的創建和操作
 */
class ShapeModeHandler {

    /**
     * 處理滑鼠按下事件
     * 創建新的形狀並添加到畫布中
     */
    public void handlePress(CanvasPanel.Mode mode, Point point, CanvasPanel canvas) {
        BaseShape newShape = canvas.getShapeFactory().createShape(mode, point);
        if (newShape != null) {
            newShape.setDepth(canvas.getNextDepth());
            canvas.incrementNextDepth();
            canvas.getShapes().add(newShape);
            canvas.setCurrentShape(newShape);
        }
    }

    /**
     * 處理滑鼠拖曳事件
     * 移動當前正在創建的形狀
     */
    public void handleDrag(Point point, CanvasPanel canvas) {
        if (canvas.getCurrentShape() != null) {
            canvas.settingDeltaX_DeltaY(point);
            canvas.getCurrentShape().move(canvas.getDeltaX(), canvas.getDeltaY());
            canvas.setStartPoint(point);
        }
    }
}