package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import player.Camera;

public class Utilities
{
	/**
	 * @param array
	 * @return whether or not a byte array is all zeros or not
	 */
	public static boolean allZeros(byte[] array)
	{
		for (byte b : array)
		{
			if (b != 0)
			{
				return false;
			}
		}
		return true;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale)
	{
		// returns a 4x4 matrix created using translation, rotation, and scale
		// uses static methods in Matrix4f to transform matrix and put it back into
		// matrix
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		// translates
		Matrix4f.translate(translation, matrix, matrix);

		// rotates
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);

		// scales
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

		return matrix;
	}

	public static float clamp(float x, float low, float high)
	{
		return x < low ? low : x > high ? high : x;
	}

	public static void checkGlError(String string)
	{
		int errorCode = GL11.glGetError();
		if (errorCode != 0)
		{
			String errorString = GLU.gluErrorString(errorCode);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + string);
			System.out.println(errorCode + ": " + errorString);
			System.exit(0);
		}
	}

	public static Vector2f toCartesian(float angle)
	{
		return new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
	}

	public static float dot(Vector2f v1, Vector2f v2)
	{
		return (float) (v1.length() * v2.length() * Math.cos(Vector2f.angle(v1, v2)));
	}

	public static String convertFileIntoString(String file) throws Exception
	{
		return new String(Files.readAllBytes(Paths.get(file)));
	}

	public static BufferedImage getBufferedImage(String file) throws Exception
	{
		return ImageIO.read(new File(file));
	}

	public static void fatalError(String message)
	{
		System.err.println("Fatal Error: " + message);
		System.exit(0);
	}

	public static void addImage(BufferedImage src, BufferedImage dest, int x, int y)
	{
		dest.getGraphics().drawImage(src, x, y, null);
	}
}
