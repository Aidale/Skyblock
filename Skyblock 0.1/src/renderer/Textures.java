package renderer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * contains the constants relating to the texture atlas, limiting it to 16*16 or 256 textures
 * also specifies that there are only 16 pixels per texture
 * @author aidan
 *
 */
public class Textures
{
	public static final int TEXTURE_SIZE = 16;
	public static final int U_TEXTURES = 16, V_TEXTURES = 16;
	public static final int PIXEL_WIDTH = TEXTURE_SIZE * U_TEXTURES, PIXEL_HEIGHT = TEXTURE_SIZE * V_TEXTURES;

	/**
	 * given a textureID, returns the uv coordinates of that texture within the texture atlas
	 * @param textureID
	 * @return
	 */
	public static float[] getUVCoords(int textureID)
	{
		float u = textureID % U_TEXTURES;
		float v = textureID / U_TEXTURES;
		// System.out.println("u = " + u + ", v = " + v);
		if (v >= V_TEXTURES) v = 0;
		return new float[] { u / U_TEXTURES, v / V_TEXTURES };
	}
}
