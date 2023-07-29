package block;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class RegistryBlock
{
	// possibly turn block ids from bytes into ints (or shorts)
	public static int AIR = 0;
	public static int STONE = 1;
	public static int GRASS = 2;
	public static int DIRT = 3;
	public static int BEDROCK = 4;
	public static int SAND = 5;
	public static int GRAVEL = 6;

	public static Block[] registry;
	public static HashMap<String, Integer> index;

	protected static LinkedHashMap<String, Block> blockList;

	public static void registerBlocks()
	{
		createBlockList();
		TerrainAssembler.assembleTerrain();
		registry = new Block[blockList.size()];
		index = new HashMap<>();

		int i = 0;
		for (Block block : blockList.values())
		{
			registry[i++] = block;
		}
		i = 0;
		for (String name : blockList.keySet())
		{
			index.put(name, i++);
		}
	}

	private static void createBlockList()
	{
		blockList = new LinkedHashMap<>();

		blockList.put("air", null);
		blockList.put("stone", new BlockStone());
		blockList.put("grass", new BlockGrass());
		blockList.put("dirt", new BlockDirt());
		blockList.put("bedrock", new BlockBedrock());
		blockList.put("sand", new BlockSand());
		blockList.put("gravel", new BlockGravel());
		blockList.put("water", new Block());
	}
}
