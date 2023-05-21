package conquest.building;

import java.awt.Graphics;
import java.awt.Point;

import conquest.world.Army;
import conquest.world.Team;
import conquest.world.World;

public interface Building {
	/**
	 * Does the processing at the beginning of the turn
	 */
	void startTurn();

	/**
	 * Returns how much population this farm wants to grow
	 * 
	 * @return how much population it wants to grow
	 */
	int howMuchGrowth();

	/**
	 * Regenerates the population either by growing it or reducing it
	 * 
	 * @param maxGrowth maximum population that can be added
	 */
	void regeneratePop(int maxGrowth);

	/**
	 * Increases or decreases the population, if possible. It checks if it is
	 * overpopulated, and if so, the population may decrease even if the
	 * maxIncrement is positive
	 * 
	 * @param maxGrowth maximum population that can be added, as it may grow but the
	 *                  team has not enough food
	 */
	void managePopulation(int maxGrowth);

	/**
	 * resets the regeneration cycle countdown
	 */
	void resetRegeneration();

	/**
	 * Returns the food produced by this farm
	 * 
	 * @return the food produced by this farm
	 */
	int getFoodOutput();

	/**
	 * Updates the farm. Each call to this method is a processing cycle
	 */
	void update();

	/**
	 * Recruit an army in this farm
	 * 
	 * @param dest the building where this army will go
	 * @return the created army, if population allows it
	 */
	Army recruitArmy(Town dest);

	/**
	 * Enlist an army in this farm
	 * 
	 * @param dest the building where this army will go
	 * @return the created army, if population allows it
	 */
	Army enlistArmy(Town dest);

	/**
	 * Creates an army
	 * 
	 * @param dest      the building where this army will go
	 * @param nSoldiers number of soldiers
	 * @return the created army
	 */
	Army createArmy(Town dest, int nSoldiers);

	/**
	 * recruits inhabitants to form an army and returns the number of recruited
	 * soldiers
	 * 
	 * @return the number of recruited soldiers
	 */
	int recruitSoldiers();

	/**
	 * Enlists inhabitants in an army and returns the number of enlisted soldiers
	 * 
	 * @return the number of enlisted soldiers
	 */
	int enlistSoldiers();

	/**
	 * Returns the defense capability
	 * 
	 * @return the defense capability
	 */
	double getDefense();

	/**
	 * set the next regeneration cycle
	 * 
	 * @param regen the new cycle
	 */
	void setNextRegeneration(int regen);

	/**
	 * Indicates if it is in a regenerating cycle, where the population may grow
	 * 
	 * @return true, is this cycle is a regeneration cycle
	 */
	boolean itsRegenerationTime();

	/**
	 * Changes the team that owns the farm
	 * 
	 * @param t the new farm owner
	 */
	void setTeam(Team t);

	/**
	 * Increases or decreases the population Use this instead of setPopulation
	 */
	void changePopulation(int popChange);

	/**
	 * draws the farm
	 * 
	 * @param g where to draw
	 */
	void draw(Graphics g);

	/**
	 * draws the flag over the farm image
	 * 
	 * @param g where to draw
	 * @param p the location of the flag
	 */
	void drawFlag(Graphics g, Point p);

	/**
	 * Checks whether a point is inside the farm area
	 * 
	 * @param pt the poit to check
	 * @return true if the point is inside the farm area
	 */
	boolean isInside(Point pt);

	/**
	 * returns the population, i.e, the number of inhabitants
	 * 
	 * @return the number of inhabitants
	 */
	int getPopulation();

	/**
	 * returns the door location, the location where the soldiers exit
	 * 
	 * @return the door location
	 */
	Point getDoorLocation();

	/**
	 * Returns the team that owns the farm
	 * 
	 * @return the team that owns the farm
	 */
	Team getTeam();

	/**
	 * Returns the maximum population allowed in this farm, counting with team
	 * extras
	 * 
	 * @return the maximum population allowed
	 */
	int getMaxPopulation();

	/**
	 * Checks if the farm is full, i.e., its population is equals to the maximum
	 * allowed
	 * 
	 * @return true if it if full
	 */
	boolean isFull();

	/**
	 * Checks if the farm is overpopulated, i.e., if the population is above the
	 * allowed maximum
	 * 
	 * @return true if it is overpopulated
	 */
	boolean isOverpopulated();

	/**
	 * Returns the produced food, without extras
	 * 
	 * @return the base food production
	 */
	int getBaseFoodOuput();

	/**
	 * Sets the base food output
	 * 
	 * @param foodOutput the new food output
	 */
	void setBaseFoodOutput(int foodOutput);

	/**
	 * Returns the world where this farm is located
	 * 
	 * @return the world where it is located
	 */
	World getWorld();
}
