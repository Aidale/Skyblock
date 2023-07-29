package player;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import worldgen.World;

public class Entity
{
    protected static final float deceleration = 0.96f,
		       flySpeed = 0.02f, 
		       walkSpeed = 0.04f, 
		       airSpeed = 0.008f;
    
    protected Vector3f position, velocity;
    protected World world;
    protected AABB aabb;
    protected boolean onGround;
    protected float AABB_X, AABB_Y, AABB_Z;
    
    public Entity(World world, Vector3f position, Vector3f velocity)
    {
	this.world = world;
	this.position = position;
	this.velocity = velocity;
	
	aabb = new AABB();
	
    }
    
    public void tick()
    {
	
    }
    
    protected void createNaturalVelocity()
    {	    
	//gravity constant
	velocity.y -= 0.01;
	    
	float drag = onGround ? 0.637f : 0.91f;
	velocity.x *= drag;
	velocity.y *= 0.98;
	velocity.z *= drag;
    }
    
    protected void collisionDetection()
    {
	//collision detection
        ArrayList<AABB> aabbs = world.getCollidingBlocks(aabb.expand(velocity.x, velocity.y, velocity.z));
        
        float v_x = velocity.x;
        float v_y = velocity.y;
        float v_z = velocity.z;
        
        for (int i = 0; i < aabbs.size(); i++)
        {
            v_y = aabbs.get(i).clipYCollide(aabb, v_y);
        }
        aabb.move(0, v_y, 0);
        
        for (int i = 0; i < aabbs.size(); i++)
        {
            v_x = aabbs.get(i).clipXCollide(aabb, v_x);
        }
        aabb.move(v_x, 0, 0);
        
        for (int i = 0; i < aabbs.size(); i++)
        {
            v_z = aabbs.get(i).clipZCollide(aabb, v_z);
        }
        aabb.move(0, 0, v_z);
        
        onGround = velocity.y != v_y && velocity.y < 0.0F;
        if (velocity.x != v_x)
        {
            velocity.x = 0;
        }
        if (velocity.y != v_y)
        {
            velocity.y = 0;
        }
        if (velocity.z != v_z)
        {
            velocity.z = 0;
        }
    }
    
    protected void setPosition()
    {
	position.x = (aabb.x0 + aabb.x1) / 2.0F;
	position.y = (aabb.y0 + aabb.y1) / 2.0F;
	position.z = (aabb.z0 + aabb.z1) / 2.0F;
    }
}
