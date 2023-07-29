package shader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import player.Camera;
import utils.Utilities;

public class StaticShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "src/shader/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shader/fragmentShader.txt";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_blockOffset;

	public StaticShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	protected void getAllUniformLocations()
	{
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_blockOffset = super.getUniformLocation("blockOffset");
	}

	public void loadBlockOffset(int x, int y, int z)
	{
		super.loadVector(location_blockOffset, new Vector3f(x, y, z));
	}

	public void loadBlockOffset(Vector3f offset)
	{
		super.loadVector(location_blockOffset, offset);
	}

	public void loadTransformationMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera)
	{
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}

	public void loadProjectionMatrix(Matrix4f projection)
	{
		super.loadMatrix(location_projectionMatrix, projection);
	}
}
