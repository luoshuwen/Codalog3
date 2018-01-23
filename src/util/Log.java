package util;
import java.io.*;

import codalog.*;

public class Log
{
	public static void writeLog(String time)
	{
		File file = new File("./log");
		if (!file.exists())
		{
			file.mkdir();
		}
		
		File file2 = new File("./log/"+Codalog.fileName.substring(0, Codalog.fileName.length()-4)+".err");
		if (file2.exists())
		{
			String log=time;
			
            BufferedWriter out = null ;  
            try  
            {  
                out = new  BufferedWriter( new  OutputStreamWriter(    new  FileOutputStream("./log/"+Codalog.fileName.substring(0, Codalog.fileName.length()-4)+".err",  true )));  
                out.write(log);  
            } 
            catch  (Exception e) 
            {  
                e.printStackTrace();
            } 
            finally  
            {  
                try  
                {  
                    out.close();  
                } 
                catch  (IOException e) 
                {  
                    e.printStackTrace();  
                }  
            }
		} 
		else
		{
			try
			{
				file2.createNewFile();//create

				String log=time;
				
	            BufferedWriter out = null ;  
	            try  
	            {  
	                out = new  BufferedWriter( new  OutputStreamWriter(    new  FileOutputStream("./log/"+Codalog.fileName.substring(0, Codalog.fileName.length()-4)+".err",  true )));  
	                out.write(log);  
	            } 
	            catch  (Exception e) 
	            {  
	                e.printStackTrace();
	            } 
	            finally  
	            {  
	                try  
	                {  
	                    out.close();  
	                } 
	                catch  (IOException e) 
	                {  
	                    e.printStackTrace();  
	                }  
	            }
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
