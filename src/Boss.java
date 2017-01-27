import java.awt.Color;
import java.awt.Point;

import uwcse.graphics.GWindow;
import uwcse.graphics.Line;
import uwcse.graphics.Oval;
import uwcse.graphics.Rectangle;
import uwcse.graphics.Shape;

/**
 * The representation and display of the boss
 */
public class Boss extends MovingObject {
	
	public static final int RADIUS = 20;
	// Check the boundary of its movement
	private boolean isMovingRight;
	// Set the health of the boss
	public static final int HEALTH = 100;
	
	// see the type of shooting
	private int type;
	// add on the damage that the boss receives
	private int injured;

	/**
	 * Create an boss in the graphics window
	 * 
	 * @param window
	 *            the GWindow this Boss belongs to
	 * @param center
	 *            the center Point of this Boss
	 */
	public Boss(GWindow window, Point center) {
		super(window, center);
	}

	/**
	 * Check to see how much damage the boss receives
	 */
	public void isShot() {
		injured += 10;
	}
	
	/**
	 * Check to see if the boss dies yet
	 */
	
	public boolean isDead(){
		return injured >= HEALTH;
	}
	
	/**
	 * How the boss shoot the space ship 	 
	 */
	public void shoot(SpaceShip spaceShip) {
		Rectangle s = spaceShip.getBoundingBox();
		if (center.x >= s.getX() && center.x <= s.getX() + s.getWidth()) {
			spaceShip.isShot();
		}
	}
	
	/**
	 * How the boss moves
	 */

	public void move() {
		// the boss move left and right in the range of the window
		if (isMovingRight) {
			if (this.center.x >= this.window.getWindowWidth()) {
				isMovingRight = false;
			} else {
				this.center.x += 10;
			}
		} else {
			if (this.center.x <= 0) {
				isMovingRight = true;
			} else {
				this.center.x -= 10;
			}
		}
		this.erase();
		this.draw();
	}

	protected void draw() {
		// Create the arrays to store the shape of the boss
		this.shapes = new Shape[8];

		this.shapes[0] = new Oval(this.center.x - RADIUS, this.center.y
				- RADIUS, 2 * RADIUS, 2 * RADIUS, Color.GREEN, true);
		this.shapes[1] = new Oval(this.center.x - RADIUS / 2, this.center.y
				- RADIUS / 2, RADIUS, RADIUS, Color.red, true);
		this.shapes[2] = new Line(this.center.x - (int) (1.2 * RADIUS),
				this.center.y - (int) (1.2 * RADIUS), this.center.x,
				this.center.y, Color.CYAN);
		this.shapes[3] = new Line(this.center.x + (int) (1.2 * RADIUS),
				this.center.y - (int) (1.2 * RADIUS), this.center.x,
				this.center.y, Color.CYAN);
		
		// Make the type randomly for the boss to shoot its laser beams
		type = (int) (Math.random() * 10) + 1;

		if (type == 1) {
			this.shapes[4] = new Line(this.center.x - RADIUS, this.center.y,
					this.center.x - RADIUS, this.window.getWindowHeight(),
					Color.RED);
			this.shapes[5] = new Line(this.center.x + RADIUS, this.center.y,
					this.center.x + RADIUS, this.window.getWindowHeight(),
					Color.BLUE);
		} else if (type == 2) {
			this.shapes[4] = new Line(this.center.x - RADIUS, this.center.y,
					this.center.x - RADIUS, this.window.getWindowHeight(),
					Color.RED);
		} else if (type == 3) {
			this.shapes[5] = new Line(this.center.x + RADIUS, this.center.y,
					this.center.x + RADIUS, this.window.getWindowHeight(),
					Color.BLUE);
		}
		
		// Create the health bar for the boss
		this.shapes[6] = new Rectangle(this.center.x - HEALTH / 2,
				this.center.y - (int)(1.5 * RADIUS), HEALTH, RADIUS / 2, Color.RED, false);
		this.shapes[7] = new Rectangle(this.center.x - HEALTH / 2,
				this.center.y - (int)(1.5 * RADIUS), HEALTH - injured, RADIUS / 2, Color.RED, true);
		for (int i = 0; i < this.shapes.length; i++) {
			if (this.shapes[i] != null) {
				this.window.add(this.shapes[i]);
			}
		}
		// The boss's bounding box
		this.boundingBox = new Rectangle(this.center.x - (int) (1.2 * RADIUS),
				this.center.y - (int) (1.2 * RADIUS), (int) (2.4 * RADIUS),
				(int) (2.4 * RADIUS));

		this.window.doRepaint();
	}
}
