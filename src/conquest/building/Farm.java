package conquest.building;

import prof.jogos2D.image.ComponenteVisual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import conquest.world.Army;
import conquest.world.Team;
import conquest.world.World;

public class Farm {

	public static final int FOOD_PER_GROUP = 15;
	public static final int GROUP_FOR_FOOD = 15;
	public static final int GROUP_FOR_EXTRAPOP = 15;
	
	private int population;            // current population
	private int maxPop;                // maximum population allowed (without team extras)
	private ComponenteVisual image;   	//skfjskfhds
	private int nextRegen;             // when is the next regeneration cycle
	private Team team;                 // the farm's team  
	private World world;               // world where it is placed
	private int foodOutput;            // how much food it produces (not counting production extras)
	
	/** Creates a farm.
	 * @param image graphic image
	 * @param team team that owns the farm
	 * @param world world where it is placed
	 * @param pop starting population
	 * @param maxPop maximum supported population
	 */
	public Farm(ComponenteVisual image, Team team, World world, int pop, int maxPop) {
		this.world = world;
		this.image = image;
		this.population = pop;		
		this.maxPop = maxPop;
		this.team = team;
		// TODO add this farm to its team
		this.world = world;
		resetRegeneration();       // starts the farm's population regeneration
	}

	/** Does the processing at the beginning of the turn
	 */
	public void startTurn() {
		// calculate the extra population that the farm provides
		int nGroups = getPopulation() / GROUP_FOR_EXTRAPOP;
		getTeam().addMaxPopExtra( 1*nGroups );
	}
	
	/** Returns how much population this farm wants to grow
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
	 * even if the maxIncrement is positive 
	 * @param maxGrowth maximum population that can be added,
	 * as it may grow but the team has not enough food
	 */
	private void managePopulation( int maxGrowth ) {		
		int increment = Math.min( maxGrowth, isOverpopulated()? -2: 1 );
		changePopulation( increment );   
	}
	
	/** resets the regeneration cycle countdown
	 */
	private void resetRegeneration() {
		setNextRegeneration( getTeam().getRegenerationRate() - 3 );
	}
	
	/** Returns the food produced by this farm
	 * @return the food produced by this farm
	 */
	public int getFoodOutput() {
		return foodOutput + (getPopulation()/GROUP_FOR_FOOD)*FOOD_PER_GROUP;
	}
	
	/** Updates the farm. 
	 * Each call to this method is a processing cycle
	 */
	public void update() {
		// decrements the population regenerator
		nextRegen--;
	}
	
	/** Recruit an army in this farm
	 * @param dest the building where this army will go
	 * @return the created army, if population allows it
	 */
	public Army recruitArmy(Town dest) {
		int nSoldiers = recruitSoldiers();
		if( nSoldiers == 0 )
		   return null;		
		return createArmy( dest, nSoldiers );
	}

	/** Enlist an army in this farm
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
	private Army createArmy(Town dest, int nSoldiers ) {
		return new Army(getTeam(), getDoorLocation(), dest,
				            nSoldiers, getTeam().getAttack()-2, getTeam().getSpeed() );
	}

	/** recruits inhabitants to form an army and returns
	 * the number of recruited soldiers 
	 * @return the number of recruited soldiers
	 */
	private int recruitSoldiers() {
		int nSoldiers = (int)(getPopulation() * 0.3);
		changePopulation( -nSoldiers );
		return nSoldiers;
	}

	/** Enlists inhabitants in an army and returns
	 * the number of enlisted soldiers
	 * @return the number of enlisted soldiers
	 */
	private int enlistSoldiers(){
		int nSoldiers = (int)(getPopulation() * 0.6);
		changePopulation( -nSoldiers );
		return nSoldiers;
	}
	
	/** Returns the defense capability
	 * @return the defense capability
	 */
	public double getDefense(){
		return (getTeam().getDefense() - 2) * getPopulation();
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
	
	/** Changes the team that owns the farm
	 * @param t the new farm owner
	 */
	public void setTeam(Team t) {
		// TODO finish this method
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

	/** Returns the maximum population allowed in this farm,
	 * counting with team extras
	 * @return the maximum population allowed
	 */
	public int getMaxPopulation() {
		return maxPop + team.getMaxPopExtra();
	}

	/** Checks if the farm is full, i.e., its population
	 * is equals to the maximum allowed
	 * @return true if it if full
	 */
	public boolean isFull() {
		return population == getMaxPopulation();
	}

	/** Checks if the farm is overpopulated, i.e., if the population
	 * is above the allowed maximum 
	 * @return true if it is overpopulated
	 */
	public boolean isOverpopulated() {
		return population > getMaxPopulation();
	}

	/** Returns the produced food, without extras 
	 * @return the base food production
	 */
	public int getBaseFoodOuput() {
		return foodOutput;
	}

	/** Sets the base food output
	 * @param foodOutput the new food output
	 */
	public void setBaseFoodOutput(int foodOutput) {
		this.foodOutput = foodOutput;
	}

	/** Returns the world where this farm is located
	 * @return the world where it is located
	 */
	public World getWorld() {
		return world;
	}	
}
