package util;

import java.util.ArrayList;

public class Fact
{
	public int line=0;
	public String predicate="";
	public Term[] constant=null;
	public boolean lastGenerated=false;
	public boolean isEdbFact=false;

	public Fact()
	{
	}
	
	public Fact(int lin,String pre,Term[] consta)
	{
		this.line=lin;
		this.predicate=pre;
		this.constant=consta;
	}
}