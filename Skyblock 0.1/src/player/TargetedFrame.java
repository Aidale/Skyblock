package player;

import org.lwjgl.util.vector.Vector3f;

import block.EFace;
import renderer.ResourceManager;
import renderer.Textures;

public class TargetedFrame
{
	public final int vaoID;
	public final int indexCount;
	public Vector3f target;
	public EFace face;

	public TargetedFrame()
	{
		float[] UV = Textures.getUVCoords(4);
		float u0 = UV[0];
		float v0 = UV[1];
		float u1 = u0 + 0.0624375F;
		float v1 = v0 + 0.0624375F;

		// each line is a face of the cube
		int[] positions = { 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1,
				0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0,
				1, 0, 0, 1, 0, 1, };

		float[] textureCoords = { u0, v0, u0, v1, u1, v1, u1, v0, u0, v0, u0, v1, u1, v1, u1, v0, u0, v0, u0, v1, u1,
				v1, u1, v0, u0, v0, u0, v1, u1, v1, u1, v0, u0, v0, u0, v1, u1, v1, u1, v0, u0, v0, u0, v1, u1, v1, u1,
				v0, };

		int[] indices = { 0, 1, 3, 3, 1, 2, 4, 5, 7, 7, 5, 6, 8, 9, 11, 11, 9, 10, 12, 13, 15, 15, 13, 14, 16, 17, 19,
				19, 17, 18, 20, 21, 23, 23, 21, 22 };

		float[] vertices = new float[positions.length + textureCoords.length];
		for (int i = 0, posPointer = 0, texPointer = 0; i < vertices.length;)
		{
			vertices[i++] = positions[posPointer++];
			vertices[i++] = positions[posPointer++];
			vertices[i++] = positions[posPointer++];
			vertices[i++] = textureCoords[texPointer++];
			vertices[i++] = textureCoords[texPointer++];
		}
		
		vaoID = ResourceManager.createVAO();
		int vboID = ResourceManager.createVBO();
		int indicesID = ResourceManager.createVBO();
		
		ResourceManager.buildInterleavedModel(vaoID, vboID, indicesID, vertices, indices);
		indexCount = indices.length;
	}

	public String toString()
	{
		if (target == null)
			return "";
		return "Looking at block: " + (int) target.x + " " + (int) target.y + " " + (int) target.z;
	}
}