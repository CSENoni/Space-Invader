/**
 * @author Hoang Minh Vo
 * @date 03/24/2015
 */
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import uwcse.graphics.GWindow;
import uwcse.graphics.GWindowEvent;
import uwcse.graphics.GWindowEventAdapter;
import uwcse.graphics.Oval;
import uwcse.graphics.Rectangle;
import uwcse.graphics.TextShape;
import uwcse.io.AudioPlayer;
import uwcse.io.Sound;

/**
 * A SpaceInvader displays a fleet of alien ships and a space ship. The player
 * directs the moves of the spaceship and can shoot at the aliens.
 */

public class SpaceInvader extends GWindowEventAdapter {
	// Possible actions from the keyboard
	/** No action */
	public static final int DO_NOTHING = 0;

	/** Steer the space ship */
	public static final int SET_SPACESHIP_DIRECTION = 1;

	/** To shoot at the aliens */
	public static final int SHOOT = 2;

	// Period of the animation (in ms)
	// (the smaller the value, the faster the animation)
	private int animationPeriod = 100;

	// Current action from the keyboard
	private int action;

	// Game window
	private GWindow window;

	// The space ship
	private SpaceShip spaceShip;

	private Boss boss;

	private boolean isBoss;
	// Direction of motion given by the player
	private int dirFromKeyboard = MovingObject.LEFT;

	// The aliens
	private ArrayList<Alien> aliens;

	private AudioPlayer player = new AudioPlayer();
	private Sound sound = new Sound("shoot.wav");

	/**
	 * Construct a space invader game
	 */
	public SpaceInvader() {
		this.window = new GWindow("Space invaders", 500, 500);
		this.window.setExitOnClose();
		this.window.addEventHandler(this); // this SpaceInvader handles all of
		// the events fired by the graphics
		// window

		// Display the game rules
		String rulesOfTheGame = "Save the Earth! Destroy all of the aliens ships.\n"
				+ "To move left, press 'j'.\n"
				+ "To move right, press 'l'.\n"
				+ "To move up , press 'i'.\n"
				+ "To move down, press 'k'.\n"
				+ "To shoot, press the space bar.\n" + "To quit, press 'Q'.";
		JOptionPane.showMessageDialog(null, rulesOfTheGame, "Space invaders",
				JOptionPane.INFORMATION_MESSAGE);
		this.initializeGame();
	}

	/**
	 * Initialize the game (draw the background, aliens, and space ship)
	 */
	private void initializeGame() {
		// Clear the window
		this.window.erase();

		// Background (starry universe)
		Rectangle background = new Rectangle(0, 0,
				this.window.getWindowWidth(), this.window.getWindowHeight(),
				Color.black, true);
		this.window.add(background);
		// Add 50 stars here and there (as small circles)
		Random rnd = new Random();
		for (int i = 0; i < 50; i++) {
			// Random radius between 1 and 3
			int radius = rnd.nextInt(3) + 1;
			// Random location (within the window)
			// Make sure that the full circle is visible in the window
			int x = rnd.nextInt(this.window.getWindowWidth() - 2 * radius);
			int y = rnd.nextInt(this.window.getWindowHeight() - 2 * radius);
			this.window.add(new Oval(x, y, 2 * radius, 2 * radius, Color.white,
					true));
		}

		// Moving instructions
		TextShape ts = new TextShape("j Left, l Right, i Up, k Down", 10, 10);
		ts.setColor(Color.WHITE);
		window.add(ts);

		// ArrayList of aliens
		this.aliens = new ArrayList<Alien>();

		// Create 12 aliens
		// Initial location of the aliens
		// (Make sure that the space ship can fire at them)
		int x = 5 * Alien.RADIUS;
		int y = 0;
		for (int i = 0; i < 12; i++) {
			y = (int) (Math.random() * (background.getHeight() / 2));
			this.aliens.add(new Alien(this.window, new Point(x, y)));
			x += background.getWidth() / 12;
		}

		// Create the space ship at the bottom of the window
		x = this.window.getWindowWidth() / 2;
		y = this.window.getWindowHeight() - SpaceShip.HEIGHT / 2;
		this.spaceShip = new SpaceShip(this.window, new Point(x, y));

		// start timer events
		this.window.startTimerEvents(this.animationPeriod);
	}

	/**
	 * Move the objects within the graphics window every time the timer fires an
	 * event
	 */
	public void timerExpired(GWindowEvent we) {
		// Perform the action requested by the user?
		switch (this.action) {
		case SpaceInvader.SET_SPACESHIP_DIRECTION:
			this.spaceShip.setDirection(this.dirFromKeyboard);
			break;
		case SpaceInvader.SHOOT:
			player.play(sound);
			this.spaceShip.shoot(this.aliens, this.boss);
			break;
		}
		// make the boss shoot if it is on the scene
		if (this.boss != null) {
			this.boss.shoot(this.spaceShip);
		}

		for (Alien a : aliens) {
			a.touch(this.spaceShip);
		}
		this.action = SpaceInvader.DO_NOTHING; // Don't do the same action
		// twice

		// Show the new locations of the objects
		this.updateGame();
	}

	/**
	 * Select the action requested by the pressed key
	 */
	public void keyPressed(GWindowEvent e) {
		// Don't perform the actions (such as shoot) directly in this method.
		// Do the actions in timerExpired, so that the alien ArrayList can't be
		// modified at the same time by two methods (keyPressed and timerExpired
		// run in different threads).

		switch (Character.toLowerCase(e.getKey())) // not case sensitive
		{
		// Put here the code to move the space ship with the j, k, l, i keys

		case ' ': // shoot at the aliens
			this.action = SpaceInvader.SHOOT;
			break;

		case 'j':
			this.action = SpaceInvader.SET_SPACESHIP_DIRECTION;
			this.dirFromKeyboard = SpaceShip.LEFT;
			break;

		case 'l':
			this.action = SpaceInvader.SET_SPACESHIP_DIRECTION;
			this.dirFromKeyboard = SpaceShip.RIGHT;
			break;

		case 'i':
			this.action = SpaceInvader.SET_SPACESHIP_DIRECTION;
			this.dirFromKeyboard = SpaceShip.UP;
			break;

		case 'k':
			this.action = SpaceInvader.SET_SPACESHIP_DIRECTION;
			this.dirFromKeyboard = SpaceShip.DOWN;
			break;

		case 'q': // quit the game (BlueJ might not like that one)
			System.exit(0);

		default: // no new action
			this.action = SpaceInvader.DO_NOTHING;
			break;
		}
	}

	/**
	 * Update the game (Move aliens + space ship)
	 */
	private void updateGame() {
		// Remove any dead alien
		for (int i = 0; i < aliens.size(); i++) {
			if (aliens.get(i).isDead()) {
				aliens.get(i).erase();
				aliens.remove(i);
			}
		}
		// Is the game won (or lost)?
		// Put here code to end the game (= no more aliens)
		// if there is a boss, show it
		if (aliens.size() == 0 && !isBoss) {
			int x = this.window.getWindowWidth() / 2;
			int y = 2 * Boss.RADIUS;
			this.boss = new Boss(this.window, new Point(x, y));
			isBoss = true;
		}
		
		// if the boss die or the space ship get destroyed
		// write code here to see what happen
		if (isBoss && boss.isDead()) {
			isBoss = false;
			boss.erase();
			window.stopTimerEvents();
			if (anotherGame("You saved the Earth. Congratulations!")) {
				initializeGame();
			} else {
				System.exit(0);
			}
		} else if (spaceShip.isDead()) {
			isBoss = false;
			spaceShip.erase();
			window.stopTimerEvents();
			if (anotherGame("Your planet has no hope.")) {
				initializeGame();
			} else {
				System.exit(0);
			}
		}

		this.window.suspendRepaints(); // to speed up the drawing

		// Move the aliens
		for (Alien a : aliens) {
			a.move();
		}

		// Move the space ship
		this.spaceShip.move();
		
		// Move the boss
		if (isBoss) {
			this.boss.move();
		}
		// Display it all
		this.window.resumeRepaints();
	}

	/**
	 * Does the player want to play again?
	 */
	public boolean anotherGame(String s) {
		// this method is useful at the end of a game if you want to prompt the
		// user
		// for another game (s would be a String describing the outcome of the
		// game
		// that just ended, e.g. "Congratulations, you saved the Earth!")
		int choice = JOptionPane.showConfirmDialog(null, s
				+ "\nDo you want to play again?", "Game over",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		return (choice == JOptionPane.YES_OPTION);
	}

	/**
	 * Starts the application
	 */
	public static void main(String[] args) {
		new SpaceInvader();
	}
}
