package worldgen;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector3f;
import player.AABB;
/**
 * Contains an array of chunks, accessor and mutator methods for the blocks in the world, and several other
 * methods to act as an abstraction of the collection of chunks
 * TODO implement infinite world generation
 * @author aidan
 *
 */
public class World
{	
	private Chunk[] chunks;
	private WorldGenerator wg;

	public World()
	{
		chunks = new Chunk[Statics.CHUNK_NUM * Statics.CHUNK_NUM];
		for (int x = 0; x < Statics.CHUNK_NUM; x++)
		{
			for (int z = 0; z < Statics.CHUNK_NUM; z++)
			{
				chunks[Statics.trustedChunkCoordsToID(x, z)] = new Chunk(x, z);
			}
		}

		wg = new WorldGenerator(0);
		wg.setupPerlinNoise();
	}

	/**
	 * Either creates a new world or loads an old one
	 */
	public void instantiateWorld()
	{
		// TODO implement loading saved worlds
		generateWorld();
	}

	/**
	 * Uses the world generator to create the new world one chunk at a time
	 */
	private void generateWorld()
	{
		for (int x = 0; x < Statics.CHUNK_NUM; x++)
		{
			for (int z = 0; z < Statics.CHUNK_NUM; z++)
			{
				chunks[Statics.trustedChunkCoordsToID(x, z)].instantiate(wg.getChunk(x, z));
				// chunks[getChunkPosition(x, z)].instantiate(wg.generateRandomChunk());
			}
		}
	}

	/**
	 * Returns what type of block, represented as a byte value, is present in the chunk
	 * with chunk number "chunkPosition" at world coordinates x, y, z. chunkPosition can
	 * be ascertained from x, y, and z, but here is precalculated. All parameters are 
	 * preverified to be valid coordinates, hence the method being called "trusted"
	 * 
	 * @param chunkPosition the chunk number
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public byte getTrustedBlockType(int chunkPosition, int x, int y, int z)
	{
		return chunks[chunkPosition].getTrustedBlockType(x & 15, y, z & 15);
	}

	/**
	 * Sets the block in the chunk with chunk number "chunkPosition" at world
	 * coordinates x, y, and z equal to "block"
	 * 
	 * @param block
	 * @param chunkPosition
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTrustedBlockType(byte block, int chunkPosition, int x, int y, int z)
	{
		chunks[chunkPosition].setTrustedBlockType(block, x & 15, y, z & 15);
	}

	/**
	 * Verifies that the coordinates in "pos" are trustable (within the world) before setting
	 * the block at those coordinates equal to "block"
	 * 
	 * @param block
	 * @param pos
	 */
	public void setBlock(byte block, Vector3f pos)
	{
		int x = (int) pos.x, y = (int) pos.y, z = (int) pos.z;
		int chunkPosition = Statics.untrustedWorldCoordsToChunkID(x, y, z);
		if (chunkPosition == -1)
			return;
		setTrustedBlockType(block, chunkPosition, x, y, z);
	}

	/**
	 * For chunks that need to be rebuilt, rebuilds or "reconstructs" those chunks vertex data
	 * corresponding to when vertex data in that chunk has changed and needs to be updated
	 */
	public void rebuild()
	{
		for (int i = 0; i < chunks.length; i++)
		{
			if (chunks[i].isRebuildNeeded())
			{
				chunks[i].rebuild();
			}
		}
	}

	/**
	 * Returns an array list of all the surrounding axis aligned bounding boxes (AABBs) that
	 * will intersect with a given AABB
	 * 
	 * @param aabb
	 * @return ArrayList of AABBs
	 */
	public ArrayList<AABB> getCollidingBlocks(AABB aabb)
	{
		ArrayList<AABB> aabbs = new ArrayList<AABB>();
		int x0 = (int) aabb.x0;
		int x1 = (int) aabb.x1;
		int y0 = (int) aabb.y0;
		int y1 = (int) aabb.y1;
		int z0 = (int) aabb.z0;
		int z1 = (int) aabb.z1;

		for (int x = x0; x <= x1; x++)
		{
			for (int y = y0; y <= y1; y++)
			{
				for (int z = z0; z <= z1; z++)
				{
					int chunkPosition = Statics.untrustedWorldCoordsToChunkID(x, y, z);
					if (chunkPosition == -1)
						continue;

					byte block = getTrustedBlockType(chunkPosition, x, y, z);
					if (block == 0)
						continue;
					
					aabbs.add(new AABB(x, y, z, x + 1, y + 1, z + 1));
				}
			}
		}
		return aabbs;
	}

	/**
	 * @return the array of chunks belonging to world
	 */
	public Chunk[] getChunks()
	{
		return chunks;
	}
}
