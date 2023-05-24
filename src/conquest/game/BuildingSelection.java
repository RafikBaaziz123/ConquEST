package conquest.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import conquest.building.Building;
import conquest.world.Team;

/** Represents the currently selected towns to perform
 * a troop displacement
 */
public class BuildingSelection {
	private Team team;  // the player team
	
	// TODO support all building types (DONE)
	// list with all selected buildings
	private ArrayList<Building> buildings = new ArrayList<Building>();
	
	/** Creates a selection for a given team
	 * @param t player team
	 */
	public BuildingSelection(Team t) {
		this.team = t;
	}

	/** checks if the selection is empty, i.e., has no buildings selected
	 * @return true if it is empty
	 */
	public boolean isEmpty() {
		return buildings.isEmpty();
	}
	
	/** Clears the selection, i.e., removes all selected buildings from this selection
	 */
	public void clear() {
		buildings.clear();
	}
	
	/** Adds a building to the current selection, if it is non null,
	 * from the player team and not already selected 
	 * @param b building to add
	 * @return true if t was added successfully  
	 */
	public boolean addBuilding( Building b ) {
		// TODO support all building types
		if( b == null || b.getTeam() != team || buildings.contains( b ) )
			return false;
		return buildings.add( b );
	}
	
	/** removes a building from the selection
	 * @param b building to remove
	 * @return true if it actually removed the building
	 */
	public boolean removeBuilding( Building b ) {
		// TODO support all building types
		return buildings.remove( b );
	}
	
	/** Returns all towns present in the selection
	 * @return all towns present in the selection
	 */
	public List<Building> getBuildings() {
		// TODO support all building types
		// recheck if all selected building are still in the player team
		// as they could be overtaken when the player was making the selection 
		for( int i=buildings.size()-1; i >= 0; i-- )
			if( buildings.get(i).getTeam() != team )
				buildings.remove( i );
		return Collections.unmodifiableList( buildings );
	}
}
