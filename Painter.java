/*
 *  ============================================================================================
 *  Painter.java : Painter interface
 *  ============================================================================================
 */
import java.awt.*;
interface Painter {
	/** draw a string
	*	@param text, x, y, width, heigth */
	public void drawString(String text, int x, int y, int width, int height);
	/** fill a rectangle
	*	@param x, y, width, heigth */
    public void fillRect(int x, int y, int width, int height);
	/** fill an oval
	*	@param x, y, width, heigth */
    public void fillOval(int x, int y, int width, int height);
	/** set the color
	*	@param color	the color object */
	public void setPaint(Color color);
	/** set the graphics element
	*	@param g	the graphics object */
	public void setGraphics(Graphics g);
	/** draw 4 corners if a shape is selected
	*	@param isSelected, x, y, width, heigth */
	public void drawHandles(boolean isSelected, int x, int y, int width, int height);
	public void drawRect(int x, int y, int width, int height);
	public void translate(int x, int y);
}