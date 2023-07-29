package player;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import block.EFace;

/**
 * class holding all data on the orientation that a player is looking and where the player is
 * the vector that holds the player's position is the same as in the player class
 * TODO make this a static class
 * @author aidan
 *
 */
public class Camera
{
	// pitch and yaw are in degrees
	public Vector3f position;
	public Vector3f ray;
	public float pitch, yaw;
	private Matrix4f viewMatrix;

	public Camera(Vector3f position, float pitch, float yaw)
	{
		this.position = position;
		this.pitch = pitch;
		this.yaw = yaw;
		calculateRay();
		createViewMatrix();
	}

	/**
	 * Using the vector passed in, updates the rotation of the camera's two angles, its pitch and 
	 * its yaw. Using the new angle values, it recalculates it ray
	 * @param vector points from the center of the screen to where the mouse is
	 */
	public void updateRotation(Vector2f vector)
	{
		// mouse sensitivity
		yaw += vector.x * 0.10f;
		pitch -= vector.y * 0.08f;

		if (pitch < -90)
		{
			pitch = -90;
		}
		else if (pitch > 90)
		{
			pitch = 90;
		}

		if (yaw >= 360)
		{
			yaw -= 360;
		}
		else if (yaw < 0)
		{
			yaw += 360;
		}

		calculateRay();
	}

	/**
	 * updates the view matrix of the camera with the new positional and orientational data
	 */
	public void updateViewMatrix()
	{
		viewMatrix = createViewMatrix();
	}

	/**
	 * @return the matrix corresponding to the camera
	 */
	public Matrix4f getViewMatrix()
	{
		return viewMatrix;
	}

	/**
	 * the vector that points to what exactly the camera is looking at. 
	 * This is useful for determining exactly what block the player is looking at
	 */
	private void calculateRay()
	{
		float radPitch = (float) Math.toRadians(pitch);
		float radYaw = (float) Math.toRadians(yaw);

		float cosPitch = (float) Math.cos(radPitch);
		float x = (float) (Math.sin(radYaw) * cosPitch);
		float y = (float) (-Math.sin(radPitch));
		float z = (float) (-Math.cos(radYaw) * cosPitch);
		ray = new Vector3f(x, y, z);
	}

	/**
	 * this matrix transforms the coordinates from world space to eye space
	 * @return Matrix4f
	 */
	private Matrix4f createViewMatrix()
	{
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	/**
	 * @return the string signaling which direction you are facing (in minecraft this is 
	 * part of the f3 debug menu)
	 */
	public String getFaceDirection()
	{
		String towards;
		int yawShift = (int) yaw + 45;
		if (yawShift >= 360) yawShift -= 360;

		if (yawShift < 90)
		{
			towards = "north (Towards negative Z) ";
		}
		else if (yawShift < 180)
		{
			towards = "east (Towards positive X) ";
		}
		else if (yawShift < 270)
		{
			towards = "south (Towards positive Z) ";
		}
		else
		{
			towards = "west (Towards negative X) ";
		}
		
		// %[flags][width][.precision]conversion-character
		String angles = String.format("(%.1f / %.1f)", yaw, pitch);
		return "Facing: " + towards + angles;
	}

	/**
	 * @return a string containing the position in the world of the camera (player) to be used
	 * in the debug menu
	 */
	public String getPosition()
	{
		return String.format("XYZ: %.3f / %.3f / %.3f", position.x, position.y, position.z);
	}
}
