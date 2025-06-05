package Canvas;
import java.awt.event.*;

/**
 * 滑鼠點擊事件監聽器
 * 負責處理滑鼠按下和釋放事件
 */
class CanvasMouseListener extends MouseAdapter {
    private final CanvasPanel canvasPanel;

    /**
     * 建構函數
     * @param canvasPanel 關聯的畫布面板
     */
    public CanvasMouseListener(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        canvasPanel.handleMousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvasPanel.handleMouseReleased(e);
    }
}

/**
 * 滑鼠移動事件監聽器
 * 負責處理滑鼠拖曳事件
 */
class CanvasMouseMotionListener extends MouseMotionAdapter {
    private final CanvasPanel canvasPanel;

    /**
     * 建構函數
     * @param canvasPanel 關聯的畫布面板
     */
    public CanvasMouseMotionListener(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        canvasPanel.handleMouseDragged(e);
    }
}