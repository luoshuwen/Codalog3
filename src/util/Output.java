package util;
import java.io.*;
import codalog.*;

public class Output
{
	public static void writeOutput(String time)
	{
		File file = new File("./output");
		if (!file.exists())
		{
			file.mkdir();
		}
		
		File file2 = new File("./output/"+Codalog.fileName.substring(0, Codalog.fileName.length()-4)+".res");
		if (file2.exists())
		{
			String log=time;
			
            BufferedWriter out = null ;  
            try  
            {  
                out = new  BufferedWriter( new  OutputStreamWriter(    new  FileOutputStream("./output/"+Codalog.fileName.substring(0, Codalog.fileName.length()-4)+".res",  true )));  
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
	                out = new  BufferedWriter( new  OutputStreamWriter(    new  FileOutputStream("./output/"+Codalog.fileName.substring(0, Codalog.fileName.length()-4)+".res",  true )));  
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
