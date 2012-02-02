package ca.joshadam;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class A2P1Canvas extends View implements OnTouchListener {
	private final int MENU_HEIGHT = 50;
	private int CANVAS_WIDTH;
	List<Point> points = new ArrayList<Point>();
	List<PathSet> paths = new ArrayList<PathSet>();

	Paint paintBG = new Paint();
	Paint paintPoints = new Paint();
	Paint paintMenus = new Paint();
	Paint paintMenuShapes = new Paint();
	Paint paintRED = new Paint();
	Paint paintGREEN = new Paint();
	Paint paintBLUE = new Paint();

	boolean isFingerDown = false;
	boolean isFingerUp = false;

	// POSITIONS
	private final int X_LEFT = 0;
	private final int X_RIGHT = 1;
	private final int Y_TOP = 2;
	private final int Y_BOTTOM = 3;

	// Color and Shape
	private enum Shape {
		CIRCLE, SQUARE, TRIANGLE, LINE
	}

	private Shape shape;
	private int shapeColor;

	public A2P1Canvas(Context context) {
		super(context);
		setFocusable(true);

		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);

		setup();
	}

	private void setup() {
		// Basic shape setup
		shape = Shape.SQUARE;
		shapeColor = Color.RED;

		paintBG.setColor(Color.WHITE);
		paintBG.setAntiAlias(true);

		paintPoints.setColor(Color.BLACK);
		paintPoints.setAntiAlias(true);

		paintMenus.setColor(Color.LTGRAY);
		paintMenus.setAntiAlias(true);

		paintMenuShapes.setColor(Color.BLACK);
		paintMenuShapes.setAntiAlias(true);

		paintRED.setColor(Color.RED);
		paintRED.setAntiAlias(true);
		paintGREEN.setColor(Color.GREEN);
		paintGREEN.setAntiAlias(true);
		paintBLUE.setColor(Color.BLUE);
		paintBLUE.setAntiAlias(true);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP	&& event.getY() < (float) MENU_HEIGHT) {
			float x = event.getX();
			// Square hit
			if (x < 50) {
				shape = Shape.SQUARE;
			} else if (x < 100) {
				shape = Shape.CIRCLE;
			} else if (x < 150) {
				shape = Shape.TRIANGLE;
			}
			else if( x > CANVAS_WIDTH - 150 && x < CANVAS_WIDTH - 100 ){
				
				shapeColor = Color.RED;
			}
			else if( x > CANVAS_WIDTH - 100 && x < CANVAS_WIDTH - 50 ){
				shapeColor = Color.GREEN;
			}
			else if( x > CANVAS_WIDTH - 50 && x < CANVAS_WIDTH ){
				shapeColor = Color.BLUE;
			}
			points = new ArrayList<Point>();
			isFingerDown = false;
			isFingerUp = false;
		} else {
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				isFingerDown = true;
				Point point = new Point();
				point.x = (int) event.getX();
				point.y = (int) event.getY();
				points.add(point);
				break;
			case MotionEvent.ACTION_UP:
				isFingerDown = false;
				isFingerUp = true;
				break;
			}
			invalidate();
		}


		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		Canvas c = canvas;
		CANVAS_WIDTH = c.getWidth();
		// White Background
		c.drawRect(0, 0, c.getWidth(), c.getHeight(), paintBG);

		if (isFingerDown) {
			// Points
			for (Point point : points) {
				c.drawCircle(point.x, point.y, 5, paintPoints);
			}
		} else if (isFingerUp) {
			switch (shape) {
			case CIRCLE:
				paths.add( new PathSet(getBestCirclePath(), shapeColor ));
				break;
			case TRIANGLE:
				paths.add(new PathSet(getBestTrianglePath(), shapeColor));
				break;
			case SQUARE:
				paths.add(new PathSet(getBestSquarePath(), shapeColor));
				break;
			}

			points = new ArrayList<Point>();
			isFingerUp = false;
		}

		// Draw any existing shapes
		for (PathSet p : paths) {
			c.drawPath(p.path, p.paint);
		}

		// DRAW SHAPE MENU
		c.drawRect(0, 0, c.getWidth(), MENU_HEIGHT, paintMenus);
		c.drawRect(5, 5, 45, 45, paintMenuShapes);
		c.drawCircle(75, 25, (float) 22.5, paintMenuShapes);
		c.drawLine(155, 25, 195, 25, paintMenuShapes);
		// triangle is a little more complicated
		c.drawPath(
				getTrianglePath(new Point(125, 5), new Point(145, 45),
						new Point(105, 45)), paintMenuShapes);

		// DRAW COLOR MENU
		c.drawRect(c.getWidth() - 45, 5, c.getWidth() - 5, 45, paintBLUE);
		c.drawRect(c.getWidth() - 95, 5, c.getWidth() - 55, 45, paintGREEN);
		c.drawRect(c.getWidth() - 145, 5, c.getWidth() - 105, 45, paintRED);
	}

	private float[] getBestPoints() {
		float x_left = 20000, x_right = 0, y_top = 20000, y_bottom = 0;
		for (Point p : points) {
			x_left = p.x < x_left ? p.x : x_left;
			x_right = p.x > x_right ? p.x : x_right;
			y_top = p.y < y_top ? p.y : y_top;
			y_bottom = p.y > y_bottom ? p.y : y_bottom;
		}
		return new float[] { x_left, x_right, y_top, y_bottom };
	}

	private Path getBestTrianglePath() {
		float[] p = getBestPoints();
		return getTrianglePath(
				new Point((p[X_LEFT] + p[X_RIGHT]) / 2, p[Y_TOP]), new Point(
						p[X_RIGHT], p[Y_BOTTOM]), new Point(p[X_LEFT],
						p[Y_BOTTOM]));
	}

	private Path getBestCirclePath() {
		float[] p = getBestPoints();
		float x = (p[X_LEFT] + p[X_RIGHT]) / 2;
		float y = (p[Y_TOP] + p[Y_BOTTOM]) / 2;
		float r = ((p[X_RIGHT] - p[X_LEFT]) + (p[Y_BOTTOM] - p[Y_TOP])) / 2;
		return getCirclePath(x, y, r);
	}

	private Path getCirclePath(float x, float y, float r) {
		Path p = new Path();
		p.addCircle(x, y, r, Direction.CW);
		return p;
	}

	private Path getTrianglePath(Point one, Point two, Point three) {
		Path p = new Path();
		p.moveTo(one.x, one.y);
		p.lineTo(two.x, two.y);
		p.lineTo(three.x, three.y);
		p.close();
		return p;
	}

	private Path getBestSquarePath() {
		float[] f = getBestPoints();
		Path p = new Path();
		p.moveTo(f[X_LEFT], f[Y_TOP]);
		p.lineTo(f[X_RIGHT], f[Y_TOP]);
		p.lineTo(f[X_RIGHT], f[Y_BOTTOM]);
		p.lineTo(f[X_LEFT], f[Y_BOTTOM]);
		p.close();
		return p;
	}
}

class PathSet {
	Paint paint;
	Path path;

	public PathSet(Path p, int color ) {
		path = p;
		paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
	}
}

class Point {
	public Point() {
	};

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	float x, y;
}
