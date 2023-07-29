package worldgen;

import renderer.SubchunkBuilder;
import utils.Utilities;
/**
 * Stores 8 subchunks, in the future will store entities. Each chunk is registered with a chunkBuilder
 * @author aidan
 *
 */
public class Chunk
{
	private Subchunk[] subchunks;
	private SubchunkBuilder chunkBuilder;
	private boolean rebuildNeeded;

	public final int vaoID;
	public final int chunkX, chunkZ;

	/**
	 * Sets the chunks coordinates to x, z. Creates all the subchunks. Creates the chunkBuilder and
	 * sets the chunks vaoID to the one created by the chunkbuilder
	 * @param x
	 * @param z
	 */
	public Chunk(int x, int z)
	{
		this.chunkX = x;
		this.chunkZ = z;
		rebuildNeeded = true;
		subchunks = new Subchunk[Statics.SUBCHUNK_NUM];
		for (int i = 0; i < subchunks.length; i++)
		{
			subchunks[i] = new Subchunk();
		}

		chunkBuilder = new SubchunkBuilder();
		vaoID = chunkBuilder.getVaoID();
	}

	/**
	 * recreates all the vertex data after the chunk has been modified
	 */
	public void rebuild()
	{
		rebuildNeeded = false;
		chunkBuilder.rebuild(subchunks);
	}

	/**
	 * takes in a list of block types and sets the subchunks block values to those block types
	 * @param blocks
	 */
	public void instantiate(byte[] blocks)
	{
		for (int i = 0; i < Statics.SUBCHUNK_NUM; i++)
		{
			byte[] sub = new byte[Statics.BLOCKS_PER_SUBCHUNK];
			System.arraycopy(blocks, i * Statics.BLOCKS_PER_SUBCHUNK, sub, 0, Statics.BLOCKS_PER_SUBCHUNK);

			boolean empty = Utilities.allZeros(sub);
			subchunks[i].empty = empty;
			if (!empty)
			{
				subchunks[i].setBlocks(sub);
			}
		}
	}

	/**
	 * returns the block type at the world coordinates
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the block type at the world coordinates
	 */
	public byte getTrustedBlockType(int x, int y, int z)
	{
		return subchunks[y >> 4].getTrustedBlockType(x, y & 15, z);
	}

	/**
	 * sets block at the world coordinate x, y, z to block type
	 * 
	 * @param block
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTrustedBlockType(byte block, int x, int y, int z)
	{
		rebuildNeeded = subchunks[y >> 4].setTrustedBlockType(block, x, y & 15, z);
	}

	/**
	 * @return the number of indices in the chunk
	 */
	public int getChunkBuilderIndexCount()
	{
		return chunkBuilder.getIndexCount();
	}
	
	/**
	 * @return the vaoID of the model corresponding to this chunk
	 */
	public int getChunkBuilderVaoID()
	{
		return chunkBuilder.getVaoID();
	}

	/**
	 * @return whether or not a rebuild is needed for this chunk
	 */
	public boolean isRebuildNeeded()
	{
		return rebuildNeeded;
	}
}
