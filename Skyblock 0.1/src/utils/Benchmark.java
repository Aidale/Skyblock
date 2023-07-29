package utils;

import java.util.HashMap;

public class Benchmark
{
	private static HashMap<String, Benchmark.UpdateTimer> bench = new HashMap<String, Benchmark.UpdateTimer>();

	public static void init(String... strings)
	{
		for (String s : strings)
		{
			bench.put(s, new UpdateTimer());
		}
	}

	private static void addTime(String s, long time)
	{
		UpdateTimer timer = bench.get(s);
		timer.updates++;
		timer.totalTime += time;
	}

	private static int getAverageTime(String s)
	{
		UpdateTimer timer = bench.get(s);
		if (timer.updates == 0)
			return -1;
		return (int) (timer.totalTime / timer.updates);
	}

	private static String format(long value)
	{
		String rev = reverse(Long.toString(value));
		int start = rev.indexOf(".") + 1;

		for (int i = start; i < rev.length(); i++)
		{
			if (i % 4 == 2 && i != rev.length() - 1)
			{
				rev = rev.substring(0, i + 1) + "," + rev.substring(i + 1);
				i++;
			}
		}
		return reverse(rev);
	}

	private static String reverse(String str)
	{
		String reverse = "";
		for (int i = 0; i < str.length(); i++)
		{
			reverse += str.charAt(str.length() - i - 1);
		}
		return reverse;
	}

	public static void printReport()
	{
		System.out.println("Report: nanoseconds per operation: (average time)/(total time)");
		for (String s : bench.keySet())
		{
			if (bench.get(s).totalTime != 0)
			{
				System.out.println(s + ": " + format(getAverageTime(s)) + " / " + format((bench.get(s).totalTime)));
			}
		}
	}

	public static void start(String s)
	{
		UpdateTimer timer = bench.get(s);
		timer.timing = true;
		timer.start = System.nanoTime();
	}

	public static void stop(String s)
	{
		long after = System.nanoTime();
		UpdateTimer timer = bench.get(s);
		if (timer.timing)
		{
			timer.timing = false;
			addTime(s, after - timer.start);
		}
	}

	private static class UpdateTimer
	{
		int updates = 0;
		long totalTime = 0;
		long start = 0;
		boolean timing = false;
	}
}
