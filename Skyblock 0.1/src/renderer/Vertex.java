package renderer;

public class Vertex
{
	public int x, y, z;
	public float u, v;
	// eventually need to add lighting information here
	
	/**
	 * Creates a new vertex object with position x, y, z and texture coords u, v
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 */
	public Vertex(int x, int y, int z, float u, float v)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = u;
		this.v = v;
	}
}
