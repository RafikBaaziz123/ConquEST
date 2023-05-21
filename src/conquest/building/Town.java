package conquest.building;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;

import conquest.world.Army;
import conquest.world.Team;
import conquest.world.World;
import conquest.world.Projectile;
import prof.jogos2D.image.*;

public class Town {
	private int population;            // current population
	private int maxPop;                // maximum population allowed (without team extras)
	private ComponenteVisual image;   
	private int nextRegen;             // when is the nest regeneration cycle
	private Team team;                 // the farm's team  
	private World world;               // world where it is placed
	private int foodOutput;            // how much food it produces (not counting production extras)

	private int nextShot = 30;         // countdown to the next shot
	private int rangeSq = 140*140;     // range squared to ease the calculations
	private ComponenteVisual projectile; // projectile image
	
	/** Creates a town.
	 * @param image graphic image
	 * @param team team that owns the farm
	 * @param world world where it is placed
	 * @param pop starting population
	 * @param maxPop maximum supported population
	 */
	public Town(ComponenteVisual image, Team team, World world, int pop, int maxPop) {
		this.world = world;
		this.image = image;
		this.population = pop;		
		this.maxPop = maxPop;
		this.team = team;
		this.team.addTown( this );
		resetRegeneration();       // starts the farm's population regeneration
		// load projectile image
		try {
			projectile = new ComponenteSimples( "data/fireball_small.png");
		} catch (IOException e) {
		}
	}

	/** Does the processing at the beginning of the turn
	 */
	public void startTurn() {
		int nGroups = getPopulation() / 20;
		getTeam().addExtraDefense( 1 * nGroups );		
	}

	/** Returns how much population this town wants to grow
	 * @return  how much population it wants to grow
	 */
	public int howMuchGrowth() {
		return itsRegenerationTime()? 1: 0;
	}

	/** Regenerates the population either by growing it or reducing it
	 * @param maxGrowth maximum population that can be added
	 */
	public void regeneratePop(int maxGrowth) {
		if( !itsRegenerationTime() )
			return;
		resetRegeneration();
		
		// if it is growing but it is full, it does nothing
		if( maxGrowth >= 0 && isFull() )
			return;
		
		// updates the population	
		managePopulation( maxGrowth );
	}


	/** Increases or decreases the population, if possible.
	 * It checks if it is overpopulated, and if so, the population may decrease
	 * even if the maxGrowth is positive 
	 * @param maxGrowth maximum population that can be added,
	 * as it may grow but the team has not enough food
	 */
	private void managePopulation(int maxGrowth) {		
		int increment = Math.min( maxGrowth, isOverpopulated()? -1: 1 );
		changePopulation( increment );   
	}

	/** resets the regeneration cycle counting
	 */
	private void resetRegeneration() {
		setNextRegeneration( team.getRegenerationRate() ); 
	}
	
	/** Returns the food produced by this town
	 * @return the food produced by this town
	 */
	public int getFoodOutput() {
		return getBaseFoodOutput();
	}

	/** Updates the town. 
	 * Each call to this method is a processing cycle
	 */
	public void update(){
		// decrements the population regenerator countdown
		nextRegen--;
		
		dealWithEnemies();
	}
	
	/** Recruit an army in this town
	 * @param dest the building where this army will go
	 * @return the created army, if population allows it
	 */
	public Army recruitArmy(Town dest) {
		int nSoldiers = recruitSoldiers();
		if( nSoldiers == 0 )
		   return null;		
		return createArmy( dest, nSoldiers );
	}

	/** Eenlists an army in this town
	 * @param dest the building where this army will go
	 * @return the created army, if population allows it
	 */
	public Army enlistArmy(Town dest) {
		int nSoldiers = enlistSoldiers();
		if( nSoldiers == 0 )
		   return null;		
		return createArmy( dest, nSoldiers );
	}

	/** Creates an army 
	 * @param dest the building where this army will go
	 * @param nSoldiers number of soldiers
	 * @return the created army
	 */
	private Army createArmy(Town dest, int nSoldiers) {
		return new Army(team, getDoorLocation(), dest, nSoldiers, team.getAttack(), team.getSpeed() );
	}

	/** recruits inhabitants to form an army and returns
	 * the number of recruited soldiers 
	 * @return the number of recruited soldiers
	 */
	private int recruitSoldiers(){
		int popBat = getPopulation()/2;  
		changePopulation( -popBat );
		return popBat;
	}
	
	/** Enlists inhabitants in an army and returns
	 * the number of enlisted soldiers
	 * @return the number of enlisted soldiers
	 */
	private int enlistSoldiers(){
		int popBat = (int)(getPopulation()*0.8);
		changePopulation( -popBat );
		return popBat;
	}
	
	/** Returns the defense capability
	 * @return the defense capability
	 */
	public double getDefense() {
		return team.getDefense() * population;
	}

	/** set the next regeneration cycle
	 * @param regen the new cycle
	 */
	private void setNextRegeneration(int regen) {
		nextRegen = regen;		
	}

	/** Indicates if it is in a regenerating cycle,
	 * where the population may grow
	 * @return true, is this cycle is a regeneration cycle
	 */
	private boolean itsRegenerationTime() {
		return nextRegen <= 0;
	}

	/** Changes the team that owns the town
	 * @param t the new farm owner
	 */
	public void setTeam(Team t) {
		team.removeTown( this );
		team = t;
		team.addTown( this );
		resetRegeneration();
	}

	/** Increases or decreases the population
	 * Use this instead of setPopulation
	 */
	public void changePopulation(int popChange) {
		population += popChange;
		if( population <= 0 )
			population = 0;
	}

	/** draws the farm
	 * @param g where to draw
	 */
	public void draw(Graphics g) {
		// draw the building image
		image.desenhar( g );
		Point p = image.getPosicaoCentro();
		
		// draw the team flag
		drawFlag(g, p);
	}

	/** draws the flag over the farm image
	 * @param g where to draw
	 * @param p the location of the flag
	 */
	private void drawFlag(Graphics g, Point p) {
		ComponenteVisual flag = team.getFlag();
		flag.setPosicaoCentro( new Point(p.x, image.getPosicao().y-8) );
		flag.desenhar( g );
		g.setColor( Color.BLACK );
		g.drawString( ""+population, p.x-(population>=10? 8: 4), image.getPosicao().y-3);
	}

	/** Checks whether a point is inside the farm area
	 * @param pt the poit to check
	 * @return true if the point is inside the farm area 
	 */
	public boolean isInside(Point pt) {	
		return image.getBounds().contains(pt);
	}

	/** returns the population, i.e, the number of inhabitants
	 * @return the number of inhabitants
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * returns the door location, the location where the soldiers exit
	 * @return the door location
	 */
	public Point getDoorLocation() {
		int x = image.getPosicaoCentro().x;
		int y = image.getPosicao().y+image.getAltura(); 
		return new Point( x, y );
	}

	/** Returns the team that owns the farm
	 * @return the team that owns the farm
	 */
	public Team getTeam() {
		return team;
	}

	/** Returns the maximum population allowed in this town,
	 * counting with team extras
	 * @return the maximum population allowed
	 */
	public int getMaxPopulation() {
		return maxPop + team.getMaxPopExtra();
	}

	/** Checks if the town is full, i.e., its population
	 * is equals to the maximum allowed
	 * @return true if it if full
	 */
	public boolean isFull() {
		return population == getMaxPopulation();
	}

	/** Checks if the town is overpopulated, i.e., if the population
	 * is above the allowed maximum 
	 * @return true if it is overpopulated
	 */
	public boolean isOverpopulated() {
		return population > getMaxPopulation();
	}

	/** Returns the produced food, without extras 
	 * @return the base food production
	 */
	public int getBaseFoodOutput() {
		return foodOutput;
	}

	/** Sets the base food output
	 * @param foodOutput the new food output
	 */
	public void setBaseFoodOutput(int comidaProduz) {
		this.foodOutput = comidaProduz;
	}

	/** Returns the world where this farm is located
	 * @return the world where it is located
	 */
	public World getWorld() {
		return world;
	}
	
	/** attacks enemies that are within range
	 */
	private void dealWithEnemies(){
		// update the shot countdown and check if it is shooting time 
		nextShot--;
		if( nextShot > 0 ) return;
		
		nextShot = 30;                  // reset the countdown
		fire( 1, projectile.clone() );  // shoot
	}

	/** Fires over the enemy
	 * @param damage how many soldiers it kills
	 * @param projetilImg tha image of the projectile
	 */
	private void fire( int damage, ComponenteVisual projetilImg ) {
		// select the target
		Army target = null;
		for( Army a : getWorld().getArmies() ){
			// it must be from a different team and be within range
			if( a.getTeam() != getTeam() && inRange( a.getPos() ) ){
				target = a;
			}
		}
		// if a target is acquired it fires
		if( target != null ){
			ComponenteVisual shotImg = projetilImg;
			shotImg.setPosicao( getDoorLocation() );
			Projectile b = new Projectile( shotImg, damage, target);
			getWorld().addProjectile( b );
		}
	}
	
	/** Check if a point is within range of the town defenses 
	 * @param pos point to check
	 * @return true is it is within range 
	 */
	private boolean inRange(Point2D.Double pos) {
		return getDoorLocation().distanceSq( pos  ) < rangeSq;
	}
}
