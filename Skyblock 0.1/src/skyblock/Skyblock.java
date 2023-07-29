package skyblock;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import block.RegistryBlock;
import display.DisplayManager;
import font.TextManager;
import input.MouseInput;
import player.Player;
import renderer.Renderer;
import renderer.ResourceManager;
import utils.Benchmark;
import worldgen.World;

public class Skyblock
{
	private World world;
	private Renderer renderer;
	private Player player;

	/**
	 * Currently no implementation of the constructor
	 */
	public Skyblock()
	{

	}

	/**
	 * Initializes the program. Creates the display, registers all the blocks, 
	 * creates the world and the player, initializes {@code TextManager}, and grabs the mouse
	 */
	private void init()
	{	
		DisplayManager.createDisplay();

		//Benchmark.init("chunk", "load to VBOs", "bind vao", "creating buffers", "bind vbo");

		RegistryBlock.registerBlocks();

		world = new World();
		world.instantiateWorld();
		world.rebuild();

		TextManager.init();

		player = new Player(world, new Vector3f(121.5f, 40, 118.5f), new Vector3f(), 20, 0);
		renderer = new Renderer(player.camera);

		// MouseInput.createMouse();
		MouseInput.grabMouse();
	}

	/**
	 * This method will continue to run until the user requests to close the window. Within the game
	 * loop, we call {@code tick()}, {@code render()}, and then update the display
	 */
	public void run()
	{
		init();

		while (!Display.isCloseRequested())
		{
			// TODO implement a pausing system

			tick();
			render();
			
			DisplayManager.updateDisplay();
		}

		//stop();
	}

	/**
	 * Updates the world and allows the player to update with user input
	 */
	private void tick()
	{
		player.tick();
	}

	/**
	 * Renders the updated world and text
	 */
	private void render()
	{
		renderer.prepare();
		world.rebuild();
		renderer.render(world, player);

		TextManager.render();
	}

	/**
	 * Marks the end of the program. Prints a benchmark of how long different processes took. 
	 * Cleans up resources and closes the display
	 */
	private void stop()
	{		
		Benchmark.printReport();
		TextManager.cleanUp();
		ResourceManager.cleanUp();
		DisplayManager.closeDisplay();
	}

	public static void main(String[] args)
	{
		Skyblock s = new Skyblock();
		s.run();
	}
}
