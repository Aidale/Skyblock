package particle;

import java.util.ArrayList;

public class ParticleSystem
{
	public ArrayList<Particle> particles;
	public boolean empty;
	
	public ParticleSystem()
	{
		particles = new ArrayList<>();
	}

	public void tick()
	{
		for (int i = 0; i < particles.size(); i++)
		{
			particles.get(i).tick();
			if (particles.get(i).removed)
			{
				particles.remove(i--);
			}
		}
		if (particles.size() == 0) empty = true;
	}
}
