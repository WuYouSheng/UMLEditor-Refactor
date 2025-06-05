package Canvas;
import java.util.ArrayList;
import java.util.List;
import Shapes.*;

/**
 * 連結更新器
 * 負責更新形狀移動時相關連結的位置
 */
class LinkUpdater {

    /**
     * 更新與指定形狀相關的所有連結位置
     */
    public void updateLinksForShape(BaseShape shape, List<BaseShape> allShapes, int deltaX, int deltaY) {
        // 收集所有關聯到此形狀的連結
        List<Link> relatedLinks = new ArrayList<>();

        for (BaseShape s : allShapes) {
            if (s instanceof Link link) {
                // 檢查此連結是否關聯到此形狀或其子形狀
                boolean isRelated = false;

                if (shape instanceof BasicShape) {
                    if (link.isRelatedTo(shape)) {
                        isRelated = true;
                    }
                } else if (shape instanceof CompositeShape composite) {
                    if (composite.isRelatedToLink(link)) {
                        isRelated = true;
                    }
                }

                if (isRelated) {
                    relatedLinks.add(link);
                }
            }
        }

        // 更新所有相關連結的位置
        for (Link link : relatedLinks) {
            if (shape instanceof BasicShape) {
                // 使用修正後的方法更新連接點
                link.updateEndpointForShape(shape, deltaX, deltaY);
            } else {
                // 如果是群組形狀，檢查每個子形狀
                CompositeShape composite = (CompositeShape) shape;
                List<BaseShape> childShapes = composite.getShapes();

                for (BaseShape childShape : childShapes) {
                    if (childShape instanceof BasicShape) {
                        // 對群組中的每個基本形狀，使用修正後的方法更新連接點
                        link.updateEndpointForShape(childShape, deltaX, deltaY);
                    }
                }
            }
        }

        // 移動完成後，確保所有連接點都是最新的
        for (Link link : relatedLinks) {
            link.updatePosition();
        }
    }
}

/**
 * 形狀刪除器
 * 負責刪除形狀及其相關連結
 */
class ShapeDeleter {

    /**
     * 刪除指定的形狀列表及其相關連結
     */
    public void deleteShapes(List<BaseShape> shapesToDelete, List<BaseShape> allShapes) {
        // 創建一個臨時列表，避免並發修改異常
        List<BaseShape> shapesToRemove = new ArrayList<>(shapesToDelete);
        List<BaseShape> allToRemove = new ArrayList<>();

        // 對於每個要刪除的形狀
        for (BaseShape shape : shapesToRemove) {
            // 如果是群組，遞迴收集所有子形狀
            if (shape instanceof CompositeShape compositeShape) {
                collectAllShapesInGroup(compositeShape, allToRemove);
            }

            // 添加當前形狀到刪除列表
            allToRemove.add(shape);
        }

        // 收集所有需要刪除的連結線
        List<BaseShape> linksToRemove = new ArrayList<>();
        for (BaseShape s : allShapes) {
            if (s instanceof Link link) {
                // 檢查連結是否與任何要刪除的形狀相關
                for (BaseShape shapeToRemove : allToRemove) {
                    if (shapeToRemove instanceof BasicShape && link.isRelatedTo(shapeToRemove)) {
                        linksToRemove.add(link);
                        break;
                    } else if (shapeToRemove instanceof CompositeShape compositeShape &&
                            compositeShape.isRelatedToLink(link)) {
                        linksToRemove.add(link);
                        break;
                    }
                }
            }
        }

        // 從畫布上移除所有標記的形狀和連結
        allShapes.removeAll(allToRemove);
        allShapes.removeAll(linksToRemove);
    }

    /**
     * 遞迴收集群組中的所有形狀
     */
    private void collectAllShapesInGroup(CompositeShape group, List<BaseShape> collector) {
        for (BaseShape shape : group.getShapes()) {
            if (shape instanceof CompositeShape compositeShape) {
                // 遞迴處理子群組
                collectAllShapesInGroup(compositeShape, collector);
            }
            collector.add(shape);
        }
    }
}