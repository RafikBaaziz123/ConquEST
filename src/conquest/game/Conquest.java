
package conquest.game;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import conquest.building.*;
import conquest.ia.IAPlayer;
import conquest.world.Army;
import conquest.world.Team;
import conquest.world.World;
import prof.jogos2D.util.Vector2D;

/**
 * Class that represents the game
 */
public class Conquest extends JFrame {

	// game elements
	private World world; // current world
	private int lavel; // current level

	private Team teams[]; // all teams playing
	private Team playerTeam; // player team
	private IAPlayer iaPlayers[] = new IAPlayer[2]; // IA that control the other teams

	// TODO support all building types
	private BuildingSelection sel; // currently selected buildings
	private Building dest; // building selected as the destination
	private Point end; // the current selected location

	// The level reader
	// level files are on the data/niveis folder
	// buidilng images are on the data/edificios folder
	private LevelReader lr = new LevelReader("data/niveis/", "data/edificios/");

	// time manager
	private Timer temporizador;

	// the possible states that the game can be on
	private static final int PLAYING = 0;
	private static final int VICTORY = 1;
	private static final int DEFEAT = 2;
	private int status; // current state

	// delay between detecting the end of the game and the game actually stopping
	private int finalDelay;

	// remembers if it is enlisting or recruiting soldiers
	private boolean enlisting;

	// visual elements
	private JPanel gameArea = null;

	// image used to draw on
	private Image screen;

	// font style to use on text info
	private Font infoFont = new Font("Roman", Font.BOLD, 14);

	// line styles to draw the motion arrows
	private Stroke estiloLinhaExterior = new BasicStroke(12.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private Stroke estiloLinhaInterior = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private Composite alphaMeio = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
	private Composite alphaFull = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);

	/** app version */
	private static final long serialVersionUID = 1L;

	/**
	 * Game constructor
	 */
	public Conquest() {
		setTitle("ConquEST");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initialize();
		startGame();
	}

	/**
	 * Starts the game
	 */
	private void startGame() {
		// Set the level to the level where you want to start
		lavel = 1;
		// play current level
		playLevel();
	}

	/**
	 * Plays the current level, initializes the teams and the AI.
	 */
	private void playLevel() {
		Team[] es = { new Team("Romans", 12, 9, 3, 40, 0), new Team("Neutral", 0, 7, 0, 0, 0),
				new Team("Aztecs", 10, 10, 5, 45, 2), new Team("Chinese", 9, 12, 4, 35, 4) };
		teams = es;
		playerTeam = teams[0];
		world = lr.readFile("nivel" + lavel + ".txt", teams);
		// you can use the line comments below to use the test level
		// world = lr.readFile( "teste.txt", es);

		// create IAs for the PC controlled teams
		for (int i = 0; i < iaPlayers.length; i++)
			iaPlayers[i] = new IAPlayer(world, teams[i + 2]);

		status = PLAYING;
		finalDelay = 30;
		sel = new BuildingSelection(playerTeam);

		// starts the timer that will update the game 30 times per second
		temporizador.start();
	}

	/**
	 * Method called automatically 30 times a second to update the game ATTENTION!
	 * This methods DOES NOT draw the game. Use method drawGame for that
	 */
	private void updateGame() {
		for (Team e : teams)
			e.startTurn();
		for (IAPlayer ia : iaPlayers)
			ia.play();
		world.update();
		checkGameOver();
	}

	/**
	 * Checks if the game is over. This method only changes the game state, is does
	 * not deal with the game end neither displays any messages
	 */
	private void checkGameOver() {
		// if it over, checks if the final delay as elapsed
		if (status != PLAYING) {
			finalDelay--;
			if (finalDelay <= 0) {
				temporizador.stop();
				endLevel();
			}
		}
		// TODO support all building types
		// if the player does not own any building then it is a defeat
		else if (playerTeam.getBuildingCount() == 0) {
			System.out.println(playerTeam.getBuildingCount());
			//status = DEFEAT;
		} else {
			// is no team has buildings then it a victory
			// neutrals may keep some buildings that a victory is granted all the same
			// TODO support all building types
			for (IAPlayer ia : iaPlayers)
				if (ia.getTeams().getBuildingCount() != 0)
					return;
			status = VICTORY;
		}
	}

	/**
	 * Method called when the level is over
	 */
	private void endLevel() {
		if (status == DEFEAT) {
			// Show the defeat message and ask what to do next
			String choices[] = { "Play this level again", "Return to level 1", "Exit game" };
			int answer = JOptionPane.showOptionDialog(null, "Crushing defeat! What do you want to do?", "DEFEAT",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
			switch (answer) {
			case 0:
				playLevel();
				break;
			case 1:
				startGame();
				break;
			case 2:
				System.exit(0);
			}
		} else {
			// show the victory message and ask what to do next
			String choices[] = { "PLay this level again", "Play next level", "Exit game" };
			int answer = JOptionPane.showOptionDialog(null, "Amazing victory! What do you want to do?", "VICTORY",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[1]);
			switch (answer) {
			case 0:
				playLevel();
				break;
			case 1:
				lavel++;
				playLevel();
				break;
			case 2:
				System.exit(0);
			}
		}
	}

	/**
	 * Method called when the player presses the mouse in the game area
	 * 
	 * @param me mouse related event
	 */
	private void ratoPremido(MouseEvent me) {
		end = me.getPoint();
		enlisting = (me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
		// TODO support all building types
		// check which town was pressed (if any)
		sel.addBuilding(world.getTownAt(end));
	}

	/**
	 * Method called when the player drags the mouse in the game area
	 * 
	 * @param me mouse related event
	 */
	private void ratoArrastado(MouseEvent me) {
		// is there is no selection, there is nothing to do
		if (sel.isEmpty())
			return;

		enlisting = (me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
		end = me.getPoint();
		// TODO support all building types
		// check if another town is selected
		Building v = world.getTownAt(end);
		if (v != null && v.getTeam() == playerTeam)
			sel.addBuilding(v);
		dest = v;
	}

	/**
	 * Method called when the player releases the mouse in the game area
	 * 
	 * @param me mouse related event
	 */
	private void ratoLibertado(MouseEvent me) {
		// if there is no selection, there is nothing to do
		if (sel.isEmpty())
			return;

		enlisting = (me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;

		// TODO support all building types 
		// check the destination town
		dest = world.getTownAt(me.getPoint());
		// is there is a destination, we must create the armies
		if (dest != null) {
			for (Building v : sel.getBuildings()) {
				// create an army in each selected building
				Army a;
				if (enlisting)
					a = v.enlistArmy(dest);
				else
					a = v.recruitArmy(dest);
				// is there is an army, it is added to the world
				if (a != null)
					world.addArmy(a);
			}
		}
		// reset selection and destination
		sel.clear();
		dest = null;
	}

	/**
	 * Method that is used to draw the game elements ALL DRAWINGS MUST BE DONE IN
	 * THIS METHOD
	 * 
	 * @param g where to draw
	 */
	private void drawGame(Graphics2D g) {
		// Use graphics2D in the aux image
		Graphics2D ge = (Graphics2D) screen.getGraphics();

		// draw the world
		world.draw(ge);
		drawMotionArrow(ge);

		// draw play team info
		ge.setColor(Color.black);
		ge.setFont(infoFont);

		ge.drawString("F:" + teams[0].getAvailableFood(), 10, 15);
		ge.drawString("P:" + teams[0].getPopulation(), 10, 30);
		ge.drawString("R:" + teams[0].getRegenerationRate(), 10, 45);
		ge.drawString("A:" + teams[0].getAttack(), 70, 15);
		ge.drawString("D:" + teams[0].getDefense(), 70, 30);
		ge.drawString("S:" + teams[0].getSpeed(), 70, 45);

		// when everything is drawn on the aux image, draw it in the screen
		g.drawImage(screen, 0, 0, null);
	}

	/**
	 * Helper method to draw the motion arrow
	 * 
	 * @param g where to draw
	 */
	private void drawMotionArrow(Graphics2D g) {
		// is there is no selection, no arrow is drawn
		if (sel.isEmpty())
			return;

		// as we will change line styles we must use an alternate graphic
		Graphics2D ge = (Graphics2D) g.create();

		// TODO support all building types
		// draw a line from the exit door of each building to the destination
		for (Building v : sel.getBuildings()) {
			Point ini = v.getDoorLocation();
			Point fim = dest == null ? this.end : dest.getDoorLocation();

			Vector2D dir = new Vector2D(ini, fim);
			dir.normalizar();
			Vector2D dirD = dir.getOrtogonalDireita();
			dirD.escalar(4);
			Vector2D dirE = dir.getOrtogonalEsquerda();
			dirE.escalar(4);
			Point2D.Double i = new Point2D.Double(ini.x, ini.y);
			Point2D.Double i1 = dirD.aplicaPonto(i);
			Point2D.Double i2 = dirE.aplicaPonto(i);

			// creates the lines and draws them in two phases, a thick but transparent one
			// and another narrower but solid
			ge.setComposite(alphaMeio);
			ge.setStroke(estiloLinhaExterior);
			Line2D.Double line1 = new Line2D.Double(i1, fim);
			Line2D.Double line2 = new Line2D.Double(i2, fim);
			// if it is enlisting the outer line is red
			// if it is recruiting its yellow
			if (enlisting)
				ge.setPaint(Color.RED);
			else
				ge.setPaint(Color.YELLOW);
			ge.draw(line1);
			ge.draw(line2);
			ge.setComposite(alphaFull);
			ge.setStroke(estiloLinhaInterior);
			// if there is a valid destination the inner line is green
			// otherwise its yellow
			if (dest != null)
				ge.setPaint(Color.GREEN);
			else
				ge.setPaint(Color.YELLOW);
			ge.draw(line1);
			ge.draw(line2);
		}

		ge.dispose();
	}

	/**
	 * Initializes the game area, NO CHANGES ARE REQUIRED
	 */
	private JPanel getGameArea() {
		if (gameArea == null) {
			gameArea = new JPanel() {
				public void paintComponent(Graphics g) {
					drawGame((Graphics2D) g);
				}
			};
			Dimension d = new Dimension(1000, 700);
			gameArea.setPreferredSize(d);
			gameArea.setSize(d);
			gameArea.setMinimumSize(d);
			gameArea.setBackground(Color.pink);
			gameArea.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					ratoPremido(e);
				}

				public void mouseReleased(MouseEvent e) {
					ratoLibertado(e);
				}
			});
			gameArea.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					ratoArrastado(e);
				}
			});
		}
		return gameArea;
	}

	/**
	 * Initializes the window interface, NO CHANGES ARE REQUIRED
	 */
	private void initialize() {
		// características da janela
		this.setLocationRelativeTo(null);
		this.setTitle("ConquEST");
		getContentPane().add(getGameArea(), BorderLayout.CENTER);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		// criar o temporizador
		temporizador = new Timer(33, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGame();
				gameArea.repaint();
			}
		});

		// criar a imagem para melhorar as animações e configurá-la para isso mesmo
		screen = new BufferedImage(1000, 700, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D ge = (Graphics2D) screen.getGraphics();
		ge.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ge.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	public static void main(String args[]) {
		Conquest ce = new Conquest();
		ce.setVisible(true);
	}
}
