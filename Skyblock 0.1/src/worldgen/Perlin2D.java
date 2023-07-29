package worldgen;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import utils.Utilities;

public class Perlin2D
{
	private int[][] heightMap;
	
	private int chunkCount, chunkSize, mapSize;
	private float amplitude;
	private Random random;

	public Perlin2D(int seed, int chunkCount, int chunkSize, float amplitude)
	{
		this.chunkCount = chunkCount;
		this.chunkSize = chunkSize;
		this.mapSize = chunkCount * chunkSize;
		this.amplitude = amplitude;
		random = new Random(seed);

		heightMap = new int[mapSize][mapSize];
		
		generateMap();
	}
	
	public Perlin2D(Perlin2D[] maps)
	{
		int minMapSize = Integer.MAX_VALUE;
		for (int i = 0; i < maps.length; i++)
		{
			System.out.println(maps[i].mapSize);
			if (maps[i].mapSize < minMapSize)
			{
				minMapSize = maps[i].mapSize;
			}
		}
		
		mapSize = minMapSize;
		heightMap = new int[minMapSize][minMapSize];
		for (int i = 0; i < maps.length; i++)
		{
			for (int x = 0; x < minMapSize; x++)
			{
				for (int y = 0; y < minMapSize; y++)
				{
					heightMap[x][y] += maps[i].heightMap[x][y]; 
				}
			}
		}
	}
	
	public static void printMap(int[][] map)
	{
		for (int x = 0; x < map.length; x++)
		{
			for (int y = 0; y < map[0].length; y++)
			{
				System.out.print(map[x][y]);
			}
			System.out.println();
		}
	}

	private void generateMap()
	{
		float minValue = Float.MAX_VALUE;
		float maxValue = Float.MIN_VALUE;
		float[][] rawHeightMap = new float[mapSize][mapSize];
		
		// generate the chunk vectors
		float[][] chunkVectors = new float[chunkCount][chunkCount];
		for (int x = 0; x < chunkCount; x++)
		{
			for (int y = 0; y < chunkCount; y++)
			{
				chunkVectors[x][y] = (float) (2 * Math.PI * random.nextFloat());
			}
		}
		
		// generate the raw height values
		for (int x = 0; x < chunkCount; x++)
		{
			for (int y = 0; y < chunkCount; y++)
			{
				Vector2f gradVec00 = Utilities.toCartesian(chunkVectors[x][y]),
						 gradVec01 = Utilities.toCartesian(chunkVectors[x][(y + 1) % chunkCount]),
						 gradVec11 = Utilities.toCartesian(chunkVectors[(x + 1) % chunkCount][(y + 1) % chunkCount]),
						 gradVec10 = Utilities.toCartesian(chunkVectors[(x + 1) % chunkCount][y]);

				for (int cx = 0; cx < chunkSize; cx++)
				{
					for (int cy = 0; cy < chunkSize; cy++)
					{
						float xCor = (float) cx / chunkSize, xFade = fade(xCor), yCor = (float) cy / chunkSize, yFade = fade(yCor);

						Vector2f distVec00 = new Vector2f(xCor, yCor), 
								 distVec01 = new Vector2f(xCor, yCor - 1),
								 distVec11 = new Vector2f(xCor - 1, yCor - 1), 
								 distVec10 = new Vector2f(xCor - 1, yCor);

						float dotProd00 = Vector2f.dot(distVec00, gradVec00),
							  dotProd01 = Vector2f.dot(distVec01, gradVec01),
							  dotProd11 = Vector2f.dot(distVec11, gradVec11),
							  dotProd10 = Vector2f.dot(distVec10, gradVec10);

						float average = lerp(lerp(dotProd00, dotProd01, yFade), lerp(dotProd10, dotProd11, yFade), xFade);
						rawHeightMap[x * chunkSize + cx][y * chunkSize + cy] = average;

						if (minValue > average)
						{
							minValue = average;
						}	
						if (maxValue < average)
						{
							maxValue = average;
						}
					}
				}
			}
		}

		// unlerp the values
		for (int x = 0; x < mapSize; x++)
		{
			for (int y = 0; y < mapSize; y++)
			{
				 heightMap[x][y] = (int)(amplitude * Math.round(unlerp(minValue, maxValue, rawHeightMap[x][y]) * Statics.MAX_HEIGHT)) / 8 + 1;
				//heightMap[x][y] = (int)(amplitude * Math.round(unlerp(minValue, maxValue, rawHeightMap[x][y]) * Statics.MAX_HEIGHT));
			}
		}
	}
	
	public void amplify(int amplificationFactor)
	{
		for (int x = 0; x < mapSize; x++)
		{
			for (int y = 0; y < mapSize; y++)
			{
				heightMap[x][y] *= amplificationFactor;
			}
		}
	}
	
	public void smooth()
	{
		int[][] copy = new int[mapSize][mapSize];
		for (int x = 0; x < mapSize; x++)
		{
			for (int y = 0; y < mapSize; y++)
			{
				int total = 0;
				int count = 0;
				for (int dx = -1; dx <= 1; dx++)
				{
					for (int dy = -1; dy <= 1; dy++)
					{
						try
						{
							total += heightMap[x + dx][y + dy];
							count++;
						}
						catch (IndexOutOfBoundsException e)
						{
							
						}
					}
				}
				copy[y][x] = (int)(total / count);
			}
		}
		
		// if any heightValue is surrounded with equal but different heightValues
		for (int x = 0; x < mapSize; x++)
		{
			for (int y = 0; y < mapSize; y++)
			{
				boolean firstChecked = true;
				boolean allEqual = true;
				int allEqualTo = 0;
				for (int dx = -1; dx <= 1 && allEqual; dx++)
				{
					for (int dy = -1; dy <= 1 && allEqual; dy++)
					{
						try
						{
							if (firstChecked)
							{
								allEqualTo = heightMap[x + dx][y + dy];
								firstChecked = false;
								// in the case that all nine blocks are equal, then no need to do anything
								if (allEqualTo == heightMap[x][y])
								{
									allEqual = false;
								}
							}
							else
							{
								allEqual = (heightMap[x + dx][y + dy] == allEqualTo);
							}
						}
						catch (IndexOutOfBoundsException e)
						{
							
						}
					}
				}
				
				if (allEqual)
				{
					copy[x][y] = allEqualTo;
				}
			}
		}
		
		heightMap = copy;
	}

	public int[][] getHeightMap()
	{
		return heightMap;
	}

	private float fade(float a)
	{
		return a * a * a * (a * (a * 6 - 15) + 10);
	}

	private float lerp(float x, float y, float input)
	{
		return x + (y - x) * input;
	}

	private float unlerp(float x, float y, float input)
	{
		return (input - x) / (y - x);
	}
}
