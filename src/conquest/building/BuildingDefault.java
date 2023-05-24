package conquest.building;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import conquest.world.Army;
import conquest.world.Team;
import conquest.world.World;
import prof.jogos2D.image.ComponenteVisual;

public abstract class BuildingDefault implements Building {
	
	protected int population; // current population
	protected int maxPop; // maximum population allowed (without team extras)
	protected ComponenteVisual image;
	protected int nextRegen; // when is the next regeneration cycle
	protected Team team; // the building team
	protected World world;
	protected int foodOutput; // how much food it produces (not counting production extras)

	public BuildingDefault(ComponenteVisual image, Team team, World world, int pop, int maxPop) {
		this.world = world;
		this.image = image;
		this.population = pop;
		this.maxPop = maxPop;
		this.team = team;

	}

	public void regeneratePop(int maxGrowth) {
		if (!itsRegenerationTime())
			return;
		resetRegeneration();

		// if it is growing but it is full, it does nothing
		if (maxGrowth >= 0 && isFull())
			return;

		// updates the population
		managePopulation(maxGrowth);
	}

	public Army enlistArmy(Building dest) {
		int nSoldiers = enlistSoldiers();
		if (nSoldiers == 0)
			return null;
		return createArmy(dest, nSoldiers);
	}

	public void setNextRegeneration(int regen) {
		nextRegen = regen;
	}

	public boolean itsRegenerationTime() {
		return nextRegen <= 0;
	}

	public void changePopulation(int popChange) {
		population += popChange;
		if (population <= 0)
			population = 0;
	}

	public void draw(Graphics g) {
		// draw the building image
		image.desenhar(g);
		Point p = image.getPosicaoCentro();

		// draw the team flag
		drawFlag(g, p);
	}

	public void drawFlag(Graphics g, Point p) {
		ComponenteVisual flag = team.getFlag();
		flag.setPosicaoCentro(new Point(p.x, image.getPosicao().y - 8));
		flag.desenhar(g);
		g.setColor(Color.BLACK);
		g.drawString("" + population, p.x - (population >= 10 ? 8 : 4), image.getPosicao().y - 3);
	}

	public boolean isInside(Point pt) {
		return image.getBounds().contains(pt);
	}

	/**
	 * returns the population, i.e, the number of inhabitants
	 * 
	 * @return the number of inhabitants
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * returns the door location, the location where the soldiers exit
	 * 
	 * @return the door location
	 */
	public Point getDoorLocation() {
		int x = image.getPosicaoCentro().x;
		int y = image.getPosicao().y + image.getAltura();
		return new Point(x, y);
	}

	/**
	 * Returns the team that owns the farm
	 * 
	 * @return the team that owns the farm
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Returns the maximum population allowed in this town, counting with team
	 * extras
	 * 
	 * @return the maximum population allowed
	 */
	public int getMaxPopulation() {
		return maxPop + team.getMaxPopExtra();
	}

	/**
	 * Checks if the town is full, i.e., its population is equals to the maximum
	 * allowed
	 * 
	 * @return true if it if full
	 */
	public boolean isFull() {
		return population == getMaxPopulation();
	}

	/**
	 * Checks if the farm is overpopulated, i.e., if the population is above the
	 * allowed maximum
	 * 
	 * @return true if it is overpopulated
	 */
	public boolean isOverpopulated() {
		return population > getMaxPopulation();
	}

	/**
	 * Returns the produced food, without extras
	 * 
	 * @return the base food production
	 */
	public int getBaseFoodOutput() {
		return foodOutput;
	}

	/**
	 * Sets the base food output
	 * 
	 * @param foodOutput the new food output
	 */
	public void setBaseFoodOutput(int foodOutput) {
		this.foodOutput = foodOutput;
	}

	/**
	 * Returns the world where this farm is located
	 * 
	 * @return the world where it is located
	 */
	public World getWorld() {
		return world;
	}

}