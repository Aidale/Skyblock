package worldgen;

import block.Block;
import block.RegistryBlock;
import utils.Utilities;

public class Subchunk
{
	// 16x16x16 array of blocks, 4096 blocks in total
	private byte[] blocks;
	public boolean empty;

	// TODO implement loading a world from a file

	/**
	 * constructor currently has no implementation
	 */
	public Subchunk()
	{

	}

	/**
	 * sets the blocks in the subchunk to the parameter blocks 
	 * @param blocks 
	 */
	public void setBlocks(byte[] blocks)
	{
		empty = false;
		this.blocks = blocks;
	}

	/**
	 * returns the blocks in the chunk
	 */
	public byte[] getBlocks()
	{
		if (empty)
			return null;
		return blocks;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return the block type at world coordinates x, y, z
	 */
	public byte getTrustedBlockType(int x, int y, int z)
	{
		if (empty)
			return 0;
		int subchunkPosition = Statics.trustedWorldCoordsToInternalChunkID(x, y, z);
		return blocks[subchunkPosition];
	}

	/**
	 * sets the block at world coordinates x, y, z to block type. 
	 * 
	 * @param block
	 * @param x
	 * @param y
	 * @param z
	 * @return whether or not the chunk was changed
	 */
	public boolean setTrustedBlockType(byte block, int x, int y, int z)
	{
		// if the subchunk is empty and we are placing a block in it
		if (block != 0 && blocks == null)
		{
			blocks = new byte[Statics.BLOCKS_PER_SUBCHUNK];
			empty = false;
		}

		int position = Statics.trustedWorldCoordsToInternalChunkID(x, y, z);
		// either destroying or placing a block
		if ((blocks[position] == 0 && block != 0) || (blocks[position] != 0 && block == 0))
		{
			blocks[position] = block;
			// if the subchunk becomes empty after we destroy a block
			if (block == 0 && Utilities.allZeros(blocks))
			{
				empty = true;
				blocks = null;
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the actual block object at world coordinates x, y, z
	 */
	public Block getTrustedBlock(int x, int y, int z)
	{
		return RegistryBlock.registry[getTrustedBlockType(x, y, z)];
	}
}
