package renderer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import font.TextData;

public class ResourceManager
{
	private static ArrayList<Integer> vaos = new ArrayList<Integer>();
	private static ArrayList<Integer> vbos = new ArrayList<Integer>();
	private static ArrayList<Integer> textures = new ArrayList<Integer>();

	/**
	 * creates a texture in the openGL context, 
	 * @param name
	 * @return
	 */
	public static int loadTexture(String name)
	{
		Texture texture = null;
		try
		{
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + name + ".png"));
		}
		catch (UnsupportedOperationException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		int textureID = texture.getTextureID();
		textures.add(textureID);

		// GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		Renderer.setTextureFilterNearest();
		return textureID;
	}

	// uses an existing vaoID and two vboIDs to store position and texCoord data
	public static void buildText(int vaoID, int posID, int texID, float[] positions, float[] textureCoords)
	{
		bindVAO(vaoID);

		storeDataInAttributeList(posID, 0, 2, positions);
		storeDataInAttributeList(texID, 1, 2, textureCoords);

		unbindVAO();
	}

	// rebuilds using existing vaos and vbos, interleaves data
	public static void buildInterleavedModel(int vaoID, int vboID, int indicesID, float[] vertices, int[] indices)
	{
		bindVAO(vaoID);

		bindIndicesBuffer(indicesID, indices);
		storeInterleavedChunkData(vboID, vertices);

		unbindVAO();
	}

	// uses existing vboID to bind the index buffer
	private static void bindIndicesBuffer(int vboID, int[] indices)
	{
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	// store interleaved data describing the chunk using an existing vboID
	private static void storeInterleavedChunkData(int vboID, float[] data)
	{
		// Benchmark.benchmark.start("bind vbo");
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

		// position
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 20, 0);

		// uv texture coordinates
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 20, 12);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// Benchmark.benchmark.stop("bind vbo");
	}

	public static int createVAO()
	{
		int vaoID = GL30.glGenVertexArrays();
//		vaos.add(vaoID);
		return vaoID;
	}

	public static int createVBO()
	{
		int vboID = GL15.glGenBuffers();
//		vbos.add(vboID);
		return vboID;
	}

	public static void deleteVAO(int vaoID)
	{
		GL30.glDeleteVertexArrays(vaoID);
	}

	public static void deleteVBO(int vboID)
	{
		GL15.glDeleteBuffers(vboID);
	}

	private static void bindVAO(int vaoID)
	{
		GL30.glBindVertexArray(vaoID);
	}

	private static void bindVBO(int vboID)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
	}

	private static void unbindVAO()
	{
		GL30.glBindVertexArray(0);
	}

	private static void unbindVBO()
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	// stores non-interleaved float data using an existing vboID
	private static void storeDataInAttributeList(int vboID, int attributeNumber, int coordinateSize, float[] data)
	{
		bindVBO(vboID);

		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		unbindVBO();
	}

	// stores non-interleaved int data using an existing vboID
	private static void storeDataInAttributeList(int vboID, int attributeNumber, int coordinateSize, int[] data)
	{
		bindVBO(vboID);

		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		unbindVBO();
	}

	// stores non-interleaved float data using a newly created vboID
	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data)
	{
		int vboID = createVBO();
		bindVBO(vboID);

		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		unbindVBO();
	}

	// stores non-interleaved int data using a newly created vboID
	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, int[] data)
	{
		int vboID = createVBO();
		bindVBO(vboID);

		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_INT, false, 0, 0);

		unbindVBO();
	}

	/**
	 * stores the data of an int array into an IntBuffer
	 * @param data
	 * @return
	 */
	private static IntBuffer storeDataInIntBuffer(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * stores the data of a float array into a FloatBuffer
	 * 
	 * @param data
	 * @return
	 */
	private static FloatBuffer storeDataInFloatBuffer(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * displays the amount of vaos, vbos, and textureID currently in use, as well as 
	 * the amount of memory that the program is currently using
	 */
	public static void resourceUsage()
	{
		System.out.println("Elements currently in vao: " + vaos.size());
		System.out.println("Elements currently in vbo: " + vbos.size());
		System.out.println("Elements currently in textures: " + textures.size());

		System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
		System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
		System.out.println("Used memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	/**
	 * cleans up the vaos, vbos, and textures by deleting all of their references
	 */
	public static void cleanUp()
	{
		for (int vao : vaos)
		{
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos)
		{
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures)
		{
			GL11.glDeleteTextures(texture);
		}
	}
}