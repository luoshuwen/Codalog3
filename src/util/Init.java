package util;

import codalog.*;
import util.PredicateLog;

public class Init
{
	public static void initialize()
	{
		PredicateLog pl=new PredicateLog("e", 2,true);
		Codalog.predicates.put("=",pl);
		Codalog.predicates.put("!=",pl);
		Codalog.predicates.put("<=",pl);
		Codalog.predicates.put(">=",pl);
		Codalog.predicates.put("<",pl);
		Codalog.predicates.put(">",pl);
	}
}
