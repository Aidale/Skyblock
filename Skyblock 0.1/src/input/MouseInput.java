package input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public class MouseInput
{
	public static boolean mouseGrabbed;

	public static void createMouse()
	{
		// apparently does not need to be called
		try
		{
			Mouse.create();
		}
		catch (LWJGLException e)
		{
			System.err.println("Mouse could not be created");
			e.printStackTrace();
		}
	}

	public static void destroyMouse()
	{
		Mouse.destroy();
	}

	public static void grabMouse()
	{
		if (!mouseGrabbed)
		{
			mouseGrabbed = true;
			Mouse.setGrabbed(true);
		}
	}

	public static void releaseMouse()
	{
		if (mouseGrabbed)
		{
			mouseGrabbed = false;
			Mouse.setGrabbed(false);
		}
	}

	public static Vector2f getMouseVector()
	{
		if (!mouseGrabbed) return null;
		return new Vector2f(Mouse.getDX(), Mouse.getDY());
	}
}
