package util;

public class Term
{
	public String name;
	public boolean isVariable;
	public Term() {}
	public Term(String str,boolean isV)
	{
		this.name=str;
		this.isVariable=isV;
	}
}
