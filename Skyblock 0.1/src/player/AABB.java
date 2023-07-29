package player;

import org.lwjgl.util.vector.Vector3f;

/**
 * Represents an axis aligned bounding box (AABB)
 * @author aidan
 *
 */
public class AABB
{
	public float x0, y0, z0, x1, y1, z1;

	/**
	 * Default constructor with all fields initialized to 0
	 */
	public AABB()
	{
		new AABB(0, 0, 0, 0, 0, 0);
	}

	/**
	 * initializes a bounding box to correspond to a block at a specific position
	 * @param block
	 */
	public AABB(Vector3f block)
	{
		x0 = block.x;
		y0 = block.y;
		z0 = block.z;
		x1 = block.x + 1;
		y1 = block.y + 1;
		z1 = block.z + 1;
	}

	/**
	 * initializes a bounding box with fields initialized to the respective parameters
	 * @param x0
	 * @param y0
	 * @param z0
	 * @param x1
	 * @param y1
	 * @param z1
	 */
	public AABB(float x0, float y0, float z0, float x1, float y1, float z1)
	{
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}

	/**
	 * updates every field to the respective parameter
	 * @param x0
	 * @param y0
	 * @param z0
	 * @param x1
	 * @param y1
	 * @param z1
	 */
	public void set(float x0, float y0, float z0, float x1, float y1, float z1)
	{
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}

	/**
	 * expands the size of the bounding box in the direction of vector constituted by the parameters
	 * @param xa
	 * @param ya
	 * @param za
	 * @return
	 */
	public AABB expand(float xa, float ya, float za)
	{
		float _x0 = this.x0;
		float _y0 = this.y0;
		float _z0 = this.z0;
		float _x1 = this.x1;
		float _y1 = this.y1;
		float _z1 = this.z1;

		if (xa < 0.0F)
		{
			_x0 += xa;
		}
		else if (xa > 0.0F)
		{
			_x1 += xa;
		}

		if (ya < 0.0F)
		{
			_y0 += ya;
		} 
		else if (ya > 0.0F)
		{
			_y1 += ya;
		}

		if (za < 0.0F)
		{
			_z0 += za;
		} 
		else if (za > 0.0F)
		{
			_z1 += za;
		}

		return new AABB(_x0, _y0, _z0, _x1, _y1, _z1);
	}

	/**
	 * moves the bounding box by the parameters in their respective dimensions
	 * @param xa
	 * @param ya
	 * @param za
	 */
	public void move(float xa, float ya, float za)
	{
		x0 += xa;
		y0 += ya;
		z0 += za;
		x1 += xa;
		y1 += ya;
		z1 += za;
	}

	/**
	 * Determines if this AABB will collide with c, another AABB, if moved by xDist in the x direction 
	 * @param c
	 * @param xDist
	 * @return the maximum distance this can move in the x direction
	 */
	public float clipXCollide(AABB c, float xDist)
	{
		if (equalYLevel(c) && equalZLevel(c))
		{
			if (xDist > 0 && c.x1 <= this.x0)
			{
				float maxDist = this.x0 - c.x1;
				if (maxDist < xDist)
				{
					xDist = maxDist;
				}
			}

			if (xDist < 0 && c.x0 >= this.x1)
			{
				float maxDist = this.x1 - c.x0;
				if (maxDist > xDist)
				{
					xDist = maxDist;
				}
			}
		}
		return xDist;
	}
	/**
	 * Determines if this AABB will collide with c, another AABB, if moved by yDist in the y direction
	 * @param c
	 * @param yDist
	 * @return the maximum distance this can move in the z direction
	 */
	public float clipYCollide(AABB c, float yDist)
	{
		if (equalXLevel(c) && equalZLevel(c))
		{
			if (yDist > 0 && c.y1 <= this.y0)
			{
				float maxDist = this.y0 - c.y1;
				if (maxDist < yDist)
				{
					yDist = maxDist;
				}
			}

			if (yDist < 0 && c.y0 >= this.y1)
			{
				float maxDist = this.y1 - c.y0;
				if (maxDist > yDist)
				{
					yDist = maxDist;
				}
			}
		}
		return yDist;
	}

	/**
	 * Determines if this AABB will collide with c, another AABB, if moved by zDist in the z direction
	 * @param c
	 * @param zDist
	 * @return the maximum distance this can move in the z direction
	 */
	public float clipZCollide(AABB c, float zDist)
	{
		if (equalXLevel(c) && equalYLevel(c))
		{
			if (zDist > 0 && c.z1 <= this.z0)
			{
				float maxDist = this.z0 - c.z1;
				if (maxDist < zDist)
				{
					zDist = maxDist;
				}
			}

			if (zDist < 0 && c.z0 >= this.z1)
			{
				float maxDist = this.z1 - c.z0;
				if (maxDist > zDist)
				{
					zDist = maxDist;
				}
			}
		}
		return zDist;
	}

	/**
	 * 
	 * @param c
	 * @return whether or not this AABB and c intersect each other
	 */
	public boolean intersects(AABB c)
	{
		return equalXLevel(c) && equalYLevel(c) && equalZLevel(c);
	}

	/**
	 * 
	 * @return the string implementation of this class, displaying all the fields, including their names
	 * and values
	 */
	public String toString()
	{
		return "[x0: " + x0 + ", y0: " + y1 + ", z0: " + z0 + ", x1: " + x1 + ", y1: " + y1 + ", z1: " + z1 + "]";
	}

	/**
	 * 
	 * @param c
	 * @return whether or not this AABB and c overlap in their projections onto the YZ plane
	 */
	private boolean equalXLevel(AABB c)
	{
		return c.x1 > this.x0 && c.x0 < this.x1;
	}

	/**
	 * 
	 * @param c
	 * @return whether or not this AABB and c overlap in their projections onto the XZ plane
	 */
	private boolean equalYLevel(AABB c)
	{
		return c.y1 > this.y0 && c.y0 < this.y1;
	}

	/**
	 * 
	 * @param c
	 * @return whether or not this AABB and c overlap in their projections onto the XY plane
	 */
	private boolean equalZLevel(AABB c)
	{
		return c.z1 > this.z0 && c.z0 < this.z1;
	}
}
