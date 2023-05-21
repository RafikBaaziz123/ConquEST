package conquest.world;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import prof.jogos2D.image.ComponenteVisual;
import prof.jogos2D.util.Vector2D;

/**
 * Represents a projectile fired from a building
 */
public class Projectile {
	// we assume that speed is constant 
	private static final int SPEED = 12;
	
	private ComponenteVisual image;     // projectile image
	private int killPower;              // how many soldier it kills
	private boolean terminated = false; // if it is terminated (miss ot hit)
	private Army target;                // army that is the target
	private Point2D.Double center;      // position of the projectile center
	
	/**
	 * Creates a projectile
	 * @param image image used to draw the projectile
	 * @param power destructive power (how many soldiers it kills)
	 * @param target the army it aims 
	 */
	public Projectile(ComponenteVisual image, int power, Army target) {
		this.image = image;
		this.killPower = power;
		this.target = target;
		Point c = image.getPosicaoCentro();
		center = new Point2D.Double( c.x, c.y );
	}

	/** Updates the projetile
	 * moves and checks if it hits the army
	 */
	public void update(){
		// sets the direction it must take to hit the target
		Vector2D dir = new Vector2D( center, target.getPos() );
		dir.normalizar();
		// move
		center.x += dir.x * SPEED;
		center.y += dir.y * SPEED;
		image.setPosicaoCentro( new Point((int)center.x, (int)center.y) );
		
		// if it is below speed distance from the army then it's a hit
		if( center.distance( target.getPos() ) < SPEED ){
			target.kill( killPower );
			terminated = true;
		}
	}

	/** draws the projcetile
	 * @param g where to draw 
	 */
	public void draw( Graphics g ){
		image.desenhar( g );
	}
	
	/** Returns the target
	 * @return the target
	 */
	public Army getTarget() {
		return target;
	}
	
	/** Checks if the projectile has done its job and is terminated
	 * @return true if it has hit the target
	 */
	public boolean isTerminated() {
		return terminated;
	}
}
