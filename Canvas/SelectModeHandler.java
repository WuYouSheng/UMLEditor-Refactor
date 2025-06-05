package Canvas;
import java.awt.*;
import java.util.List;
import Shapes.*;

/**
 * 選取模式處理器
 * 支援群組物件的框選和重新群組
 */
class SelectModeHandler {

    /**
     * 處理滑鼠按下事件
     * 根據深度從上到下找到點擊的物件
     */
    public void handlePress(Point point, CanvasPanel canvas) {
        boolean found = false;
        canvas.getSelectionManager().clearSelection(canvas.getSelectedShapes());

        // 根據深度從上到下找到點擊的物件
        List<BaseShape> shapes = canvas.getShapes();
        List<BaseShape> selectedShapes = canvas.getSelectedShapes();

        for (int i = shapes.size() - 1; i >= 0; i--) {
            BaseShape shape = shapes.get(i);
            if (shape.contains(point)) {
                selectedShapes.add(shape);
                shape.setSelected(true);
                found = true;
                break;
            }
        }
    }

    /**
     * 處理滑鼠拖曳事件
     * 移動選取的物件或產生選取框
     */
    public void handleDrag(Point point, CanvasPanel canvas) {
        List<BaseShape> selectedShapes = canvas.getSelectedShapes();

        if (!selectedShapes.isEmpty()) {
            canvas.settingDeltaX_DeltaY(point);

            // 移動選取的物件
            for (BaseShape shape : selectedShapes) {
                shape.move(canvas.getDeltaX(), canvas.getDeltaY());
            }

            // 更新所有相關連結
            for (BaseShape shape : selectedShapes) {
                canvas.updateLinksForShape(shape);
            }

            canvas.setStartPoint(point);
        } else {
            // 產生選取框
            if (!(canvas.getCurrentShape() instanceof SelectionRectangle)) {
                canvas.setCurrentShape(new SelectionRectangle(canvas.getStartPoint(), point));
            } else {
                canvas.getCurrentShape().resize(canvas.getStartPoint(), point);
            }
        }
    }

    /**
     * 處理滑鼠釋放事件
     * 完成框選操作
     */
    public void handleRelease(Point point, CanvasPanel canvas) {
        if (canvas.getCurrentShape() instanceof SelectionRectangle) {
            Rectangle selectionRect = ((SelectionRectangle) canvas.getCurrentShape()).getRectangle();
            selectShapesInRectangle(selectionRect, canvas);
            canvas.setCurrentShape(null);
        }
    }

    /**
     * 框選矩形內的所有形狀
     */
    private void selectShapesInRectangle(Rectangle selectionRect, CanvasPanel canvas) {
        List<BaseShape> shapes = canvas.getShapes();
        List<BaseShape> selectedShapes = canvas.getSelectedShapes();

        for (BaseShape shape : shapes) {
            if (isShapeInSelectionArea(shape, selectionRect)) {
                shape.setSelected(true);
                selectedShapes.add(shape);
            }
        }
    }

    /**
     * 判斷形狀是否在選取區域內
     * 支援不同類型的形狀
     */
    private boolean isShapeInSelectionArea(BaseShape shape, Rectangle selectionRect) {
        if (shape instanceof BasicShape basicShape) {
            Rectangle shapeBounds = basicShape.getBounds();
            // 檢查形狀的邊界是否與選取矩形相交或包含
            return selectionRect.intersects(shapeBounds) || selectionRect.contains(shapeBounds);

        } else if (shape instanceof CompositeShape compositeShape) {
            Rectangle groupBounds = compositeShape.getBounds();
            // 對於群組，檢查群組的邊界
            return selectionRect.intersects(groupBounds) || selectionRect.contains(groupBounds);

        } else if (shape instanceof Link link) {
            Point startPoint = link.getStartPoint();
            Point endPoint = link.getEndPoint();
            // 對於連結，檢查起點和終點是否在選取區域內
            return selectionRect.contains(startPoint) || selectionRect.contains(endPoint) ||
                    selectionRect.intersectsLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }

        return false;
    }
}