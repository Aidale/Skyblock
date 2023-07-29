package block;

import org.lwjgl.util.vector.Vector2f;

public class Block
{
	public Vector2f[] texCoords;

	public Block()
	{
		texCoords = new Vector2f[6];
	}

	public void setTexCoords(Vector2f vector)
	{
		for (int i = 0; i < texCoords.length; i++)
		{
			texCoords[i] = vector;
		}
	}

	public void setTexCoords(Vector2f[] vectors)
	{
		for (int i = 0; i < texCoords.length; i++)
		{
			texCoords[i] = vectors[i];
		}
	}

	public Vector2f getTexCoord(EFace face)
	{
		return texCoords[face.ordinal()];
	}

	public boolean isSolid()
	{
		return true;
	}
}
