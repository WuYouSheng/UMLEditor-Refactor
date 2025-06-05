package Canvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import Shapes.*;

/**
 * 畫布面板類別
 * 使用命令模式處理各種操作
 * 使用觀察者模式處理形狀變化
 * 使用狀態模式管理不同的操作模式
 */
public class CanvasPanel extends JPanel {
    public enum Mode {
        SELECT, ASSOCIATION, GENERALIZATION, COMPOSITION, RECT, OVAL
    }

    private Mode currentMode = Mode.SELECT; // 當前模式
    private List<BaseShape> shapes = new ArrayList<>(); // 儲存所有形狀
    private List<BaseShape> selectedShapes = new ArrayList<>(); // 儲存被選取的形狀
    private Point startPoint; // 拖曳起始點
    private Point endPoint;// 拖曳的目的點
    private BaseShape currentShape; // 當前操作的形狀
    private ShapeFactory shapeFactory = new ShapeFactory(); // 形狀工廠
    private int nextDepth = 0; // 下一個形狀的深度值
    private int DeltaX = 0;
    private int DeltaY = 0;

    // 使用模式處理不同的操作
    private ModeHandler modeHandler;
    private SelectionManager selectionManager;
    private ShapeManager shapeManager;

    // 獨立的事件監聽器
    private CanvasMouseListener mouseListener;
    private CanvasMouseMotionListener mouseMotionListener;

    /**
     * 建構函數
     */
    public CanvasPanel() {
        initializePanel();
        initializeComponents();
        setupEventListeners();
    }

    /**
     * 初始化面板
     */
    private void initializePanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * 初始化組件
     */
    private void initializeComponents() {
        shapeFactory = new ShapeFactory();
        modeHandler = new ConcreteModeHandler();
        selectionManager = new ConcreteSelectionManager();
        shapeManager = new ConcreteShapeManager();

        // 初始化事件監聽器
        mouseListener = new CanvasMouseListener(this);
        mouseMotionListener = new CanvasMouseMotionListener(this);
    }

    /**
     * 設置事件監聽器
     */
    private void setupEventListeners() {
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
    }

    /**
     * 處理滑鼠拖曳事件
     */
    public void handleMouseDragged(MouseEvent e) {
        Point currentPoint = e.getPoint();
        modeHandler.handleDrag(currentMode, currentPoint, this);
        repaint();
    }

    /**
     * 處理滑鼠按下事件
     */
    public void handleMousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        modeHandler.handlePress(currentMode, startPoint, this);
        repaint();
    }

    /**
     * 處理滑鼠釋放事件
     */
    public void handleMouseReleased(MouseEvent e) {
        Point endPoint = e.getPoint();
        modeHandler.handleRelease(currentMode, endPoint, this);
        currentShape = null;
        repaint();
    }

    /**
     * 處理形狀移動時，更新所有相關連結的位置
     */
    public void updateLinksForShape(BaseShape shape) {
        LinkUpdater updater = new LinkUpdater();
        updater.updateLinksForShape(shape, shapes, DeltaX, DeltaY);
    }

    /**
     * 設定位移量DeltaX DeltaY
     */
    public void settingDeltaX_DeltaY(Point currentPoint){
        DeltaX = currentPoint.x - startPoint.x;
        DeltaY = currentPoint.y - startPoint.y;
    }

    /**
     * 設定當前模式
     */
    public void setMode(Mode mode) {
        this.currentMode = mode;
        clearSelection();
    }

    /**
     * 清除所有選取狀態
     */
    public void clearSelection() {
        selectionManager.clearSelection(selectedShapes);
        repaint();
    }

    /**
     * 清除畫布上的所有物件
     */
    public void clearAll() {
        shapes.clear();
        selectedShapes.clear();
        nextDepth = 0;
        repaint();
    }

    /**
     * 群組選取的物件
     */
    public void groupSelectedShapes() {
        if (selectedShapes.size() > 1) {
            CompositeShape group = shapeManager.createGroup(selectedShapes, shapes, nextDepth++);
            if (group != null) {
                shapes.add(group);
                selectedShapes.clear();
                selectedShapes.add(group);
                group.setSelected(true);
                repaint();
            }
        }
    }

    /**
     * 解除群組
     */
    public void ungroupSelectedShape() {
        ungroupSelectedShape(false); // 預設為淺層解除群組
    }

    /**
     * 解除群組
     * @param deepUngroup 是否深度解除群組（遞迴展開所有巢狀群組）
     */
    public void ungroupSelectedShape(boolean deepUngroup) {
        if (selectedShapes.size() == 1 && selectedShapes.getFirst() instanceof CompositeShape group) {

            if (deepUngroup && shapeManager instanceof ConcreteShapeManager) { //複合式Group
                ((ConcreteShapeManager) shapeManager).deepUngroupShape(group, shapes, selectedShapes);
            } else {
                shapeManager.ungroupShape(group, shapes, selectedShapes); //單層Group
            }
            repaint();
        }
    }

    /**
     * 刪除選取的形狀
     */
    public void deleteSelectedShapes() {
        ShapeDeleter deleter = new ShapeDeleter();
        deleter.deleteShapes(selectedShapes, shapes);
        selectedShapes.clear();
        repaint();
    }

    /**
     * 重命名選取的形狀
     */
    public void renameSelectedShape(String name) {
        if (!selectedShapes.isEmpty()) {
            BaseShape shape = selectedShapes.get(0);
            if (shape instanceof BasicShape) {
                ((BasicShape) shape).setName(name);
                repaint();
            }
        }
    }

    /**
     * 自定義標籤樣式
     */
    public void customizeLabelStyle(String name, String shape, Color color, int fontSize) {
        if (selectedShapes.size() == 1) {
            BaseShape baseShape = selectedShapes.get(0);
            if (baseShape instanceof BasicShape) {
                BasicShape shape1 = (BasicShape) baseShape;
                shape1.setName(name);
                shape1.setLabelShape(shape);
                shape1.setLabelColor(color);
                shape1.setFontSize(fontSize);
                repaint();
            }
        }
    }

    /**
     * 檢查是否有選取的形狀
     */
    public boolean hasSelectedShapes() {
        return !selectedShapes.isEmpty();
    }

    /**
     * 取得選取形狀的名稱
     */
    public String getSelectedShapeName() {
        if (!selectedShapes.isEmpty()) {
            BaseShape shape = selectedShapes.get(0);
            if (shape instanceof BasicShape) {
                return ((BasicShape) shape).getName();
            }
        }
        return "";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 繪製所有形狀
        for (BaseShape shape : shapes) {
            shape.draw(g2d);
        }

        // 繪製當前正在操作的形狀（如選取框）
        if (currentShape != null) {
            currentShape.draw(g2d);
        }
    }

    // Getter 方法
    public List<BaseShape> getShapes() { return shapes; }
    public List<BaseShape> getSelectedShapes() { return selectedShapes; }
    public Point getStartPoint() { return startPoint; }
    public void setStartPoint(Point startPoint) { this.startPoint = startPoint; }
    public BaseShape getCurrentShape() { return currentShape; }
    public void setCurrentShape(BaseShape currentShape) { this.currentShape = currentShape; }
    public ShapeFactory getShapeFactory() { return shapeFactory; }
    public int getNextDepth() { return nextDepth; }
    public void incrementNextDepth() { this.nextDepth++; }
    public int getDeltaX() { return DeltaX; }
    public int getDeltaY() { return DeltaY; }
    public SelectionManager getSelectionManager() { return selectionManager; }
    public ShapeManager getShapeManager() { return shapeManager; }

    /**
     * 模式處理interface
     * 使用模式處理不同操作
     */
    public interface ModeHandler {
        void handlePress(Mode mode, Point point, CanvasPanel canvas);
        void handleDrag(Mode mode, Point point, CanvasPanel canvas);
        void handleRelease(Mode mode, Point point, CanvasPanel canvas);
    }

    /**
     * 選取管理器 interface
     */
    public interface SelectionManager {
        void clearSelection(List<BaseShape> selectedShapes);
    }

    /**
     * 形狀管理器interface
     */
    public interface ShapeManager {
        CompositeShape createGroup(List<BaseShape> selectedShapes, List<BaseShape> allShapes, int depth);
        void ungroupShape(CompositeShape group, List<BaseShape> allShapes, List<BaseShape> selectedShapes);
    }
}