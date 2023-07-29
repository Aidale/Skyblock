package player;

import java.util.HashMap;

import display.DisplayManager;
import font.TextGUI;
import renderer.ResourceManager;
import utils.Timer;

/**
 * meant to imitate the debug menu in minecraft, currently includign information of where the player is, 
 * what block they are looking at, and what direction they are facing
 * TODO make this a static class
 * @author aidan
 *
 */
public class DebugMenu
{
	private Camera camera;
	private TargetedFrame targetedFrame;

	private boolean open;
	private Timer updateFPS;

	private HashMap<String, TextGUI> dynamic = new HashMap<>();

	public DebugMenu(Player player)
	{
		camera = player.camera;
		targetedFrame = player.targetedFrame;

		dynamic.put("fps", new TextGUI());
		dynamic.put("pos", new TextGUI());
		dynamic.put("rot", new TextGUI());
		dynamic.put("target", new TextGUI());

		updateFPS = new Timer(1000);

		open();
	}

	public void open()
	{
		open = true;
	}

	public void update()
	{
		if (open)
		{
//			ResourceManager.resourceUsage();
			dynamic.get("pos").remove();
			dynamic.get("rot").remove();
			dynamic.get("target").remove();

			if (updateFPS.ready())
			{
				dynamic.get("fps").remove();
				dynamic.put("fps", new TextGUI((int) (1 / DisplayManager.secondsPerFrame()) + " fps", 3, 3, 3));
			}
			
			dynamic.put("pos", new TextGUI(camera.getPosition(), 3, 30, 3));
			dynamic.put("rot", new TextGUI(camera.getFaceDirection(), 3, 57, 3));
			dynamic.put("target", new TextGUI(targetedFrame.toString(), 3, 84, 3));
			
//			ResourceManager.resourceUsage();
		}
	}

	public void close()
	{
		open = false;

		for (TextGUI text : dynamic.values())
		{
			text.remove();
		}
	}
}
