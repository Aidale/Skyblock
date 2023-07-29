package worldgen;

import java.util.Random;

import block.RegistryBlock;

public class WorldGenerator
{
	// dimensions in chunks
	// width and height refer respectively to the number of subchunks along the x (and z) axis, and height
	// refers to the number of subchunks along the y axis
	private int seed, numOctaves;
	
	private Random rand;
	private Perlin2D[] octaves;

	private int[][] heightMap;

	public WorldGenerator(int seed)
	{
		this.seed = seed;
		numOctaves = (int)(Math.log(Statics.CHUNK_SIZE) / Math.log(2));
		System.out.println("numOctaves: " + numOctaves);
		octaves = new Perlin2D[numOctaves];
			
		rand = new Random(seed);
	}

	public void setupPerlinNoise()
	{
		float chunkSize = Statics.CHUNK_SIZE;
		float chunkCount = Statics.CHUNK_NUM;
		float amplitude = 1;
		for (int i = 0; i < numOctaves; i++)
		{
			octaves[i] = new Perlin2D(seed + i, (int)chunkCount, (int)chunkSize, amplitude);
			
			chunkSize *= 0.5;
			chunkCount *= 2;
			
			amplitude *= 0.5;
		}
		
		Perlin2D finalMap = new Perlin2D(octaves);
		finalMap.smooth();
		heightMap = finalMap.getHeightMap();
	}	
	
	public byte[] getChunk(int x, int z)
	{
		int xOffset = x * Statics.CHUNK_SIZE, zOffset = z * Statics.CHUNK_SIZE;
		byte[] chunk = getEmptyByteChunk();
		for (int cx = 0; cx < Statics.CHUNK_SIZE; cx++)
		{
			for (int cz = 0; cz < Statics.CHUNK_SIZE; cz++)
			{
				int height = heightMap[cx + xOffset][cz + zOffset];
				if (height < 14) height = 14;
				if (height > 128) height = 128;
				
				if (height == 14)
				{
					for (int y = 0; y < 14; y++)
					{
						chunk[Statics.trustedWorldCoordsToInternalChunkID(cx, y, cz)] = (byte) RegistryBlock.SAND;
					}
					continue;
				}
				
				for (int y = 0; y < height - 4; y++)
				{
					chunk[Statics.trustedWorldCoordsToInternalChunkID(cx, y, cz)] = (byte) RegistryBlock.STONE;
				}
				for (int y = height - 4; y < height - 1; y++)
				{
					chunk[Statics.trustedWorldCoordsToInternalChunkID(cx, y, cz)] = (byte) RegistryBlock.DIRT;
				}
				
				chunk[Statics.trustedWorldCoordsToInternalChunkID(cx, height - 1, cz)] = (byte) RegistryBlock.GRASS;
			
			}
		}
		return chunk;
	}

	public byte[] generateBlock()
	{
		byte[] blocks = getEmptyByteChunk();
		blocks[0] = (byte) RegistryBlock.STONE;
		return blocks;
	}

	public byte[] generateSolidChunk()
	{
		byte[] blocks = getEmptyByteChunk();
		for (int i = 0; i < blocks.length; i++)
		{
			blocks[i] = 1;
		}
		return blocks;
	}

	public byte[] generateRandomChunk()
	{
		byte[] blocks = getEmptyByteChunk();
		for (int i = 0; i < blocks.length; i++)
		{
			blocks[i] = (byte) (Math.random() > ((i / 256) / 128.0) ? 1 : 0);
		}
		return blocks;
	}

	public byte[] generateGradientChunk()
	{
		byte[] blocks = getEmptyByteChunk();
		for (int i = 0; i < blocks.length; i++)
		{
			int y = i / 256;
			int z = (i / 16) % 16;
			int x = i % 16;

//	    if ((x - 8) * (x - 8) - 3 * y - (z - 8) * (z - 8) + 20 > 0)
//	    {
//		blocks[i] = Math.random() > 0.5 ? (byte)1 : (byte)2;
//	    }

			if (Math.abs(x - 8) + Math.abs(z - 8) - y + 4 == 0)
			{
				blocks[i] = rand(3);
			}
			if (y == 23 || y == 26 || y < 2)
			{
				blocks[i] = rand(3);
			}
//	    if (x + y + z == 20) blocks[i] = 1;  

//	    System.out.println("x: " + x + ", y: " + y + ", z: " + z);
		}
		return blocks;
	}

	public static byte rand(int range)
	{
		return (byte) (Math.random() * range + 1);
	}

	private static byte[] getEmptyByteChunk()
	{
		return new byte[Statics.CHUNK_SIZE * Statics.CHUNK_SIZE * Statics.CHUNK_SIZE * Statics.SUBCHUNK_NUM];
	}
}
