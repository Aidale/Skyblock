package block;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import utils.Utilities;

public class TerrainAssembler
{
	private static final int atlasSize = 256, paddingFactor = 2, textureSize = 16,
			texturesPerLength = atlasSize / (textureSize * paddingFactor);

	public static final float normalizedTextureSize = 1f / (texturesPerLength * paddingFactor);
	public static int atlasID;

	private static BufferedImage atlas;

	private static String path = "res/blocks";

	private static HashMap<String, Integer> pngs = new HashMap<>();

	/**
	 * Creates the texture atlas from the JSON at the specified path, then loads the
	 * atlas into a buffer where it can be used by opengl
	 * 
	 * @param path The location of the JSON file and all the block textures
	 * 
	 */
	protected static void assembleTerrain()
	{
		createTextureAtlasFromJSON();
		atlasID = loadTexture(atlas);
	}

	/**
	 * Reads the JSON file, puts all the blocks' information about textures for each
	 * blocks' faces into the appropriate block type in
	 * {@code RegistryBlock.blockList}, then compiles all the image files associated
	 * with all the block faces and arranges them in order on a texture atlas.
	 * 
	 * @param path The location of the JSON file and all the block textures
	 * 
	 * @return A buffered image representing the texture atlas of all blocks in the
	 *         game
	 */
	private static void createTextureAtlasFromJSON()
	{
		atlas = new BufferedImage(atlasSize, atlasSize, BufferedImage.TYPE_INT_ARGB);
		JSONObject json = null;
		try
		{
			Reader reader = new FileReader(path + "/blocks.json");
			JSONParser parser = new JSONParser();

			json = (JSONObject) parser.parse(reader);
		} 
		catch (FileNotFoundException fnfe)
		{
			Utilities.fatalError("JSON File not found. Path: " + path);
		} 
		catch (ParseException pe)
		{
			Utilities.fatalError("Parse Exception");
		} 
		catch (IOException ioe)
		{
			Utilities.fatalError("IOException");
		}

		JSONArray jsonArr = (JSONArray) json.get("blocks");
		// go through every block listed in the JSON
		for (int i = 0; i < jsonArr.size(); i++)
		{
			JSONObject jsonBlock = (JSONObject) jsonArr.get(i);

			String blockName = (String) jsonBlock.get("block");
			Block block = RegistryBlock.blockList.get(blockName);

			Object textureName = jsonBlock.get("texture");

			// will not have "missing texture" texture (the black and purple checkerboard)
			try
			{
				if (textureName instanceof String)
				{
					block.setTexCoords(getUVCoords((String) textureName));
				} 
				else if (textureName instanceof JSONObject)
				{
					JSONObject jsonTextureName = (JSONObject) textureName;
					String[] textureNames = new String[6];
					Vector2f[] texFaces = new Vector2f[6];

					if (jsonTextureName.containsKey("top"))
					{
						textureNames[EFace.TOP.ordinal()] = (String) jsonTextureName.get("top");
					}
					if (jsonTextureName.containsKey("bottom"))
					{
						textureNames[EFace.BOTTOM.ordinal()] = (String) jsonTextureName.get("bottom");
					}
					if (jsonTextureName.containsKey("side"))
					{
						String side = (String) jsonTextureName.get("side");
						textureNames[EFace.NORTH.ordinal()] = side;
						textureNames[EFace.SOUTH.ordinal()] = side;
						textureNames[EFace.EAST.ordinal()] = side;
						textureNames[EFace.WEST.ordinal()] = side;
					}

					for (int j = 0; j < textureNames.length; j++)
					{
						texFaces[j] = getUVCoords(textureNames[j]);
					}

					block.setTexCoords(texFaces);
				}
			} catch (Exception ex)
			{
				Utilities.fatalError("Missing texture in " + path + " for " + blockName);
			}
		}
	}

	private static Vector2f getUVCoords(String textureName)
	{
		Vector2f pixel;

		// if pngs doesn't contain the image (represented by the string textureName),
		// else it does
		if (!pngs.containsKey(textureName))
		{
			pixel = getPixelCoords(pngs.size());

			// find the image referenced in the json file, and add it to the texture atlas
			try
			{
				BufferedImage texture = Utilities.getBufferedImage(path + "/" + textureName + ".png");
				Utilities.addImage(texture, atlas, (int) pixel.x, (int) pixel.y);
				Utilities.addImage(texture, atlas, (int) pixel.x + textureSize, (int) pixel.y);
				Utilities.addImage(texture, atlas, (int) pixel.x, (int) pixel.y + textureSize);
				Utilities.addImage(texture, atlas, (int) pixel.x + textureSize, (int) pixel.y + textureSize);
			} catch (Exception e)
			{
				Utilities.fatalError("Could not find image for " + textureName);
			}

			pngs.put(textureName, pngs.size());
		} else
		{
			pixel = getPixelCoords(pngs.get(textureName));
		}

		return new Vector2f(pixel.x / atlasSize, pixel.y / atlasSize);
	}

	private static Vector2f getPixelCoords(int t)
	{
		int x = (t % texturesPerLength) * textureSize * paddingFactor;
		int y = (t / texturesPerLength) * textureSize * paddingFactor;

		return new Vector2f(x, y);
	}

	/**
	 * Takes in a buffered image, puts it into a {@code ByteBuffer}, creates a
	 * texture associated with that buffer, and applies some settings to that
	 * texture
	 * 
	 * @param image The buffered image to be put into a {@code ByteBuffer}
	 * 
	 * @return The ID representing that texture; the "name" of the texture
	 */
	private static int loadTexture(BufferedImage image)
	{
		int BYTES_PER_PIXEL = 4; // 3 for RGB, 4 for RGBA
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); // 4
																													// for
																													// RGBA,
																													// 3
																													// for
																													// RGB

		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int pixel = pixels[y * image.getWidth() + x];

				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component

				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component. Only for RGBA
			}
		}

		buffer.flip();

		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:

		int textureID = GL11.glGenTextures(); // Generate texture ID
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID

		// Setup wrap mode
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, buffer);

		// Setup mipmaps
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);

		Utilities.checkGlError("generated mipmaps");

		// Setup anisotropic filtering
//        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, 16f); 

		// Return the texture ID so we can bind it later again
		return textureID;
	}
}