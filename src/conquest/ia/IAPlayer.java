package conquest.ia;

import java.util.List;

import conquest.building.*;
import conquest.world.*;

/**
 * This class represents the "Artificial Intelligence" of the game.
 * NO CHANGES ARE REQUIRED
 * NO CHANGES ARE REQUIRED
 */
public class IAPlayer {

	private Team team;    // the team this AI controls
	private World world;  // the world where it operates
	
	/**
	 * Create a new AI
	 * @param w the world where the team plays
	 * @param t the team it will control
	 */
	public IAPlayer(World w, Team t) {
		this.team = t;
		this.world = w;
	}

	/** make a move
	 */
	public void play( ){
		// Check, for each building the teams owns, if the population
		// is higher than half its maximum population size.
		// Is if is, then it attacks the closest building
		// TODO support all building types (DONE)
		for( Building t : team.getBuildings() ){
			// has more than half the maximum population?
			if( t.getPopulation() > t.getMaxPopulation() / 2 ){
				List< Building> targetCandidate = world.getBuilding();
				Building alvo = null;
				double lowestDistanceSq = 0;
				// TODO support all building types
				// pick the building to attack
				for( Building va : targetCandidate ){
					if( va.getTeam() == team ) // if it is from the same team ignore
						continue;
					if( alvo == null ){
						alvo = va;
						lowestDistanceSq = t.getDoorLocation().distance( alvo.getDoorLocation() );
					}
					else {
						double distancia = t.getDoorLocation().distance( va.getDoorLocation() );
						if( distancia < lowestDistanceSq ){
							alvo = va;
							lowestDistanceSq = distancia;
						}
					}
				}
				if( alvo != null ) {// if here is a target, then attack
					Army b = t.recruitArmy( alvo );
					world.addArmy( b );
				}
			}
		}		
	}
	
	/**
	 * Returns the team that this AI controls
	 * @return the team that this AI controls
	 */
	public Team getTeams() {
		return team;
	}
	
	/**
	 * Defines the team that this AI will control
	 * @param t the team that this AI will control
	 */
	public void setEquipa(Team t) {
		this.team = t;
	}
}
