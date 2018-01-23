package util;

import codalog.*;

public class ErrorOutput
{
	public static void Output(String message)
	{
		if(Codalog.fileOption==0)
		{
			Log.writeLog(message+"\n");
		}
		if(Codalog.fileOption==1)
		{
			System.out.println(message);
		}
		if(Codalog.fileOption==2)
		{
			Log.writeLog(message+"\n");
			System.out.println(message);
		}
	}
}
