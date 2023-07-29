package player;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import block.EFace;
import font.Character;
import font.TextGUI;
import input.MouseInput;
import utils.Timer;
import worldgen.Statics;
import worldgen.World;

public class Player extends Entity
{
	private static final int TOGGLE_SPEED = 200, REACH = 5;

	private static final float jumpSpeed = 0.17f, HEIGHT_OFFSET = 1.62f;

	private Vector3f respawn;
	public Camera camera;

	private EGamemode gamemode;

	private Timer escTimer;
	private Timer flyTimer;

	public TargetedFrame targetedFrame;
	private DebugMenu f3;

	private static int gcTimer = 0;
	
	public Player(World world, Vector3f position, Vector3f velocity, float pitch, float yaw)
	{
		super(world, position, velocity);

		this.respawn = new Vector3f(position);
		camera = new Camera(position, pitch, yaw);
		gamemode = EGamemode.SURVIVAL;
		targetedFrame = new TargetedFrame();
		f3 = new DebugMenu(this);

		new TextGUI(Character.CROSSHAIR, Display.getWidth() / 2, Display.getHeight() / 2, 3, true);

		escTimer = new Timer(TOGGLE_SPEED);
		flyTimer = new Timer(TOGGLE_SPEED);

		AABB_X = 0.6f;
		AABB_Y = 1.8f;
		AABB_Z = 0.6f;
		setAABB();
	}

	private void setAABB()
	{
		aabb.set(position.x - AABB_X / 2, position.y - HEIGHT_OFFSET, position.z - AABB_Z / 2, position.x + AABB_X / 2,
				position.y + AABB_Y - HEIGHT_OFFSET, position.z + AABB_Z / 2);
	}

	private void rotate(Vector2f vector)
	{
		if (vector != null)
			camera.updateRotation(vector);
	}

	public void tick()
	{
		camera.updateViewMatrix();
		rotate(MouseInput.getMouseVector());
		keyboardInput();
		getTargetedBlock();
		mouseInput();
		f3.update();
	}

	private void mouseInput()
	{
		Vector3f target = targetedFrame.target;
		while (Mouse.next())
		{
			if (target == null)
				continue;

			if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState())
			{
				world.setBlock((byte) 0, target);
			}
			if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState())
			{
				Vector3f placeBlock = null;
				if (targetedFrame.face == EFace.WEST)
				{
					placeBlock = new Vector3f(target.x - 1, target.y, target.z);
				} else if (targetedFrame.face == EFace.EAST)
				{
					placeBlock = new Vector3f(target.x + 1, target.y, target.z);
				} else if (targetedFrame.face == EFace.BOTTOM)
				{
					placeBlock = new Vector3f(target.x, target.y - 1, target.z);
				} else if (targetedFrame.face == EFace.TOP)
				{
					placeBlock = new Vector3f(target.x, target.y + 1, target.z);
				} else if (targetedFrame.face == EFace.NORTH)
				{
					placeBlock = new Vector3f(target.x, target.y, target.z - 1);
				} else if (targetedFrame.face == EFace.SOUTH)
				{
					placeBlock = new Vector3f(target.x, target.y, target.z + 1);
				}

				AABB placeBB = new AABB(placeBlock);

				if (placeBB.intersects(aabb) || placeBlock == null)
				{
					return;
				}

				world.setBlock((byte) 1, placeBlock);
			}
		}
	}

	private void keyboardInput()
	{
		int v_ws = 0, v_da = 0, v_y = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			v_ws++;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			v_da++;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			v_da--;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			v_ws--;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
		{
			v_y--;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			v_y++;
		}
		
		
		if (Keyboard.isKeyDown(Keyboard.KEY_G) && gcTimer++ > 60)
		{
			System.out.println("Garbage Collection");
			System.gc();
			gcTimer = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_F) && flyTimer.ready())
		{
			if (gamemode == EGamemode.CREATIVE)
				gamemode = EGamemode.SURVIVAL;
			else
				gamemode = EGamemode.CREATIVE;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && escTimer.ready())
		{
			if (MouseInput.mouseGrabbed)
				MouseInput.releaseMouse();
			else
				MouseInput.grabMouse();
		}

		if (gamemode == EGamemode.CREATIVE)
		{
			applyPlayerMovement(v_ws, v_da, flySpeed);
			createFlyingVelocity(v_y);
		} else if (gamemode == EGamemode.SURVIVAL)
		{
			applyPlayerMovement(v_ws, v_da, onGround ? walkSpeed : airSpeed);
			if (v_y == 1 && onGround)
				velocity.y = jumpSpeed;
			createNaturalVelocity();
		}

		collisionDetection();
		setPosition();
		if (position.y < 0)
			respawn();
	}

	private void applyPlayerMovement(int v_ws, int v_da, float speed)
	{
		if (v_ws != 0 || v_da != 0)
		{
			float dist = speed / (float) Math.sqrt(v_ws * v_ws + v_da * v_da);

			float rad = (float) (Math.toRadians(camera.yaw));
			float sin = (float) Math.sin(rad);
			float cos = (float) Math.cos(rad);

			velocity.x += dist * (v_da * cos + v_ws * sin);
			velocity.z += dist * (v_da * sin - v_ws * cos);
		}
	}

	private void createFlyingVelocity(int v_y)
	{
		if (v_y != 0)
			velocity.y = v_y * flySpeed * 6;

		velocity.x *= deceleration * 0.97;
		velocity.y *= deceleration * 0.9;
		velocity.z *= deceleration * 0.97;
	}

	protected void setPosition()
	{
		// move
		position.x = (aabb.x0 + aabb.x1) / 2.0F;
		position.y = aabb.y0 + HEIGHT_OFFSET;
		position.z = (aabb.z0 + aabb.z1) / 2.0F;
	}

	private void respawn()
	{
		position.x = respawn.x;
		position.y = respawn.y;
		position.z = respawn.z;
		setAABB();
	}

	private void getTargetedBlock()
	{
		targetedFrame.target = null;
		targetedFrame.face = null;

		int x = 0, y = 1, z = 2;
		int[] d = new int[3];
		int[] rel = new int[3];
		float[] t = new float[3];
		float[] dt = new float[3];

		float[] posArr = new float[] { position.x, position.y, position.z };
		float[] rayArr = new float[] { camera.ray.x, camera.ray.y, camera.ray.z };

		for (int i = 0; i < 3; i++)
		{
			rayArr[i] = (float) (Math.round(rayArr[i] * 1000) / 1000.0);
			if (rayArr[i] > 0)
			{
				dt[i] = 1 / rayArr[i];
				t[i] = ((float) Math.ceil(posArr[i]) - posArr[i]) * dt[i];
				d[i] = 1;
			} 
			else if (rayArr[i] < 0)
			{
				dt[i] = -1 / rayArr[i];
				t[i] = (posArr[i] - (float) Math.floor(posArr[i])) * dt[i];
				d[i] = -1;
			} 
			else
			{
				dt[i] = Float.MAX_VALUE;
				t[i] = Float.MAX_VALUE;
			}
		}

		int reachSqr = REACH * REACH;
		EFace face = null;
		while (rel[x] * rel[x] + rel[y] * rel[y] + rel[z] * rel[z] <= reachSqr)
		{
			if (t[x] < t[y] && t[x] < t[z])
			{
				// x is the smallest
				rel[x] += d[x];
				t[x] += dt[x];
				if (rayArr[x] > 0)
					face = EFace.WEST;
				else if (rayArr[x] < 0)
					face = EFace.EAST;
			}
			else if (t[y] < t[x] && t[y] < t[z])
			{
				// y is the smallest
				rel[y] += d[y];
				t[y] += dt[y];
				if (rayArr[y] > 0)
					face = EFace.BOTTOM;
				else if (rayArr[y] < 0)
					face = EFace.TOP;
			} 
			else if (t[z] < t[x] && t[z] < t[y])
			{
				// z is the smallest
				rel[z] += d[z];
				t[z] += dt[z];
				if (rayArr[z] > 0)
					face = EFace.NORTH;
				else if (rayArr[z] < 0)
					face = EFace.SOUTH;
			} 
			else if (t[x] == t[z] && t[x] < t[y])
			{
				// x and z are both the smallest
				rel[x] += d[x];
				t[x] += dt[x];
				rel[z] += d[z];
				t[z] += dt[z];
				face = EFace.NORTH;
				if (rayArr[z] > 0)
					face = EFace.NORTH;
				else if (rayArr[z] < 0)
					face = EFace.SOUTH;
			} 
			else if (t[x] == t[y] && t[y] < t[z])
			{
				// x and y are both the smallest
				rel[x] += d[x];
				t[x] += dt[x];
				rel[y] += d[y];
				t[y] += dt[y];
				if (rayArr[y] > 0)
					face = EFace.BOTTOM;
				else if (rayArr[y] < 0)
					face = EFace.TOP;
			} 
			else if (t[y] == t[z] && t[z] < t[x])
			{
				// y and z are both the smallest
				rel[y] += d[y];
				t[y] += dt[y];
				rel[z] += d[z];
				t[z] += dt[z];
				if (rayArr[y] > 0)
					face = EFace.BOTTOM;
				else if (rayArr[y] < 0)
					face = EFace.TOP;
			} 
			else if (t[x] == t[y] && t[y] == t[z] && t[x] != Float.MAX_VALUE)
			{
				// if x, y, and z are all equal and finite
				rel[x] += d[x];
				t[x] += dt[x];
				rel[y] += d[y];
				t[y] += dt[y];
				rel[z] += d[z];
				t[z] += dt[z];
				if (rayArr[y] > 0)
					face = EFace.BOTTOM;
				else if (rayArr[y] < 0)
					face = EFace.TOP;
			}

			int bx = (int) (rel[x] + posArr[x]), by = (int) (rel[y] + posArr[y]), bz = (int) (rel[z] + posArr[z]);

			int chunkPosition = Statics.untrustedWorldCoordsToChunkID(bx, by, bz);
			if (chunkPosition == -1)
				return;

			byte block = world.getTrustedBlockType(chunkPosition, bx, by, bz);
			if (block != 0)
			{
				targetedFrame.target = new Vector3f(bx, by, bz);
				targetedFrame.face = face;
				return;
			}
		}
		return;
	}
}