package particle;

import org.lwjgl.util.vector.Vector3f;

import player.Entity;
import worldgen.World;

public class Particle extends Entity
{
	public int lifetime, age;
	public boolean removed;

	public Particle(World world, Vector3f position, Vector3f velocity, int lifetime)
	{
		super(world, position, velocity);
		this.lifetime = lifetime;
		age = 0;
	}

	public void tick()
	{
		if (age++ >= lifetime)
		{
			removed = true;
			return;
		}

		createNaturalVelocity();
		collisionDetection();
		setPosition();
	}

}