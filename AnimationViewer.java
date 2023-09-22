/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

class AnimationViewer extends JComponent implements Runnable, TreeModel{
	private Thread animationThread = null;    // the thread for animation
    private static int DELAY = 30;         // the current animation speed
    private Painter painter = new GraphicsPainter();;
    private ShapeType currentShapeType=Shape.DEFAULT_SHAPETYPE; // the current shape type,
    private PathType currentPathType=Shape.DEFAULT_PATHTYPE;  // the current path type
    private Color currentColor=Shape.DEFAULT_COLOR; // the current fill colour of a shape
    private int marginWidth=Shape.DEFAULT_MARGIN_WIDTH, marginHeight = Shape.DEFAULT_MARGIN_HEIGHT, currentWidth=Shape.DEFAULT_WIDTH, currentHeight=Shape.DEFAULT_HEIGHT;
	private String currentText=Shape.DEFAULT_TEXT;
	private NestedShape root = new NestedShape(marginWidth, marginHeight);
	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

    public AnimationViewer(boolean isGraphicsVersion) {
		start();
		addMouseListener(new MyMouseAdapter());
    }
    class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked( MouseEvent e ) {
			boolean found = false;
			for (Shape currentShape: root.getAllInnerShapes())
				if ( currentShape.contains( e.getPoint()) ) {
					currentShape.setSelected( ! currentShape.isSelected() );
					found = true;
				}
			if (!found) {
				root.createInnerShape(e.getX(), e.getY(), currentWidth, currentHeight, currentColor, PathType.BOUNCE, currentText, currentShapeType);
				insertNodeInto(root.getInnerShapeAt(root.getSize()-1), root);
			}
		}
	}
    /**    move and paint all shapes within the animation area
     * @param g    the Graphics control */
	public void paintComponent(Graphics g) {
		painter.setGraphics(g);
		super.paintComponent(g);
		for (Shape currentShape: root.getAllInnerShapes()) {
			currentShape.move();
			currentShape.draw(painter);
			currentShape.drawHandles(painter);
			currentShape.drawString(painter);
		}
    }
	/** set the current height and the height for all currently selected shapes
	 * @param h	the new height value */
	public void setCurrentHeight(int h) {
		currentHeight = h;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setHeight(currentHeight);
	}
	/** set the current width and the width for all currently selected shapes
	 * @param w	the new width value */
	public void setCurrentWidth(int w) {
		currentWidth = w;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setWidth(currentWidth);
	}
	/** set the current color and the color for all currently selected shapes
	 * @param bc	the new color value */
	public void setCurrentColor(Color bc) {
		currentColor = bc;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setColor(currentColor);
	}
	/** set the current text and the text for all currently selected shapes
	 * @param text	the new text value */
	public void setCurrentText(String text) {
		currentText = text;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setText(currentText);
	}
 	/** reset the margin size of all shapes from our ArrayList */
    public void resetMarginSize() {
        marginWidth = getWidth();
        marginHeight = getHeight() ;
        for (Shape currentShape: root.getAllInnerShapes())
			currentShape.setMarginSize(marginWidth,marginHeight );
    }
	/** get the current width
	 * @return currentWidth - the width value */
	public int getCurrentWidth() { return currentWidth; }
	/** get the current height
	 * @return currentHeight - the height value */
	public int getCurrentHeight() { return currentHeight; }
	/** get the current fill colour
	 * @return currentColor - the fill colour value */
	public Color getCurrentColor() { return currentColor; }
	/** get the current shape type
	 * @return currentShapeType - the shape type */
    public ShapeType getCurrentShapeType() { return currentShapeType; }
	/** set the current shape type
	 * @param st the new shape type */
    public void setCurrentShapeType(int st) {
		currentShapeType = ShapeType.getShapeType(st);
	}
	/** get the current path type
	 * @return currentPathType - the path type */
	public PathType getCurrentPathType() { return currentPathType; }
	/** set the current path type
	 * @param pt the new path type */
	public void setCurrentPathType(int pt) {
		currentPathType = PathType.getPathType(pt);
	}
	/** get the current text
	 * @return currentText - the text value */
	public String getCurrentText() { return currentText; }

	public NestedShape getRoot() {return root;}

	public boolean isLeaf(Object node){
		boolean leaf = true;
		if (node instanceof NestedShape){
			leaf = false;
		}
		return leaf;
	}

	public boolean isRoot(Shape selectedNode){
		boolean r = false;
		if(selectedNode == root){
			r = true;
		}
		return r;
	}

	public Object getChild(Object parent, int index){
		if (index < 0 || parent.getClass().getName() != "NestedShape"){
			return null;
		}else{
			return ((NestedShape) parent).getInnerShapeAt(index);
		}
	}

	public int getChildCount(Object parent){
		if (parent.getClass().getName() != "NestedShape"){
			return 0;
		}else{
			return ((NestedShape) parent).getSize();
		}
	}

	public int getIndexOfChild(Object parent, Object child){
		if (parent.getClass().getName() != "NestedShape" && child.getClass().getName() != "NestedShape"){
			return -1;
		}else{
			return ((NestedShape) parent).indexOf((Shape)child);
		}
	}

	public void	addTreeModelListener(final TreeModelListener tml){
		treeModelListeners.add(tml);
	}

	public void	removeTreeModelListener(final TreeModelListener tml){
		treeModelListeners.remove(tml);
	}

	public void valueForPathChanged(TreePath path, Object newValue){}

	public void fireTreeNodesInserted(Object source, Object[] path,int[] childIndices,Object[] children){
			TreeModelEvent treeModelEvent = new TreeModelEvent(source, path, childIndices, children);
			for (TreeModelListener listener : treeModelListeners) {
				listener.treeNodesInserted(treeModelEvent);
			}
		}
	
	public void insertNodeInto(Shape newChild, NestedShape parent){
		fireTreeNodesInserted(this, parent.getPath(), new int[]{parent.indexOf(newChild)}, new Object[] {newChild});
	}

	public void addShapeNode(NestedShape selectedNode){
		if (isRoot(selectedNode)){
			selectedNode.createInnerShape(0, 0, currentWidth, currentHeight, currentColor, currentPathType, currentText, currentShapeType);
		}else{
			selectedNode.createInnerShape(0, 0, currentWidth/2, currentHeight/2, currentColor, currentPathType, currentText, currentShapeType);
		}
		insertNodeInto(selectedNode.getInnerShapeAt(selectedNode.getSize()-1), selectedNode);
	}

	public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children){
		TreeModelEvent treeModelEvent = new TreeModelEvent(source, path, childIndices, children);
		for (TreeModelListener listener : treeModelListeners) {
			listener.treeNodesRemoved(treeModelEvent);
		}
	}

	public void removeNodeFromParent(Shape selectedNode){
		NestedShape parent = selectedNode.getParent();
		int index = parent.indexOf(selectedNode);
		parent.remove(selectedNode);
		fireTreeNodesRemoved(this, parent.getPath(), new int[]{index}, new Object[] {selectedNode});
	}

// you don't need to make any changes after this line ______________
	public void start() {
        animationThread = new Thread(this);
        animationThread.start();
    }
    public void stop() {
        if (animationThread != null) {
            animationThread = null;
        }
    }
    public void run() {
        Thread myThread = Thread.currentThread();
        while(animationThread==myThread) {
            repaint();
            pause(DELAY);
        }
    }
    private void pause(int milliseconds) {
        try {
            Thread.sleep((long)milliseconds);
        } catch(InterruptedException ie) {}
    }
}


