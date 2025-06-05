package Canvas;
import java.util.List;
import Shapes.*;

/**
 * 實作選取管理器
 * 負責管理形狀的選取狀態
 */
class ConcreteSelectionManager implements CanvasPanel.SelectionManager {

    @Override
    public void clearSelection(List<BaseShape> selectedShapes) {
        for (BaseShape shape : selectedShapes) {
            shape.setSelected(false);
        }
        selectedShapes.clear();
    }
}

/**
 * 實作形狀管理器
 * 將現有群組與其他物件重新組成新群組
 */
class ConcreteShapeManager implements CanvasPanel.ShapeManager {

    @Override
    public CompositeShape createGroup(List<BaseShape> selectedShapes, List<BaseShape> allShapes, int depth) {
        if (selectedShapes.size() < 2) {
            return null; // 至少需要兩個物件才能組成群組
        }

        CompositeShape group = new CompositeShape();
        group.setDepth(depth);

        // 處理所有選取的形狀，包括現有的群組
        for (BaseShape shape : selectedShapes) {
            if (allShapes.contains(shape)) {
                allShapes.remove(shape);
                // 如果是群組，直接加入新群組中
                group.addShape(shape);
            }
        }

        return group;
    }

    @Override
    public void ungroupShape(CompositeShape group, List<BaseShape> allShapes, List<BaseShape> selectedShapes) {
        allShapes.remove(group);

        List<BaseShape> childShapes = group.getShapes();
        for (BaseShape shape : childShapes) {
            allShapes.add(shape);
            shape.setSelected(true);
            selectedShapes.add(shape);
        }

        selectedShapes.remove(group);
    }

    /**
     * 深度解除群組
     * 遞迴地將所有巢狀群組展開為個別形狀
     */
    public void deepUngroupShape(CompositeShape group, List<BaseShape> allShapes, List<BaseShape> selectedShapes) {
        allShapes.remove(group);

        List<BaseShape> childShapes = group.getShapes();
        for (BaseShape shape : childShapes) {
            if (shape instanceof CompositeShape compositeShape) {
                // 遞迴展開子群組
                deepUngroupShape(compositeShape, allShapes, selectedShapes);
            } else {
                allShapes.add(shape);
                shape.setSelected(true);
                selectedShapes.add(shape);
            }
        }

        selectedShapes.remove(group);
    }
}