package font;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderer.ResourceManager;

public class TextGUI
{
	public final TextData data;
	public final String text;
	public final int x, y;
	public final int fontSize;
	public final boolean shadow, highlight, centered, inverted;
	public final Vector3f color;

	public TextGUI(String text, int x, int y, int fontSize, Vector3f color, boolean shadow, boolean highlight, boolean centered)
	{
		this.text = text;
		this.x = x;
		this.y = y;
		this.fontSize = fontSize;
		this.color = color;
		this.shadow = shadow;
		this.highlight = highlight;
		this.centered = centered;
		this.inverted = false;
		
		//only loads the data if the length of the text is greater than 0
		data = text.length() != 0 ? TextManager.loadText(this) : null;
	}

	public TextGUI(int symbol, int x, int y, int fontSize, boolean inverted)
	{
		this.text = (char) symbol + "";
		this.x = x;
		this.y = y;
		this.fontSize = fontSize;

		this.shadow = false;
		this.highlight = false;
		this.centered = true;
		this.inverted = inverted;

		this.color = new Vector3f(1, 1, 1);

		data = text.length() != 0 ? TextManager.loadText(this) : null;
	}

	public TextGUI()
	{
		text = "";
		x = 0;
		y = 0;
		fontSize = 0;
		shadow = false;
		highlight = false;
		centered = false;
		inverted = false;
		color = null;
		
		data = text.length() != 0 ? TextManager.loadText(this) : null;
	}

	public TextGUI(String text, int x, int y, int fontSize)
	{
		this(text, x, y, fontSize, new Vector3f(0.86f, 0.86f, 0.86f), false, true, false);
	}

	public void remove()
	{
		if (text.length() != 0)
		{
			TextManager.removeText(this);
			ResourceManager.deleteVAO(data.vaoID);
			ResourceManager.deleteVBO(data.posID);
			ResourceManager.deleteVBO(data.texID);
		}
	}
}
