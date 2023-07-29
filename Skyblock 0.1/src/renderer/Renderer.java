package renderer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import block.TerrainAssembler;
import player.Camera;
import player.Player;
import player.TargetedFrame;
import shader.StaticShader;
import worldgen.Chunk;
import worldgen.World;

public class Renderer
{
//    eventually plan to make sky colors dynamic

//    private static final float RED = 0.42f;
//    private static final float GREEN = 0.9f;
//    private static final float BLUE = 1.00f;

	private static final float RED = 0.43f;
	private static final float GREEN = 0.69f;
	private static final float BLUE = 1.00f;

//    private static final float RED = 0.52f;
//    private static final float GREEN = 0.69f;
//    private static final float BLUE = 1.00f;

	// eventually plan to make FOV and FAR_PLANE nonstatic and nonfinal
	private static final float FOV = 90;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	private StaticShader shader;
	private Camera camera;
	private int terrainAtlasID;

	public Renderer(Camera camera)
	{
		enableCulling();
		Matrix4f projectionMatrix = createProjectionMatrix();
		this.camera = camera;

//		textureAtlasID = ResourceManager.loadTexture("terrain");
		terrainAtlasID = TerrainAssembler.atlasID;

		shader = new StaticShader();
		shader.start();

		shader.loadProjectionMatrix(projectionMatrix);

		shader.stop();
	}

	public static void setTextureFilterNearest()
	{
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}

	public static void enableCulling()
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void render(World world, Player player)
	{
		Chunk[] chunks = world.getChunks();
		shader.start();
		shader.loadViewMatrix(camera);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainAtlasID);
		for (Chunk chunk : chunks)
		{
//	    Benchmark.benchmark.start("bind vao");
			bindVAO(chunk.vaoID);
//	    Benchmark.benchmark.stop("bind vao");

			shader.loadBlockOffset(chunk.chunkX << 4, 0, chunk.chunkZ << 4);
			GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getChunkBuilderIndexCount(), GL11.GL_UNSIGNED_INT, 0);

//	    Benchmark.benchmark.start("bind vao");
			unbindVAO();
//	    Benchmark.benchmark.stop("bind vao");
		}

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		TargetedFrame targetedFrame = player.targetedFrame;
		Vector3f target = targetedFrame.target;
		if (target != null)
		{
			disableCulling();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);

			bindVAO(targetedFrame.vaoID);
			shader.loadBlockOffset(target);
			GL11.glDrawElements(GL11.GL_TRIANGLES, targetedFrame.indexCount, GL11.GL_UNSIGNED_INT, 0);
			unbindVAO();

			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			enableCulling();
		}
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		shader.stop();
	}

	private void bindVAO(int vaoID)
	{
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}

	private void unbindVAO()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	private Matrix4f createProjectionMatrix()
	{
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;

		return projectionMatrix;
	}

	public void cleanUp()
	{
		shader.cleanUp();
	}
}
