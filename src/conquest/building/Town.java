package conquest.building;

import java.awt.geom.Point2D;
import java.io.IOException;

import conquest.world.Army;
import conquest.world.Team;
import conquest.world.World;
import conquest.world.Projectile;
import prof.jogos2D.image.*;

public class Town extends BuildingDefault {

	private int nextShot = 30; // countdown to the next shot
	private int rangeSq = 140 * 140; // range squared to ease the calculations
	private ComponenteVisual projectile; // projectile image

	/**
	 * Creates a town.
	 * 
	 * @param image  graphic image
	 * @param team   team that owns the farm
	 * @param world  world where it is placed
	 * @param pop    starting population
	 * @param maxPop maximum supported population
	 */
	public Town(ComponenteVisual image, Team team, World world, int pop, int maxPop) {
		super(image, team, world, pop, maxPop);
		this.team.addBuilding(this);
		resetRegeneration(); // starts the farm's population regeneration
		// load projectile image
		try {
			projectile = new ComponenteSimples("data/fireball_small.png");
		} catch (IOException e) {
		}
	}

	public int howMuchGrowth() {
		return itsRegenerationTime() ? 1 : 0;
	}

	public void managePopulation(int maxGrowth) {
		int increment = Math.min(maxGrowth, isOverpopulated() ? -1 : 2);
		changePopulation(increment);
	}

	/**
	 * Does the processing at the beginning of the turn
	 */
	public void startTurn() {
		int nGroups = getPopulation() / 20;
		getTeam().addExtraDefense(1 * nGroups);
	}

	/**
	 * resets the regeneration cycle counting
	 */
	public void resetRegeneration() {
		setNextRegeneration(team.getRegenerationRate());
	}

	/**
	 * Returns the food produced by this town
	 * 
	 * @return the food produced by this town
	 */
	public int getFoodOutput() {
		return getBaseFoodOutput();
	}

	/**
	 * Updates the town. Each call to this method is a processing cycle
	 */
	public void update() {
		// decrements the population regenerator countdown
		nextRegen--;

		dealWithEnemies();
	}

	/**
	 * Recruit an army in this town
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
	 * Creates an army
	 * 
	 * @param dest      the building where this army will go
	 * @param nSoldiers number of soldiers
	 * @return the created army
	 */
	public Army createArmy(Building dest, int nSoldiers) {
		return new Army(team, getDoorLocation(), dest, nSoldiers, team.getAttack(), team.getSpeed());
	}

	/**
	 * recruits inhabitants to form an army and returns the number of recruited
	 * soldiers
	 * 
	 * @return the number of recruited soldiers
	 */
	public int recruitSoldiers() {
		int popBat = getPopulation() / 2;
		changePopulation(-popBat);
		return popBat;
	}

	/**
	 * Enlists inhabitants in an army and returns the number of enlisted soldiers
	 * 
	 * @return the number of enlisted soldiers
	 */
	public int enlistSoldiers() {
		int popBat = (int) (getPopulation() * 0.8);
		changePopulation(-popBat);
		return popBat;
	}

	/**
	 * Returns the defense capability
	 * 
	 * @return the defense capability
	 */
	public double getDefense() {
		return team.getDefense() * population;
	}

	/**
	 * Changes the team that owns the town
	 * 
	 * @param t the new farm owner
	 */
	public void setTeam(Team t) {
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

	/**
	 * attacks enemies that are within range
	 */
	private void dealWithEnemies() {
		// update the shot countdown and check if it is shooting time
		nextShot--;
		if (nextShot > 0)
			return;

		nextShot = 30; // reset the countdown
		fire(1, projectile.clone()); // shoot
	}

	/**
	 * Fires over the enemy
	 * 
	 * @param damage      how many soldiers it kills
	 * @param projetilImg tha image of the projectile
	 */
	private void fire(int damage, ComponenteVisual projetilImg) {
		// select the target
		Army target = null;
		for (Army a : getWorld().getArmies()) {
			// it must be from a different team and be within range
			if (a.getTeam() != getTeam() && inRange(a.getPos())) {
				target = a;
			}
		}
		// if a target is acquired it fires
		if (target != null) {
			ComponenteVisual shotImg = projetilImg;
			shotImg.setPosicao(getDoorLocation());
			Projectile b = new Projectile(shotImg, damage, target);
			getWorld().addProjectile(b);
		}
	}

	/**
	 * Check if a point is within range of the town defenses
	 * 
	 * @param pos point to check
	 * @return true is it is within range
	 */
	private boolean inRange(Point2D.Double pos) {
		return getDoorLocation().distanceSq(pos) < rangeSq;
	}

	@Override
	public int getBaseFoodOutput() {
		return foodOutput;
	}
}