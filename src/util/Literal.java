package util;

public class Literal
{
	public String predicate="";
	public Term[] varia=null;
	
	public Literal()
	{
		
	}
	
	public Literal(String pre,Term[] vari)
	{
		this.predicate=pre;
		this.varia=vari;				
	}
}
