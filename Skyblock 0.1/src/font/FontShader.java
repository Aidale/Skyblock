package font;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shader.ShaderProgram;

public class FontShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "src/font/fontVertex.txt";
	private static final String FRAGMENT_FILE = "src/font/fontFragment.txt";

	private int location_color;
	private int location_highlight;
	private int location_shadow;

	public FontShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations()
	{
		location_color = super.getUniformLocation("color");
		location_highlight = super.getUniformLocation("highlight");
		location_shadow = super.getUniformLocation("shadow");
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadColor(Vector3f color)
	{
		super.loadVector(location_color, color);
	}

	protected void loadHighlight(boolean highlight)
	{
		super.loadBoolean(location_highlight, highlight);
	}

	protected void loadShadow(boolean shadow)
	{
		super.loadBoolean(location_shadow, shadow);
	}
}
