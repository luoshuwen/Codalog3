package codalog;

import java.util.*;
import parser.*;
import util.*;
import evaluater.*;

public class Codalog
{
	public static ArrayList<Fact> facts=new ArrayList<>();
	public static ArrayList<Rule> rules=new ArrayList<>();
	public static ArrayList<InputSentence> inputSentences=new ArrayList<>();
	public static HashMap<String,PredicateLog> predicates=new HashMap<String,PredicateLog>();
	public static String filePath = "./datalogProgram/";
	public static String fileName="graph10.cdl";
	public static int fileOption=2;//0 for file;1 for console;2 for both 
	public static boolean seminaive;
	public static boolean trace=false;
	public static boolean printProgram=false;
	public static ArrayList<Literal> userQuery=null;
	public static String userquery="";
	
	public static void main(String[] args)
	{
		Init.initialize();	
		Parser.fileReader();
		
		Scanner sc=new Scanner(System.in);
		sc.useDelimiter("\n");
		
		while(true)
		{
			String str="";
			System.out.println("Please type in evaluation mode:(0 for naive, 1 for semi-naive)");
			str=sc.next().trim();
			if(str.length()==1&&(str.charAt(0)=='0'||str.charAt(0)=='1'))
			{
				if(str.charAt(0)=='0')
					seminaive=false;
				else
					seminaive=true;
				break;
			}
		}
		
		while(true)
		{
			String str="";
			System.out.println("Please type in output print mode:(0 for file, 1 for console, 2 for both)");
			str=sc.next().trim();
			if(str.length()==1&&(str.charAt(0)=='0'||str.charAt(0)=='1'||str.charAt(0)=='2'))
			{
				if(str.charAt(0)=='0')
					fileOption=0;
				if(str.charAt(0)=='1')
					fileOption=1;
				if(str.charAt(0)=='2')
					fileOption=2;
				break;
			}
		}
		
		while(true)
		{
			String str="";
			System.out.println("Please type in trace mode:(0 for close, 1 for open)");
			str=sc.next().trim();
			if(str.length()==1&&(str.charAt(0)=='0'||str.charAt(0)=='1'))
			{
				if(str.charAt(0)=='0')
					trace=false;
				else
					trace=true;
				break;
			}
		}
		
		if(Parser.parse())
		{
//			for(Rule rule:rules)
//				Print.printRule(rule);
//			for(Fact fact:facts)
//				Print.printFact(fact);
			
			long starTime=System.currentTimeMillis();
			Evaluater.evaluation();
			long endTime=System.currentTimeMillis();
			long ttime=endTime-starTime;
			System.out.println(ttime+" ms");
			//System.out.println(Evaluater.allcount);

			System.out.println("Type in your query:(exit with 0)");
			while (sc.hasNext())
			{
				userquery=sc.next().trim();
				if(userquery.equals("0"))
					break;
				if(Parser.checkQuery(userquery))	
					Evaluater.query();
				else				
					System.out.println("Wrong user-query.");
				System.out.println("Type in your query:(exit with 0)");								
			}				
		}
	}
}