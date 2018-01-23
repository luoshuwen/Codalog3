package util;

import java.util.ArrayList;
import java.util.HashMap;

import codalog.Codalog;
import evaluater.Evaluater;

public class Print
{
	public static void printRule(Rule rule)
	{
		System.out.print(rule.head.predicate);
		System.out.print("(");
		for (int i = 0; i < rule.head.varia.length - 1; i++)
			System.out.print(rule.head.varia[i].name + ",");
		System.out.print(rule.head.varia[rule.head.varia.length - 1].name + "):-");
		for (int i = 0; i < rule.body.size() - 1; i++)
		{
			Literal currentLiteral = rule.body.get(i);
			System.out.print(currentLiteral.predicate + "(");
			for (int j = 0; j < currentLiteral.varia.length - 1; j++)
				System.out.print(currentLiteral.varia[j].name + ",");
			System.out.print(currentLiteral.varia[currentLiteral.varia.length - 1].name + ")");
			System.out.print(",");
		}
		System.out.print(rule.body.get(rule.body.size() - 1).predicate + "(");
		for (int i = 0; i < rule.body.get(rule.body.size() - 1).varia.length - 1; i++)
			System.out.print(rule.body.get(rule.body.size() - 1).varia[i].name + ",");
		System.out.print(
				rule.body.get(rule.body.size() - 1).varia[rule.body.get(rule.body.size() - 1).varia.length - 1].name
						+ ").");
		System.out.println();
	}

	public static void printFact(Fact fact)
	{
		String str = "";
		if (!fact.isEdbFact)
		{
			str = "*";
		}
		System.out.print(str + fact.predicate + "(");
		for (int i = 0; i < fact.constant.length - 1; i++)
			System.out.print(fact.constant[i].name + ",");
		System.out.print(fact.constant[fact.constant.length - 1].name + ").");
		System.out.println();
	}

	public static String printFactString(Fact fact)
	{
		String str = "";
		if (!fact.isEdbFact)
		{
			str = "*";
		}
		str=str + fact.predicate + "(";
		for (int i = 0; i < fact.constant.length - 1; i++)
			str=str+fact.constant[i].name + ",";
		str=str+fact.constant[fact.constant.length - 1].name + ").\n";
		return str;
	}
	
	public static String printLiteral(Literal literal)
	{
		String str=literal.predicate+"(";
		for(int i=0;i<literal.varia.length-1;i++)
			str=str+literal.varia[i].name+",";
		str=str+literal.varia[literal.varia.length-1].name+")";
		return str;
	}
	
	public static void inputPrint()
	{
		for (InputSentence istr : Codalog.inputSentences)
		{
			System.out.println("lineNo. " + istr.line + " " + istr.sentence);
		}
	}

	public static void printQuery()
	{
		for (int i = 0; i < Evaluater.queryAnswer.size(); i++)
		{
			ArrayList<Fact> all = new ArrayList<>();
			all.addAll(Evaluater.queryAnswer.get(i));
			for (int j = 0; j < all.size(); j++)
			{
				if (Codalog.predicates.get(all.get(j).predicate).built_in)
				{
					all.remove(j);
					j--;
				}
			}
			
			String factStr="";
			for (int j = 0; j < all.size(); j++)
			{
				Fact fact = all.get(j);				
				System.out.print(fact.predicate + "(");
				factStr=factStr+fact.predicate + "(";
				for (int ii = 0; ii < fact.constant.length - 1; ii++)
				{
					System.out.print(fact.constant[ii].name + ",");
					factStr=factStr+fact.constant[ii].name + ",";
				}
				System.out.print(fact.constant[fact.constant.length - 1].name + ")");
				factStr=factStr+fact.constant[fact.constant.length - 1].name + ")";
				if (j == all.size() - 1)
				{
					System.out.print(".");
					factStr=factStr+".";
				}
				else
				{
					System.out.print(",");
					factStr=factStr+",";
				}
			}
			System.out.println();
			factStr=factStr+"\n";
			Evaluater.printOn(factStr);
		}
	}

	public static String printQueryStr(ArrayList<Fact> al)
	{
		String factStr="";
		for (int j = 0; j < al.size(); j++)
		{
			Fact fact = al.get(j);				
			factStr=factStr+fact.predicate + "(";
			for (int ii = 0; ii < fact.constant.length - 1; ii++)
			{
				factStr=factStr+fact.constant[ii].name + ",";
			}
			factStr=factStr+fact.constant[fact.constant.length - 1].name + ")";
			if (j == al.size() - 1)
			{
				factStr=factStr+".";
			}
			else
			{
				factStr=factStr+",";
			}
		}
		factStr=factStr+"\n";
		return factStr;
	}
	
	public static String printSubstitution(HashMap<String, String> substitution)
	{
		String str="{";
		for(String key:substitution.keySet())
			str=str+key+"="+substitution.get(key)+",";
		str=str.substring(0,str.length()-1);
		str=str+"}";
		return str;
	}
}
