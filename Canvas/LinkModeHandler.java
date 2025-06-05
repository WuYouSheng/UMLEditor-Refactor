package Canvas;
import java.awt.*;
import java.util.List;
import Shapes.*;

/**
 * 連結模式處理器
 * 負責處理各種連結線的創建和操作
 */
class LinkModeHandler {

    /**
     * 處理滑鼠按下事件
     * 在基本形狀上開始創建連結
     */
    public void handlePress(CanvasPanel.Mode mode, Point point, CanvasPanel canvas) {
        List<BaseShape> shapes = canvas.getShapes();

        for (BaseShape shape : shapes) {
            if (shape instanceof BasicShape basicShape && shape.contains(point)) {
                Point port = basicShape.getNearestPort(point);
                if (port != null) {
                    BaseShape newLink = canvas.getShapeFactory().createLink(mode, port);
                    if (newLink instanceof Link link) {
                        link.setStartShape(basicShape);
                        newLink.setDepth(canvas.getNextDepth());
                        canvas.incrementNextDepth();
                        shapes.add(newLink);
                        canvas.setCurrentShape(newLink);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 處理滑鼠拖曳事件
     * 更新連結的終點位置
     */
    public void handleDrag(Point point, CanvasPanel canvas) {
        if (canvas.getCurrentShape() instanceof Link link) {
            link.setEndPoint(point);
        }
    }

    /**
     * 處理滑鼠釋放事件
     * 完成連結的創建，檢查是否有有效的終點
     */
    public void handleRelease(CanvasPanel.Mode mode, Point point, CanvasPanel canvas) {
        if (canvas.getCurrentShape() instanceof Link link) {
            boolean validEnd = false;
            List<BaseShape> shapes = canvas.getShapes();

            for (BaseShape shape : shapes) {
                if (shape instanceof BasicShape basicShape && shape.contains(point)) {
                    Point port = basicShape.getNearestPort(point);

                    if (port != null) {
                        link.setEndPoint(port);
                        link.setEndShape(basicShape);
                        validEnd = true;
                        break;
                    }
                }
            }

            // 如果沒有找到有效的終點，則移除這個連結
            if (!validEnd) {
                shapes.remove(canvas.getCurrentShape());
            }
        }
    }
}