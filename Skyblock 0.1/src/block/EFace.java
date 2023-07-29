package block;

public enum EFace
{
	TOP(0), BOTTOM(1), NORTH(2), SOUTH(3), EAST(4), WEST(5);

	public int id;

	EFace(int id)
	{
		this.id = id;
	}
}
