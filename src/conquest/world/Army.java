package conquest.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import conquest.building.Town;
import prof.jogos2D.image.*;
import prof.jogos2D.util.Vector2D;

/**
 *  Group of soldiers that leave a building
 */
public class Army {
	private Team team;                      // team the army belongs to
	// TODO support all building types
	private Town dest;                      // destination building
	
	private int nSoldiers;                  // number of soldiers    
	private double attack;                  // attack power
	private Point2D.Double pos;             // position
	private ComponenteVisual image;         // image of the soldier 
	private Vector2D dir;                   // movement direction 
	private double speed, speedOrig;        // current and original speed
	private boolean terminated = false;     // if it is terminated

	/** Constructs an Army
	 * @param t          the army team
	 * @param start      where it starts
	 * @param dest       where it is going to
	 * @param nSoldiers  number of soldiers in the army 
	 * @param attack     the attack power
	 * @param speed      the movement speed 
	 */
	public Army(Team t, Point start, Town dest, int nSoldiers, int attack, int speed ) {
		this.team = t;
		// TODO support all building types
		this.dest = dest; 				
		setAttack( attack );           
		this.nSoldiers = nSoldiers;

		// place the army in the correct spot
		this.pos = new Point2D.Double( start.x, start.y );

		// calculate the direction of the movement and setup its speed
		dir = new Vector2D( start, dest.getDoorLocation() );
		dir.normalizar();
		setSpeed( speed ); 
		speedOrig = speed;

		// uses aclone from the army image 
		ComponenteMultiAnimado img = (ComponenteMultiAnimado)team.getSoldier().clone();
		// Check if the soldier is facing left or right
		if( dir.x < 0 )
			img.setAnim( 1 );
		this.image = img;
	}

	/** update the army
	 * Each call to this methos is a processing cycle
	 */
	public void update() {
		// move the army
		pos.x += dir.x * speed;
		pos.y += dir.y * speed;

		// if it is placed closer than speed distance we consider it has arrived 
		if( dest.getDoorLocation().distance( pos ) < speed ){
			// is dest is from the same team its a population change, otherwise its an attack
			if( team == dest.getTeam() )
				enter();
			else
				attack( dest );				
		}
		// reset original speed
		speed = speedOrig;
	}

	/**
	 * attack a building
	 */
	private void attack( Town v ){
		// TODO support all building types
		// calculate the attack and defense values
		double totalDefense = v.getDefense();
		double totalAttack = attack*nSoldiers;
		double fight = totalDefense - totalAttack;
		if( fight > 0 ){
			// if fight is positive, then defense wins
	        // calculate survivors
			int popFinal = (int)(v.getPopulation() * (fight / totalDefense ));
			int decrementPop = v.getPopulation() - popFinal;  // dead population
			v.changePopulation( -decrementPop );		    
		}
		else {
			// the attackers won
			v.changePopulation( -v.getPopulation() );  // remove all previous population 
			v.setTeam( team );                         // the building now belongs to another team
            // calculate the surviving soldiers
			int popFinal = (int)(nSoldiers *(-fight / totalAttack));
			v.changePopulation( popFinal );            // they will be the new population
		}
		terminated = true; // the army has done its job and terminates
	}
	
	/** Attack another army
	 * @param a army to attack
	 */
	public void attack(Army a) {
		// calculate the attack power of both armies
		double totalAttackA = attack*nSoldiers;
		double totalAttackB = a.attack*a.nSoldiers;
		double fight = totalAttackA - totalAttackB;
		if( fight > 0 ){
			// we win
			nSoldiers = (int)(fight/attack); // remove the dead soldiers
			a.terminated = true;             // the other army is terminated
		}
		else {
			// they win
			a.nSoldiers = (int)(-fight/a.attack); // remove dead soldiers
			terminated = true;                    // we have been terminated
		}
	}

	/** army reaches destination and soldiers become population
	 */
	private void enter(){
		dest.changePopulation( nSoldiers );
		terminated = true;
	}
	
	/** draw the army
	 * @param g where to draw
	 */
	public void draw(Graphics g) {
		// draw the number of soldiers above the first soldier
		g.setColor( Color.BLACK );  // shadow		
		g.drawString( ""+nSoldiers, (int)(pos.x+16), (int)(pos.y-3) );
		g.setColor( Color.WHITE );		
		g.drawString( ""+nSoldiers, (int)(pos.x+15), (int)(pos.y-4) );
		// draw a soldier for each group of 5 soldiers
		int nDraws = nSoldiers / 5 + 1;
		for( int i = nDraws; i >= 0; i--){
			image.setPosicaoCentro( new Point( (int)(getPos().x-i*dir.x*15), (int)(getPos().y-i*dir.y*15)) );
			image.desenhar(g);
		}
	}

	/** Checks if the army is terminated and should be removed
	 * @return true if it is terminated 
	 */
	public boolean isTerminated() {
		return terminated;
	}
	
	/** return the current position of the army in the world
	 * @return the current position of the army in the world
	 */
	public Point2D.Double getPos() {
		return pos;
	}

	/** reduces the number of soldiers in the army
	 * @param kills number of dead soldiers 
	 */
	public void kill(int kills) {
		nSoldiers -= kills;
		if( nSoldiers <= 0)
			terminated = true;
	}

	/** Returns the team that owns the army
	 * @return the team that owns the army 
	 */
	public Team getTeam() {
		return team;
	}

	/** Returns the movement speed
	 * @return the movement speed
	 */
	public double getSpeed() {
		return speed;
	}
	
	/** defines the movement speed
	 * @param speed the new speed 
	 */
	public void setSpeed(double speed) {
		if( speed > Team.MAX_SPEED )
			this.speed = Team.MAX_SPEED;
		else if( speed < 0 )
			this.speed = 0;
		else
			this.speed = speed;
	}

	/** defines the attack value for the army
	 * @param a the new attack value
	 */
	public void setAttack(int a) {
		if( a > Team.MAX_POWER )
			this.attack = Team.MAX_POWER;
		else if( a < 0 )
			this.attack = 0;
		else
			this.attack = a;	} 
}
