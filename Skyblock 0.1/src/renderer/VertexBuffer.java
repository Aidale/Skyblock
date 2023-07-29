package renderer;

public class VertexBuffer
{
	private Vertex[] vertices;
	private int pointer;
	
	/**
	 * creates a new VertexBuffer with length bufferLength
	 * @param bufferLength
	 */
	public VertexBuffer(int bufferLength)
	{
		vertices = new Vertex[bufferLength];
		pointer = 0;
	}
	
	/**
	 * adds a vertex v to the first empty space in the buffer
	 * @param v
	 */
	public void add(Vertex v)
	{
		vertices[pointer++] = v;
	}
	
	/**
	 * @param index
	 * @return the vertex in the buffer at the index given
	 */
	public Vertex get(int index)
	{
		return vertices[index];
	}
	
	/**
	 * @return the size of the buffer
	 */
	public int size()
	{
		return pointer;
	}
	
	/**
	 * resets the buffer, allowing it to be overwritten
	 */
	public void clear()
	{
		pointer = 0;
	}
}
