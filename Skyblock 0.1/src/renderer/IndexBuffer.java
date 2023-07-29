package renderer;

public class IndexBuffer
{
	private int[] indices;
	private int pointer;
	
	/**
	 * creates a new indice buffer with length bufferLength
	 * 
	 * @param bufferLength
	 */
	public IndexBuffer(int bufferLength)
	{
		indices = new int[bufferLength];
		pointer = 0;
	}
	
	/**
	 * adds another number to indices in the first empty slot
	 * @param num
	 */
	public void add(int num)
	{
		indices[pointer++] = num;
	}
	
	/**
	 * resets the pointer back to 0, effectively reseting the indices, allowing them to be overwritten
	 */
	public void clear()
	{
		pointer = 0;
	}
	
	/**
	 * @return the length of the indices, or up until the last number put in the buffer
	 */
	public int size()
	{
		return pointer;
	}
	
	/**
	 * @return the values of the index buffer
	 */
	public int[] get()
	{
		return indices;
	}
}
