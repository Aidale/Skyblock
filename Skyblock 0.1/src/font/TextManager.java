package font;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import utils.Utilities;

public class TextManager
{
	protected static TextAssembler assembler;
	private static ArrayList<TextGUI> texts;
	private static FontRenderer renderer;

	public static void init()
	{
		try
		{
			assembler = new TextAssembler("minecraftFont");
		} 
		catch (FileNotFoundException ex)
		{
			Utilities.fatalError("Font file not found");
		} 
		catch (IOException ex)
		{
			Utilities.fatalError("Could not read font file");
		}
		texts = new ArrayList<>();
		renderer = new FontRenderer();
	}

	public static void render()
	{
		renderer.render(texts);
	}

	protected static TextData loadText(TextGUI text)
	{
		texts.add(text);
		return assembler.getTextData(text);
	}

	protected static void removeText(TextGUI text)
	{
		texts.remove(text);
	}

	public static void cleanUp()
	{
		renderer.cleanUp();
	}
}
