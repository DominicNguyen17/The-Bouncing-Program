/*
 *	===============================================================================
 *	NestedShape.java : A shape that is an nested shape.
 *	=============================================================================== */
import java.awt.Color;
import java.util.*;
class NestedShape extends RectangleShape{
    private ArrayList<Shape> innerShapes;
    public NestedShape(){
        this.innerShapes = new ArrayList<Shape>();
        createInnerShape(0, 0, (int)(width/2.0), (int)(height/2.0), color, PathType.BOUNCE, text, ShapeType.RECTANGLE);
    }

    public NestedShape(int x, int y, int w, int h, int mw, int mh, Color c, PathType pt, String t){
        super(x, y, w, h, mw, mh,c,pt,t);
        this.innerShapes = new ArrayList<Shape>();
        createInnerShape(0, 0, (int)(w/2.0), (int)(h/2.0), c, PathType.BOUNCE, t, ShapeType.RECTANGLE);
    }

    public NestedShape(int w, int h){
        super(0,0,w,h,DEFAULT_MARGIN_WIDTH,DEFAULT_MARGIN_HEIGHT,Color.black,PathType.BOUNCE, "");
        this.innerShapes = new ArrayList<Shape>();
    }

    public Shape createInnerShape(int x, int y, int w, int h, Color c, PathType pt,String text, ShapeType st){
        if (st == ShapeType.RECTANGLE){
            RectangleShape inner = new RectangleShape(x, y, w, h, width, height, c, pt, text);
            innerShapes.add(inner);
            inner.parent = this;
        }else if (st == ShapeType.OVAL){
            OvalShape inner = new OvalShape(x, y, w, h, width, height, c, pt, text);
            innerShapes.add(inner);
            inner.parent = this;
        }else if (st == ShapeType.NESTED){
            NestedShape inner = new NestedShape(x, y, w, h, width, height, c, pt, text);
            innerShapes.add(inner);
            inner.parent = this;
        }
        return innerShapes.get(innerShapes.size()-1);
    }

    public Shape getInnerShapeAt(int index){return innerShapes.get(index);}

    public int getSize(){return innerShapes.size();}

    public int indexOf(Shape s){return innerShapes.indexOf(s);}

    public void add(Shape s){
        innerShapes.add(s);
        s.setParent(this);
    }
    public void remove(Shape s){
        innerShapes.remove(s);
        s.setParent(null);
    }
    public ArrayList<Shape> getAllInnerShapes(){return innerShapes;}

    public void setWidth(int w){
        this.width = w;
        for(Shape s : innerShapes){
            s.marginWidth = w;
        }
    }

    public void setHeight(int h){
        this.height = h;
        for(Shape s : innerShapes){
            s.marginHeight = h;
        }
    }

    public void setColor(Color c){
        this.color = c;
        for(Shape s : innerShapes){
            s.color = c;
        }
    }

    public void setText(String t){
        this.text = t;
        for(Shape s : innerShapes){
            s.text = t;
        }
    }

    public void draw(Painter painter){
		painter.setPaint(Color.BLACK);
		painter.drawRect(x, y, width, height);
        painter.translate(x, y);
        for(Shape s : innerShapes){
            s.draw(painter);
        }
        painter.translate(-x, -y);
	}

    public void move(){
        this.path.move();
        for(Shape s : innerShapes){
            s.move();
        }
    }
}