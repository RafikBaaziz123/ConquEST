package conquest.game;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JOptionPane;

import conquest.building.Farm;
import conquest.building.Town;
import conquest.world.Team;
import conquest.world.World;
import prof.jogos2D.image.ComponenteSimples;
import prof.jogos2D.image.ComponenteVisual;

/**Responsible for reading and interpreting the level files
 */
public class LevelReader {

	private String artFolfer;    // folder where the building images are
	private String levelFolder;  // folder where the level files are
	
	/**
	 * Creates the file reader
	 * @param levelFolder folder where the level files are
	 * @param artFolder folder where the building images are
	 */
	public LevelReader( String levelFolder, String artFolder ) {
		this.levelFolder = levelFolder;
		this.artFolfer = artFolder;
	}

	/** read a file that represents a level
	 * @param level the level file to read
	 * @param teams the teams to use on this level
	 * @return the created world
	 */
	public World readFile( String level, Team teams[] ) {
		World world = null;
		String file = levelFolder + level;     // level file full name

		try( BufferedReader in = new BufferedReader( new FileReader( file )) ) {
			// open the level file			
			try {			
				// read the background image info
				// read the line with the background file name
				String backgroundFile = levelFolder + in.readLine(); 
				ComponenteSimples background = new ComponenteSimples( backgroundFile );

				// create the world
				world = new World( background ); 
				
				// read info about each building 
				// each line as the info:
				// building type, x position, y position, initial population, owner team
				String line = in.readLine();
				while( line != null ){
					// check if it is an empty line or a comment
					if( line.isBlank() || line.startsWith("%") ){
						line = in.readLine();
						continue;
					}
					// as the information is separated with ',' we use split
					String info[] = line.split(",");
					String buildingName = info[0].toLowerCase();   
					int x = Integer.parseInt( info[1] );   
					int y = Integer.parseInt( info[2] );   
					Point pos = new Point( x, y );
					int pop = Integer.parseInt( info[3] );        // initial population
					int teamIndex = Integer.parseInt( info[4] );  
					Team t = teams[ teamIndex ];
					String edifArtFile = artFolfer + buildingName +".gif";   // building image folder
					ComponenteSimples img = new ComponenteSimples( pos, edifArtFile );					
					Town town = null;
					Farm farm = null;
					// TODO support all building types
					// create the correct building given its name
					if( buildingName.equals("aldeia") ){
						town = createVillage(world, pop, t, img);
					}
					else if( buildingName.equals("vila")){
						town = createTown( world, pop, t, img);
					}
					else if( buildingName.equals("cidade")){
						town = createCity( world, pop, t, img);
					}
					else if( buildingName.equals("campo")){
						farm = createField(world, pop, t, img);
					}
					else if( buildingName.equals("quinta")){
						farm = createFarm(world, pop, t, img);						
					}
					else if( buildingName.equals("herdade")){
						farm = createPlantation(world, pop, t, img);
					}
					// TODO support all building types
					
					// is the building was created it is added to the world
					if( town != null )
						world.addBuilding(town);					
					if( farm != null )
						world.addBuilding( farm );					
					line = in.readLine();
				}				
			} catch( Exception e ){
				// in case something went wrong reading the file 
				e.printStackTrace();
				JOptionPane.showMessageDialog( null, "Error reading file " + file, "ERROR", JOptionPane.ERROR_MESSAGE );
				System.exit( 1 );
				return null;
			}
		} catch( Exception e ){
			// in case something went wrong opening the file
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, "Error opening file " + file, "ERROR", JOptionPane.ERROR_MESSAGE );
			System.exit( 1 );
			return null;
		}
		return world;
	}

	private Town createVillage(World w, int pop, Team t, ComponenteVisual img) {
		Town v = new Town( img, t, w, pop, 20 );
		v.setBaseFoodOutput( 20 );
		return v;
	}
	
	private Town createTown(World w, int pop, Team t, ComponenteVisual img) {
		Town v = new Town( img, t, w, pop, 40 );
		v.setBaseFoodOutput( 10 );
		return v;
	}
	
	private Town createCity(World w, int pop, Team t, ComponenteVisual img) {
		Town v = new Town( img, t, w, pop, 60 );
		v.setBaseFoodOutput( 0 );
		return v;
	}

	private Farm createField(World w, int pop, Team t, ComponenteVisual img) {
		Farm q = new Farm( img, t, w, pop, 20 );
		q.setBaseFoodOutput( 10 );
		return q;
	}
	
	private Farm createFarm(World w, int pop, Team t, ComponenteVisual img) {
		Farm q = new Farm( img, t, w, pop, 40 );
		q.setBaseFoodOutput( 20 );
		return q;
	}
	
	private Farm createPlantation(World w, int pop, Team t, ComponenteVisual img) {
		Farm q = new Farm( img, t, w, pop, 60 );
		q.setBaseFoodOutput( 30 );
		return q;
	}
	
}
