package renderer;

public class Indexer
{
	private int pointer = 0;
	
	/**
	 * advances the pointer by one
	 * @return returns the number the pointer is looking at
	 */
	public int get()
	{
		return pointer++;
	}
	
	/**
	 * resets the pointer back to 0
	 */
	public void clear()
	{
		pointer = 0;
	}
}
