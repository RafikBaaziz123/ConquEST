package conquest.building;

import prof.jogos2D.image.ComponenteVisual;

import conquest.world.Army;
import conquest.world.Team;
import conquest.world.World;

public class Farm extends BuildingDefault {

	public static final int FOOD_PER_GROUP = 15;
	public static final int GROUP_FOR_FOOD = 15;
	public static final int GROUP_FOR_EXTRAPOP = 15;

	/**
	 * Creates a farm.
	 * 
	 * @param image  graphic image
	 * @param team   team that owns the farm
	 * @param world  world where it is placed
	 * @param pop    starting population
	 * @param maxPop maximum supported population
	 */
	public Farm(ComponenteVisual image, Team team, World world, int pop, int maxPop) {
		super(image, team, world, pop, maxPop);
		// TODO add this farm to its team (DONE)
		this.team.addBuilding(this);
		this.world = world;
		resetRegeneration(); // starts the farm's population regeneration
	}

	/**
	 * Does the processing at the beginning of the turn
	 */
	public void startTurn() {
		// calculate the extra population that the farm provides
		int nGroups = getPopulation() / GROUP_FOR_EXTRAPOP;
		getTeam().addMaxPopExtra(1 * nGroups);
	}

	public int howMuchGrowth() {
		return itsRegenerationTime() ? 1 : 0;
	}

	public void managePopulation(int maxGrowth) {
		int increment = Math.min(maxGrowth, isOverpopulated() ? -2 : 1);
		changePopulation(increment);
	}

	/**
	 * resets the regeneration cycle countdown
	 */
	public void resetRegeneration() {
		setNextRegeneration(getTeam().getRegenerationRate() - 3);
	}

	/**
	 * Returns the food produced by this farm
	 * 
	 * @return the food produced by this farm
	 */
	public int getFoodOutput() {
		return foodOutput + (getPopulation() / GROUP_FOR_FOOD) * FOOD_PER_GROUP;
	}

	/**
	 * Updates the farm. Each call to this method is a processing cycle
	 */
	public void update() {
		// decrements the population regenerator
		nextRegen--;
	}

	/**
	 * Recruit an army in this farm
	 * 
	 * @param dest the building where this army will go
	 * @return the created army, if population allows it
	 */
	public Army recruitArmy(Building dest) {
		int nSoldiers = recruitSoldiers();
		if (nSoldiers == 0)
			return null;
		return createArmy(dest, nSoldiers);
	}

	/**
	 * Enlist an army in this farm
	 * 
	 * @param dest the building where this army will go
	 * @return the created army, if population allows it
	 */

	/**
	 * Creates an army
	 * 
	 * @param dest the building where this army will go
	 * @param nSoldiers number of soldiers
	 * @return the created army
	 */
	public Army createArmy(Building dest, int nSoldiers) {
		return new Army(getTeam(), getDoorLocation(), dest, nSoldiers, getTeam().getAttack() - 2, getTeam().getSpeed());
	}

	/**
	 * recruits inhabitants to form an army and returns the number of recruited
	 * soldiers
	 * 
	 * @return the number of recruited soldiers
	 */
	public int recruitSoldiers() {
		int nSoldiers = (int) (getPopulation() * 0.3);
		changePopulation(-nSoldiers);
		return nSoldiers;
	}

	/**
	 * Enlists inhabitants in an army and returns the number of enlisted soldiers
	 * 
	 * @return the number of enlisted soldiers
	 */
	public int enlistSoldiers() {
		int nSoldiers = (int) (getPopulation() * 0.6);
		changePopulation(-nSoldiers);
		return nSoldiers;
	}

	/**
	 * Returns the defense capability
	 * 
	 * @return the defense capability
	 */
	public double getDefense() {
		return (getTeam().getDefense() - 2) * getPopulation();
	}

	/**
	 * set the next regeneration cycle
	 * 
	 * @param regen the new cycle
	 */

	/**
	 * Changes the team that owns the farm
	 * 
	 * @param t the new farm owner
	 */
	public void setTeam(Team t) {
		// TODO finish this method (DONE)
		team.removeBuilding(this);
		team = t;
		team.addBuilding(this);
		resetRegeneration();
	}

	/**
	 * Increases or decreases the population Use this instead of setPopulation
	 */
	public void changePopulation(int popChange) {
		population += popChange;
		if (population <= 0)
			population = 0;
	}
}