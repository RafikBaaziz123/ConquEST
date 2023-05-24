package conquest.world;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import conquest.building.*;
import prof.jogos2D.image.*;

/**
 * This class combines all the elements in the game,
 * that are present in a level
 */
public class World {

	private ComponenteVisual backgroundImage;  
	// TODO support all building types
	// lists with all elements of the game
	private ArrayList<Building> buildings = new ArrayList<Building>();

	
	private ArrayList<Army> armies = new ArrayList<Army>();
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
		
	/** World constructor
	 * @param img background image
	 */
	public World( ComponenteVisual img ){
		backgroundImage = img;
	}
	
	/** Draws all game elements in the world
	 * @param g whwre to draw
	 */
	public synchronized void draw( Graphics2D g ){
		backgroundImage.desenhar( g );
		
		// TODO support all building types
		for( Building v : buildings )
			v.draw( g );
		
		for( Army b : armies )
			b.draw( g );
		
		for( Projectile b : projectiles )
			b.draw( g );
	}

	/**
	 * Updates all world elements and removes the ones
	 * already terminated
	 * Each call to this method is a processing cycle
	 */
	public synchronized void update(){
		for( Army b : armies )
			b.update();
		
		// check if armies battle each other
		for( int i=0; i < armies.size()-1; i++ ) {
			for( int k = i+1; k < armies.size(); k++ ) {
				Army a = armies.get( i );
				Army b = armies.get( k );
				// if their are on the same team, ignore
				if( a.getTeam() == b.getTeam() )
					continue;
				// if they are within attack range (5 pixels) there is a fight
				if( a.getPos().distance( b.getPos() ) < 5 )
					a.attack( b );
			}
		}
			
		for( Projectile b : projectiles )
			b.update();
			
		// TODO support all building types
		for( Building v : buildings )
			v.update();
		

		// remove terminated armies
		for( int i = armies.size()-1; i >= 0; i-- ){
			if( armies.get(i).isTerminated() )
				armies.remove( i );
		}
		
		// remove terminated projectiles
		for( int i = projectiles.size()-1; i >= 0; i-- ){
			if( projectiles.get(i).isTerminated() )
				projectiles.remove( i );
		}
	}

	/** Adds a town to the world
	 * @param t town to add
	 */
	public synchronized void addBuilding(Building t ){
		// TODO support all building types
		buildings.add( t );
	}
	
	/** Adds a farm to the world
	 * @param f farm to the world
	 */

	
	/** Removes a town from the world
	 * @param t town to remove
	 */
	public synchronized void remove( Building t ){
		// TODO support all building types
		buildings.remove( t );
	}
	
	/** returns all towns present in this world
	 * @return all towns present in the world
	 */
	public List<Building> getBuilding() {		
		// TODO support all building types
		return Collections.unmodifiableList( buildings );
	}

	/** returns all farms present in this world
	 * @return all farms present in this world
	 */
	
	/** Test if there is a town in a given point
	 * @param pt point where to search
	 * @return the town that contains point pt, or null if there is none
	 */
	public Building getTownAt(Point pt) {
		// TODO support all building types
		for( Building v : buildings )
			if( v.isInside( pt ) )
				return v;
		return null;
	}
	
	/** Test if there is a town in a given point
	 * @param pt point where to search
	 * @return the farm that contains point pt, or null if there is none
	 */

	/** Adds an army
	 * @param a army to add
	 */
	public synchronized void addArmy(Army a) {
		armies.add( a );
	}
	
	/** removes an army
	 * @param a army to remove
	 */
	public synchronized void removeArmy(Army a) {
		armies.remove( a );
	}

	/** returns all armies in this world
	 * @return all armies in this world
	 */
	public List<Army> getArmies() {
		return Collections.unmodifiableList( armies );
	}
	
	/** adds a projectile
	 * @param p projectile to add
	 */
	public synchronized void addProjectile(Projectile p) {
		projectiles.add( p );
	}
	
	/** removes a projectile
	 * @param p o projectile to remove
	 */
	public synchronized void removeProjectile( Projectile p ) {
		projectiles.remove( p );
	}
	
	/** Returns all projectiles
	 * @return all projectiles
	 */
	public List<Projectile> getProjectiles(){
		return Collections.unmodifiableList( projectiles );
	}
}
