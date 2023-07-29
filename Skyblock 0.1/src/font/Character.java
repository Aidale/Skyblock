package font;

public class Character
{
	public static final int CROSSHAIR = 127;

	public final int id;
	public final int u, v, width, height;
	public final int xOffset, yOffset;

	public Character(int id, int u, int v, int width, int height, int xOffset, int yOffset)
	{
		xOffset += 5;
		yOffset += 5;

		u += 5;
		v += 4 - yOffset;

		width -= 9;
		height = 9;

		yOffset = 0;

		this.id = id;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
