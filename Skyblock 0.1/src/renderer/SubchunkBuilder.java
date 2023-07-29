package renderer;

import org.lwjgl.util.vector.Vector2f;

import block.Block;
import block.EFace;
import block.TerrainAssembler;
import worldgen.Statics;
import worldgen.Subchunk;

public class SubchunkBuilder
{
	private static int MAX_VERTEX_LENGTH = 12 * Statics.VERTEX_PER_CHUNK;

	private static VertexBuffer vertexBuffer = new VertexBuffer(MAX_VERTEX_LENGTH);
	private static Indexer indexer = new Indexer();
	private static IndexBuffer indexBuffer = new IndexBuffer(MAX_VERTEX_LENGTH);
	private static float[] vertices;

	private int vaoID, vboID, indicesID;
	private int indexCount;

	private int yOffset;

	/**
	 * Creates a new ChunkBuilder object with a registered vaoID, vboID, and indicesID
	 */
	public SubchunkBuilder()
	{
		vaoID = ResourceManager.createVAO();
		vboID = ResourceManager.createVBO();
		indicesID = ResourceManager.createVBO();
	}

	/**
	 * we return the vaoID to the chunk that controls this chunkbuilder so that the chunk canpass this 
	 * to the renderer
	 * 
	 * @return the vao registered to this chunkBuilder
	 */
	public int getVaoID()
	{
		return vaoID;
	}

	/**
	 * @return the number of indices in the interweaved bufferdata
	 */
	public int getIndexCount()
	{
		return indexCount;
	}

	/**
	 * clears all the buffers at once
	 */
	public void clearBuffers()
	{
		vertexBuffer.clear();
		indexer.clear();
		indexBuffer.clear();
	}

	public void rebuild(Subchunk[] subchunks)
	{	
		// prepare by clearing all buffers of previous data
		clearBuffers();
		
		// render subchunk by subchunk, skipping empty subchunks
		for (int i = 0; i < Statics.SUBCHUNK_NUM; i++)
		{
			if (subchunks[i].empty)
			{
				continue;
			}
				
			yOffset = i << 4;
			// for each subchunk go through each block 
			for (int x = 0; x < Statics.CHUNK_SIZE; x++)
			{
				for (int y = 0; y < Statics.CHUNK_SIZE; y++)
				{
					for (int z = 0; z < Statics.CHUNK_SIZE; z++)
					{
						// only renders solid, non-air blocks
						if (subchunks[i].getTrustedBlockType(x, y, z) > 0)
						{
							// Benchmark.benchmark.start("block");
							rebuildBlock(subchunks[i], x, y, z);
							// Benchmark.benchmark.stop("block");
						}
					}
				}
			}
		}

		interleaveData();
	}

	/**
	 * takes all the vertices stored in the vertice buffer and adds their elements one by one
	 * to a new temporary buffer called vertices. The data is now interleaved
	 */
	private void interleaveData()
	{
		// Benchmark.start("load to VBOs");
		int size = vertexBuffer.size();
		vertices = new float[5 * size];
		int pointer = 0;
		for (int i = 0; i < size; i++)
		{
			Vertex vertex = vertexBuffer.get(i);
			vertices[pointer++] = vertex.x;
			vertices[pointer++] = vertex.y;
			vertices[pointer++] = vertex.z;
			vertices[pointer++] = vertex.u;
			vertices[pointer++] = vertex.v;
		}

		int[] indices = indexBuffer.get();
		indexCount = indexBuffer.size();

		ResourceManager.buildInterleavedModel(vaoID, vboID, indicesID, vertices, indices);
		// Benchmark.stop("load to VBOs");
	}

	/**
	 * the specific subchunk that is being rendered, along side the chunk coordinates
	 * x, y, z range from 0-16 each independently. Checks whether each face should be rebuilt, then creates
	 * then rebuilds the faces that should be rebuilt.
	 * 
	 * @param subchunk
	 * @param x
	 * @param y
	 * @param z
	 */
	private void rebuildBlock(Subchunk subchunk, int x, int y, int z)
	{
		boolean[] shouldRender = new boolean[6];
		shouldRender[0] = shouldRenderFace(subchunk, x, y + 1, z);
		shouldRender[1] = shouldRenderFace(subchunk, x, y - 1, z);
		shouldRender[2] = shouldRenderFace(subchunk, x, y, z - 1);
		shouldRender[3] = shouldRenderFace(subchunk, x, y, z + 1);
		shouldRender[4] = shouldRenderFace(subchunk, x + 1, y, z);
		shouldRender[5] = shouldRenderFace(subchunk, x - 1, y, z);

		Block block = subchunk.getTrustedBlock(x, y, z);
		for (EFace face : EFace.values())
		{
			if (shouldRender[face.ordinal()])
			{
				rebuildBlockFace(block, x, y, z, face);
			}
		}
	}

	/**
	 * checks whether or not a specific block is null (or transparent), or outside the chunk
	 * if this is true, then we should render all the blocks with a face facing this block
	 * consider adding this to a precalculated list to avoid recalculating
	 * 
	 * @param subchunk
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean shouldRenderFace(Subchunk subchunk, int x, int y, int z)
	{
		if (Statics.untrustedWorldCoordsToInternalChunkID(x, y, z) == -1)
		{
			return true;
		}
		
		Block block = subchunk.getTrustedBlock(x, y, z);
		return block == null;
		// ? true : block.isSolid();
	}

	/**
	 * looks at a certain positions within and relative to a chunk, a certain face of that position, 
	 * and the block currently at that position, and adds the positional and textural vertex data
	 * associated with the points making up the two triangles that make up the square. 
	 * TODO add lighting by adding it as more information to the vertex data, will need to calculate this 
	 * information still (I have no idea how yet)
	 * 
	 * @param block
	 * @param x
	 * @param y
	 * @param z
	 * @param face
	 */
	private void rebuildBlockFace(Block block, int x, int y, int z, EFace face)
	{
		Vector2f uvCoords = block.getTexCoord(face);
		float u0 = uvCoords.x;
		float v0 = uvCoords.y;
		float u1 = u0 + TerrainAssembler.normalizedTextureSize;
		float v1 = v0 + TerrainAssembler.normalizedTextureSize;

		int x0 = x;
		int x1 = x0 + 1;

		int y0 = y + yOffset;
		int y1 = y0 + 1;

		int z0 = z;
		int z1 = z0 + 1;

		Vertex vertex0;
		Vertex vertex1;
		Vertex vertex2;
		Vertex vertex3;

		if (face == EFace.TOP)
		{
			vertex0 = new Vertex(x1, y1, z0, u0, v0);
			vertex1 = new Vertex(x0, y1, z0, u0, v1);
			vertex2 = new Vertex(x0, y1, z1, u1, v1);
			vertex3 = new Vertex(x1, y1, z1, u1, v0);

			rebuildQuad(vertex0, vertex1, vertex2, vertex3);
		} 
		else if (face == EFace.BOTTOM)
		{
			vertex0 = new Vertex(x1, y0, z1, u0, v0);
			vertex1 = new Vertex(x0, y0, z1, u0, v1);
			vertex2 = new Vertex(x0, y0, z0, u1, v1);
			vertex3 = new Vertex(x1, y0, z0, u1, v0);

			rebuildQuad(vertex0, vertex1, vertex2, vertex3);
		} 
		else if (face == EFace.NORTH)
		{
			vertex0 = new Vertex(x1, y1, z0, u0, v0);
			vertex1 = new Vertex(x1, y0, z0, u0, v1);
			vertex2 = new Vertex(x0, y0, z0, u1, v1);
			vertex3 = new Vertex(x0, y1, z0, u1, v0);

			rebuildQuad(vertex0, vertex1, vertex2, vertex3);
		}
		else if (face == EFace.SOUTH)
		{
			vertex0 = new Vertex(x0, y1, z1, u0, v0);
			vertex1 = new Vertex(x0, y0, z1, u0, v1);
			vertex2 = new Vertex(x1, y0, z1, u1, v1);
			vertex3 = new Vertex(x1, y1, z1, u1, v0);

			rebuildQuad(vertex0, vertex1, vertex2, vertex3);
		} 
		else if (face == EFace.EAST)
		{
			vertex0 = new Vertex(x1, y1, z1, u0, v0);
			vertex1 = new Vertex(x1, y0, z1, u0, v1);
			vertex2 = new Vertex(x1, y0, z0, u1, v1);
			vertex3 = new Vertex(x1, y1, z0, u1, v0);

			rebuildQuad(vertex0, vertex1, vertex2, vertex3);
		} 
		else if (face == EFace.WEST)
		{
			vertex0 = new Vertex(x0, y1, z0, u0, v0);
			vertex1 = new Vertex(x0, y0, z0, u0, v1);
			vertex2 = new Vertex(x0, y0, z1, u1, v1);
			vertex3 = new Vertex(x0, y1, z1, u1, v0);

			rebuildQuad(vertex0, vertex1, vertex2, vertex3);
		}
	}
	
	/**
	 * Given 4 vertices, assembles them into a face while adding that information in the correct
	 * order to a vertex buffer
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	private void rebuildQuad(Vertex v0, Vertex v1, Vertex v2, Vertex v3)
	{
		// v0, v1, v3 (triangle 1)
		// v3, v1, v2 (triangle 2)
		// adds the triangles in a counterclockwise rotation
		// so that their normals face outwards
		
		vertexBuffer.add(v0);
		vertexBuffer.add(v1);
		vertexBuffer.add(v3);
		vertexBuffer.add(v2);

		indexBuffer.add(indexer.get());
		int i1 = indexer.get();
		int i3 = indexer.get();
		indexBuffer.add(i1);
		indexBuffer.add(i3);
		indexBuffer.add(i3);
		indexBuffer.add(i1);

		indexBuffer.add(indexer.get());
	}
}