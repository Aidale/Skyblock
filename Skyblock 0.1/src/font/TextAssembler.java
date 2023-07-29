package font;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.Display;

import renderer.ResourceManager;

public class TextAssembler
{
	public static final int atlasSize = 512;
	public final int atlasID;

	private HashMap<Integer, Character> characters = new HashMap<>();

	// One text assembler per font
	public TextAssembler(String fontName) throws FileNotFoundException, IOException
	{
		atlasID = ResourceManager.loadTexture(fontName);

		BufferedReader reader = new BufferedReader(new FileReader(new File("res/" + fontName + ".fnt")));
		reader.readLine();
		reader.readLine();
		reader.readLine();
		reader.readLine();

		String line = "";
		while ((line = reader.readLine()) != null)
		{
			if (line.startsWith("kernings"))
				break;

			String[] args = line.split(" ");
			HashMap<String, Integer> argMap = new HashMap<>();

			for (int i = 0; i < args.length; i++)
			{
				String[] pair = args[i].split("=");
				if (pair.length == 2)
					argMap.put(pair[0], Integer.parseInt(pair[1]));
			}

			int id = argMap.get("id");
			int x = argMap.get("x");
			int y = argMap.get("y");
			int xOffset = argMap.get("xoffset");
			int yOffset = argMap.get("yoffset");
			int width = argMap.get("width");
			int height = argMap.get("height");

			Character c = new Character(id, x, y, width, height, xOffset, yOffset);
			characters.put(id, c);
		}
	}

	public TextData getTextData(TextGUI guiText)
	{
		String text = guiText.text;

		int arrayLength = 12 * text.length();
		float[] positions = new float[arrayLength];
		float[] textureCoords = new float[arrayLength];

		int posPointer = 0;
		int texPointer = 0;
		int charPointer = 0;
		int xAdvance = 0;
		while (charPointer < text.length())
		{
			Character c = characters.get((int) text.charAt(charPointer++));

//			if (c == null)
//			{
//				System.out.println("id: " + (int) text.charAt(charPointer - 1));
//			}

			int u = c.u;
			int v = c.v;
			int width = c.width;
			int height = c.height;

			if (charPointer == 1 && guiText.highlight)
			{
				u--;
				width++;
			}

			// 1
			positions[posPointer++] = c.xOffset + xAdvance;
			positions[posPointer++] = c.yOffset;
			// 3
			positions[posPointer++] = c.xOffset + xAdvance;
			positions[posPointer++] = c.yOffset + height;
			// 2
			positions[posPointer++] = c.xOffset + width + xAdvance;
			positions[posPointer++] = c.yOffset;
			// 2
			positions[posPointer++] = c.xOffset + width + xAdvance;
			positions[posPointer++] = c.yOffset;
			// 3
			positions[posPointer++] = c.xOffset + xAdvance;
			positions[posPointer++] = c.yOffset + height;
			// 4
			positions[posPointer++] = c.xOffset + width + xAdvance;
			positions[posPointer++] = c.yOffset + height;

			// 1
			textureCoords[texPointer++] = u;
			textureCoords[texPointer++] = v;
			// 3
			textureCoords[texPointer++] = u;
			textureCoords[texPointer++] = v + height;
			// 2
			textureCoords[texPointer++] = u + width;
			textureCoords[texPointer++] = v;
			// 2
			textureCoords[texPointer++] = u + width;
			textureCoords[texPointer++] = v;
			// 3
			textureCoords[texPointer++] = u;
			textureCoords[texPointer++] = v + height;
			// 4
			textureCoords[texPointer++] = u + width;
			textureCoords[texPointer++] = v + height;

			xAdvance += width;
		}

		int x = guiText.x;
		int y = guiText.y;
		if (guiText.centered)
		{
			x -= (int) ((positions[0] + positions[positions.length - 2]) / 2);
			y -= (int) ((positions[1] + positions[positions.length - 1]) / 2);
		}

		boolean even = true;
		for (int i = 0; i < arrayLength; i++, even = !even)
		{
			positions[i] *= guiText.fontSize;
			if (even)
			{
				positions[i] = 2 * (positions[i] + x) / Display.getWidth() - 1;
			} 
			else
			{
				positions[i] = 1 - 2 * (positions[i] + y) / Display.getHeight();
			}
			textureCoords[i] /= atlasSize;
		}

//		System.out.println("text: " + guiText.text);
		int vaoID = ResourceManager.createVAO(),
			posID = ResourceManager.createVBO(),
			texID = ResourceManager.createVBO();
		
		ResourceManager.buildText(vaoID, posID, texID, positions, textureCoords);
		return new TextData(vaoID, posID, texID, textureCoords.length / 2);
	}
}
