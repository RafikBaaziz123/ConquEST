package conquest.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import conquest.building.Town;
import conquest.world.Team;

/** Represents the currently selected towns to perform
 * a troop displacement
 */
public class BuildingSelection {
	private Team team;  // the player team
	
	// TODO support all building types
	// list with all selected towns
	private ArrayList<Town> towns = new ArrayList<Town>();
	
	/** Creates a selection for a given team
	 * @param t player team
	 */
	public BuildingSelection(Team t) {
		this.team = t;
	}

	/** checks if the selection is empty, i.e., has no town selected
	 * @return true if it is empty
	 */
	public boolean isEmpty() {
		return towns.isEmpty();
	}
	
	/** Clears the selection, i.e., removes all selected buildings from this selection
	 */
	public void clear() {
		towns.clear();
	}
	
	/** Adds a town to the current selection, if it is non null,
	 * from the player team and not already selected 
	 * @param t town to add
	 * @return true if t was added successfully  
	 */
	public boolean addTown( Town t ) {
		// TODO support all building types
		if( t == null || t.getTeam() != team || towns.contains( t ) )
			return false;
		return towns.add( t );
	}
	
	/** removes a town from the selection
	 * @param t town to remove
	 * @return true if it actually removed the town
	 */
	public boolean removeVila( Town v ) {
		// TODO support all building types
		return towns.remove( v );
	}
	
	/** Returns all towns present in the selection
	 * @return all towns present in the selection
	 */
	public List<Town> getTowns() {
		// TODO support all building types
		// recheck if all selected building are still in the player team
		// as they could be overtaken when the player was making the selection 
		for( int i=towns.size()-1; i >= 0; i-- )
			if( towns.get(i).getTeam() != team )
				towns.remove( i );
		return Collections.unmodifiableList( towns );
	}
}
