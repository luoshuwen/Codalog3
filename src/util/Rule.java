package util;

import java.util.ArrayList;

public class Rule
{
	public int line=0;
	public Literal head=null;
	public ArrayList<Literal> body=new ArrayList<Literal>();
	
	public Rule()
	{
		
	}
	
	public Rule(int lin,Literal hea,ArrayList<Literal> bod)
	{
		this.line=lin;
		this.head=hea;
		this.body=bod;
	}
}
