package display;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager
{
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
//  private static final int WIDTH = 3500;
//  private static final int HEIGHT = 2100;

	private static final int FPS_CAP = 60;

	private static long lastFrameTime;
	private static float delta;

	/**
	 * creates the display and the display context
	 */
	public static void createDisplay()
	{
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		
		try
		{
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Skyblock 0.1");
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}

		// set the display to take up the whole window
		GL11.glViewport(0, 0, WIDTH, HEIGHT);

		lastFrameTime = getCurrentTime();
	}

	/**
	 * updates the display, calculates the delta value, and keeps the FPS constant
	 */
	public static void updateDisplay()
	{
		// Display.sync() keeps the fps constant at FPS_CAP
		Display.sync(FPS_CAP);
		Display.update();

		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}

	/**
	 * @return the time between frames in seconds
	 */
	public static float secondsPerFrame()
	{
		return delta;
	}

	/**
	 * closes the display
	 */
	public static void closeDisplay()
	{
		Display.destroy();
	}

	/**
	 * @return the time in seconds since the program started
	 */
	private static long getCurrentTime()
	{
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}
