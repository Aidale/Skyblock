package utils;

public class Timer
{
    private int millis;
    private long lastTime = System.currentTimeMillis();
    public Timer(int millis)
    {
	this.millis = millis;
    }
    
    public boolean ready()
    {
	long time = System.currentTimeMillis();
	if (time - lastTime > millis)
	{
	    lastTime = time;
	    return true;
	}
	else return false;
    }
}
