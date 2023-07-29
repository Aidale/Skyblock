package worldgen;

public class Statics
{
	/*
	 * Static fields relating to the dimensions of the world. These will never change.
	 */
	public static final int CHUNK_NUM = 16, SUBCHUNK_NUM = 8, 
			                CHUNK_SIZE = 16, MAX_HEIGHT = SUBCHUNK_NUM * CHUNK_SIZE;
	public static final int BLOCKS_PER_SUBCHUNK = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
	public static final int BLOCKS_PER_CHUNK = BLOCKS_PER_SUBCHUNK * SUBCHUNK_NUM;
	public static final int VERTEX_PER_CHUNK = 37281;
	
	/**
	 * Takes in world coordinates x, y, and z, and returns the chunk number that those coordinates
	 * fall in. If the coordinates are outside the world, returns -1. Because it is untrusted, first must
	 * verify that the coordinates are valid
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the chunk ID the coordinates are in
	 */
	public static int untrustedWorldCoordsToChunkID(int x, int y, int z)
	{
		if (x < 0 || (x >>= 4) >= CHUNK_NUM || y < 0 || y >= MAX_HEIGHT || z < 0 || (z >>= 4) >= CHUNK_NUM)
		{
			return -1;
		}
		else
		{
			return x + CHUNK_NUM * z;
		}
	}

	/**
	 * Takes in chunk coordinates x and z and returns the chunk number that those chunk coordinates
	 * correspond to. These chunk coordinates are assumed to be trusted and within the world, so no
	 * verification is required.
	 * 
	 * @param x
	 * @param z
	 * @return the ID of the chunk
	 */
	public static int trustedChunkCoordsToID(int x, int z)
	{
		return x + CHUNK_NUM * z;
	}
	
	/**
	 * Takes in world coordinates and returns the corresponding ID of that position in the chunk
	 * @param x
	 * @param y
	 * @param z
	 * @return the ID of the position in the reference frame of the chunk its in
	 */
	public static int untrustedWorldCoordsToInternalChunkID(int x, int y, int z)
	{
		// return x + World.CHUNK_SIZE * (z + World.CHUNK_SIZE * y);
		if ((x >> 4) != 0 || y < 0 || y >= CHUNK_SIZE || (z >> 4) != 0)
		{
			return -1;
		} 
		else
		{
			return x | (z << 4) | (y << 8);
		}
	}
	
	/**
	 * Takes in trusted world coordinates and returns the corresponding ID of that position in the chunk
	 */
	public static int trustedWorldCoordsToInternalChunkID(int x, int y, int z)
	{
		return x | (z << 4) | (y << 8);
	}
}
