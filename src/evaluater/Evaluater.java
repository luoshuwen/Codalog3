package evaluater;

import java.util.*;
import codalog.*;
import util.*;

public class Evaluater
{
	public static int allcount = 0;
	public static HashMap<String, ArrayList<Fact>> edb = new HashMap<String, ArrayList<Fact>>();
	public static HashMap<String, ArrayList<Fact>> idb = new HashMap<String, ArrayList<Fact>>();

	public static HashMap<String, ArrayList<Fact>> newidb;
	public static HashMap<String, ArrayList<Fact>> lastIdb;
	public static HashMap<String, ArrayList<Fact>> evaluEdb;
	public static ArrayList<Rule> evaluRules;
	public static ArrayList<ArrayList<Fact>> queryAnswer;

	public static int iteration = 0;

	public static void evaluation()
	{
		for (Fact fact : Codalog.facts)
		{
			fact.isEdbFact = true;
			Print.printFact(fact);
		}

		for (Fact fact : Codalog.facts)
		{
			ArrayList<Fact> al = null;
			al = edb.get(fact.predicate);
			if (al == null)
			{
				al = new ArrayList<Fact>();
				al.add(fact);
				edb.put(fact.predicate, al);
			} else
			{
				al.add(fact);
				edb.put(fact.predicate, al);
			}
		}

		evaluRules = new ArrayList<Rule>();
		for (Rule rule : Codalog.rules)
			evaluRules.add(rule);

		boolean continu = true;

		while (continu)
		{
			newidb = new HashMap<String, ArrayList<Fact>>();
			if (Codalog.seminaive)
			{
				if (iteration != 0)
				{
					if (iteration == 1)
					{
						for (int r = 0; r < evaluRules.size(); r++)// È¡Ò»Ìõrule
						{
							Rule currentRule = evaluRules.get(r);
							if (Codalog.predicates.get(currentRule.body.get(0).predicate).ie.equals("e"))
							{
								//traceOn("delete£º" + evaluRules.get(r).head.predicate + "\n");
								evaluRules.remove(r);
								r--;
							}
						}
					} else
					{
						for (int r = 0; r < evaluRules.size(); r++)
						{
							Rule rul = evaluRules.get(r);
							boolean showup = false;
							for (int l = 0; l < rul.body.size(); l++)
							{
								Literal lite = rul.body.get(l);
								for (int r2 = 0; r2 < evaluRules.size(); r2++)
								{
									Rule rul2 = evaluRules.get(r2);
									if (lite.predicate.equals(rul2.head.predicate))
									{
										showup = true;
									}
								}
							}
							if (showup == false)
							{
								//traceOn("delete£º" + rul.head.predicate + "\n");
								evaluRules.remove(r);
								r--;
							}
						}
					}
					traceOn("After deleting rules, we have rules£º" + evaluRules.size() + "\n");
					// System.out.println("After deleting rules, we have rules£º" +
					// evaluRules.size());
				}
			}

			for (int r = 0; r < evaluRules.size(); r++)
			{
				// System.out.println(r+"~~~");
				Rule currentRule = evaluRules.get(r);
				HashMap<String, String> substitution = new HashMap<String, String>();
				if (Codalog.seminaive)
					semimatch(0, substitution, currentRule, false);
				else
					naivematch(0, substitution, currentRule);

			}

			int iterationCount = 0;

			if (Codalog.seminaive)
			{
				unlabelidb();
			}
			iterationCount = labelnewidb(Codalog.seminaive);

			// System.out.println("iteration: "+iterationCount);
			//traceOn("new idb size:" + newidb.size() + "\n");

			if (iterationCount == 0)
			{
				continu = false;
			}

			allcount += iterationCount;

			combine();

			//traceOn("idb size:" + idb.size() + "\n");

			iteration++;
		}

		for (String str : edb.keySet())
		{
			ArrayList<Fact> edbList = null;
			edbList = edb.get(str);
			if (edbList != null)
			{
				ArrayList<Fact> alidb = null;
				alidb = idb.get(str);
				if (alidb == null)
				{
					alidb = new ArrayList<Fact>();
				}
				for (Fact f : edbList)
				{
					alidb.add(f);
				}
				idb.put(str, alidb);
			}
		}

		for (String str : idb.keySet())
		{
			ArrayList<Fact> idbList = null;
			idbList = idb.get(str);
			if (idbList != null)
			{
				for (Fact f : idbList)
				{
					printOn(Print.printFactString(f));
				}
			}
		}
	}

	public static void querymatch(int depth, HashMap<String, String> parentSubstitution)
	{
		traceOn("Subgoal: " + Print.printLiteral(Codalog.userQuery.get(depth)) + ":\n");
		HashMap<String, String> substitution = new HashMap<String, String>();
		for (String str : parentSubstitution.keySet())
		{
			substitution.put(str, parentSubstitution.get(str));
		}
		Literal currentSubgoal = Codalog.userQuery.get(depth);

		String[] instance = new String[currentSubgoal.varia.length];
		boolean[] matched = new boolean[currentSubgoal.varia.length];
		for (int v = 0; v < currentSubgoal.varia.length; v++)
		{
			if (!currentSubgoal.varia[v].isVariable)
			{
				instance[v] = currentSubgoal.varia[v].name;
				matched[v] = true;
			}
		}
		for (int v = 0; v < currentSubgoal.varia.length; v++)
		{
			if (!matched[v])
			{
				String substi = null;
				substi = substitution.get(currentSubgoal.varia[v].name);
				if (substi != null)
				{
					matched[v] = true;
					//traceOn(v + " matched true: " + substi + ";" + "\n");
				}
				instance[v] = substi;
			}
		}

		if (Codalog.predicates.get(currentSubgoal.predicate) != null&& Codalog.predicates.get(currentSubgoal.predicate).ie.equals("i"))// idbÎ½´Ê
		{
			ArrayList<Fact> localIdb = null;
			localIdb = idb.get(currentSubgoal.predicate);
			if (localIdb != null)
			{
				for (int i = 0; i < localIdb.size(); i++)
				{
					boolean canMatch = true;
					for (int v = 0; v < currentSubgoal.varia.length; v++)
					{
						if (matched[v] == true && !localIdb.get(i).constant[v].name.equals(instance[v]))
						{
							canMatch = false;
							break;
						}
					}
					if (canMatch == true)
					{
						traceOn("\t matching£º" + Print.printFactString(localIdb.get(i)).substring(0, Print.printFactString(localIdb.get(i)).length()-1) + " :\n");
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == false)
							{
								traceOn("\t \t add to substitution£º" + currentSubgoal.varia[v].name + "=="+ localIdb.get(i).constant[v].name+"\n");
								substitution.put(currentSubgoal.varia[v].name, localIdb.get(i).constant[v].name);
							}
						}

						if (depth < Codalog.userQuery.size() - 1)
							querymatch(depth + 1, substitution);
						else
						{
							ArrayList<Fact> newal = new ArrayList<>();
							for (int ii = 0; ii < Codalog.userQuery.size(); ii++)
							{
								Term[] consta = new Term[Codalog.userQuery.get(ii).varia.length];
								for (int v = 0; v < Codalog.userQuery.get(ii).varia.length; v++)
								{
									if (Codalog.userQuery.get(ii).varia[v].isVariable)
										consta[v] = new Term(substitution.get(Codalog.userQuery.get(ii).varia[v].name),
												false);
									else
										consta[v] = Codalog.userQuery.get(ii).varia[v];
								}
								Fact newfact = new Fact(-1, Codalog.userQuery.get(ii).predicate, consta);
								newal.add(newfact);
							}
							boolean duplic = false;
							for (ArrayList<Fact> al : queryAnswer)
							{
								if (factArrayCompare(al, newal))
								{
									duplic = true;
								}
							}
							if (!duplic)
							{
								queryAnswer.add(newal);
								traceOn("\t \t \t generate new answer:"+Print.printQueryStr(newal));
								traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
							}
							
						}
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == false)
							{
								traceOn("\t \t delete from substitution£º" + currentSubgoal.varia[v].name + "=="+ localIdb.get(i).constant[v].name + "\n");
								substitution.remove(currentSubgoal.varia[v].name);
							}
						}
					}

				}
			}
		} else// edb weici
		{
			if (currentSubgoal.predicate.charAt(0) == 33
					|| (currentSubgoal.predicate.charAt(0) >= 60 && currentSubgoal.predicate.charAt(0) <= 62))// built-in
			{
				if (matched[0] && matched[1])
				{
					if ((currentSubgoal.predicate.equals("=") && instance[0].equals(instance[1]))
							|| (currentSubgoal.predicate.equals("!=") && !instance[0].equals(instance[1]))
							|| (currentSubgoal.predicate.equals(">") && stringCompare(instance[0], instance[1]) == 1)
							|| (currentSubgoal.predicate.equals("<") && stringCompare(instance[0], instance[1]) == -1)
							|| (currentSubgoal.predicate.equals(">=")
									&& (instance[0].equals(instance[1]) || stringCompare(instance[0], instance[1]) == 1
											|| stringCompare(instance[0], instance[1]) == 0))
							|| (currentSubgoal.predicate.equals("<=")
									&& (instance[0].equals(instance[1]) || stringCompare(instance[0], instance[1]) == -1
											|| stringCompare(instance[0], instance[1]) == 0)))
					{
						if (depth < Codalog.userQuery.size() - 1)
							querymatch(depth + 1, substitution);
						else
						{
							ArrayList<Fact> newal = new ArrayList<>();
							for (int ii = 0; ii < Codalog.userQuery.size(); ii++)
							{
								Term[] consta = new Term[Codalog.userQuery.get(ii).varia.length];
								for (int v = 0; v < Codalog.userQuery.get(ii).varia.length; v++)
								{
									if (Codalog.userQuery.get(ii).varia[v].isVariable)
										consta[v] = new Term(substitution.get(Codalog.userQuery.get(ii).varia[v].name),false);
									else
										consta[v] = Codalog.userQuery.get(ii).varia[v];
								}
								Fact newfact = new Fact(-1, Codalog.userQuery.get(ii).predicate, consta);
								newal.add(newfact);
							}
							boolean duplic = false;
							for (ArrayList<Fact> al : queryAnswer)
							{
								if (factArrayCompare(al, newal))
								{
									duplic = true;
								}
							}
							if (!duplic)
							{
								queryAnswer.add(newal);
								traceOn("\t \t \t generate new answer:"+Print.printQueryStr(newal));
								traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
							}
						}
					}
				}
			} else// edb weici
			{
				ArrayList<Fact> localEdb = null;
				localEdb = edb.get(currentSubgoal.predicate);
				if (localEdb != null)
				{
					for (int e = 0; e < localEdb.size(); e++)
					{
						boolean canMatch = true;
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == true && !localEdb.get(e).constant[v].name.equals(instance[v]))
							{
								canMatch = false;
								break;
							}
						}
						if (canMatch == true)
						{
							traceOn("\t matching£º" + Print.printFactString(localEdb.get(e)).substring(0, Print.printFactString(localEdb.get(e)).length()-1) + " :\n");
							for (int v = 0; v < currentSubgoal.varia.length; v++)
							{
								if (matched[v] == false)
								{
									traceOn("\t \t add to substitution£º" + currentSubgoal.varia[v].name + "=="+ localEdb.get(e).constant[v].name+"\n");
									substitution.put(currentSubgoal.varia[v].name, localEdb.get(e).constant[v].name);
								}
							}
							if (depth < Codalog.userQuery.size() - 1)
								querymatch(depth + 1, substitution);
							else
							{
								ArrayList<Fact> newal = new ArrayList<>();
								for (int ii = 0; ii < Codalog.userQuery.size(); ii++)
								{
									Term[] consta = new Term[Codalog.userQuery.get(ii).varia.length];
									for (int v = 0; v < Codalog.userQuery.get(ii).varia.length; v++)
									{
										if (Codalog.userQuery.get(ii).varia[v].isVariable)
											consta[v] = new Term(
													substitution.get(Codalog.userQuery.get(ii).varia[v].name), false);
										else
											consta[v] = Codalog.userQuery.get(ii).varia[v];
									}
									Fact newfact = new Fact(-1, Codalog.userQuery.get(ii).predicate, consta);
									newal.add(newfact);
								}
								boolean duplic = false;
								for (ArrayList<Fact> al : queryAnswer)
								{
									if (factArrayCompare(al, newal))
									{
										duplic = true;
									}
								}
								if (!duplic)
								{
									queryAnswer.add(newal);
									traceOn("\t \t \t generate new answer:"+Print.printQueryStr(newal));
									traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
								}
							}
							for (int v = 0; v < currentSubgoal.varia.length; v++)
							{
								if (matched[v] == false)
								{
									traceOn("\t \t delete from substitution£º" + currentSubgoal.varia[v].name + "=="+ localEdb.get(e).constant[v].name + "\n");
									substitution.remove(currentSubgoal.varia[v].name);
								}
							}
						}

					}
				}
			}

		}
	}

	public static void query()
	{
		queryAnswer = new ArrayList<>();
		HashMap<String, String> substitution = new HashMap<String, String>();
		querymatch(0, substitution);
		if (queryAnswer.size() == 0)
			System.out.println("false");
		else
		{
			boolean groundQuery = true;
			for (int i = 0; i < Codalog.userQuery.size(); i++)
			{
				Literal lit = Codalog.userQuery.get(i);
				for (int v = 0; v < lit.varia.length; v++)
				{
					if (lit.varia[v].isVariable)
					{
						groundQuery = false;
						break;
					}
				}
				if (!groundQuery)
					break;
			}
			if (groundQuery)
			{
				System.out.println("true");
			} else
			{
				printOn(Codalog.userquery + "\n");
				Print.printQuery();
			}
		}

	}

	public static void unlabelidb()
	{
		for (String str : idb.keySet())
		{
			ArrayList<Fact> aList = null;
			aList = idb.get(str);
			if (aList != null)
				for (Fact f : aList)
				{
					f.lastGenerated = false;
				}
		}
	}

	public static int labelnewidb(boolean need)
	{
		int num = 0;
		for (String str : newidb.keySet())
		{
			ArrayList<Fact> aList = null;
			aList = newidb.get(str);
			if (aList != null)
				for (Fact f : aList)
				{
					if (need)
						f.lastGenerated = true;
					num++;
				}
		}
		return num;
	}

	public static void combine()
	{
		for (String str : newidb.keySet())
		{
			ArrayList<Fact> bList = null;
			bList = newidb.get(str);
			if (bList != null)
			{
				ArrayList<Fact> aList = null;
				aList = idb.get(str);
				if (aList == null)
				{
					idb.put(str, bList);
				} else
				{
					for (Fact f : bList)
						aList.add(f);
				}
			}
		}
	}

	public static void semimatch(int depth, HashMap<String, String> parentSubstitution, Rule matchRule,boolean usedNewFacts)
	{
		traceOn("Subgoal: " + Print.printLiteral(matchRule.body.get(depth)) + ":\n");
		HashMap<String, String> substitution = new HashMap<String, String>();
		for (String str : parentSubstitution.keySet())
		{
			substitution.put(str, parentSubstitution.get(str));
		}
		Literal currentSubgoal = matchRule.body.get(depth);
		String[] instance = new String[currentSubgoal.varia.length];
		boolean[] matched = new boolean[currentSubgoal.varia.length];
		for (int v = 0; v < currentSubgoal.varia.length; v++)
		{
			if (!currentSubgoal.varia[v].isVariable)
			{
				instance[v] = currentSubgoal.varia[v].name;
				matched[v] = true;
			}
		}
		for (int v = 0; v < currentSubgoal.varia.length; v++)
		{
			if (!matched[v])
			{
				String substi = null;
				substi = substitution.get(currentSubgoal.varia[v].name);
				if (substi != null)
				{
					matched[v] = true;
				}
				instance[v] = substi;
			}
		}

		if (Codalog.predicates.get(currentSubgoal.predicate).ie.equals("i"))// idbÎ½´Ê
		{
			ArrayList<Fact> localIdb = null;
			localIdb = idb.get(currentSubgoal.predicate);
			if (localIdb != null)
			{
				for (int i = 0; i < localIdb.size(); i++)
				{
					boolean canMatch = true;
					for (int v = 0; v < currentSubgoal.varia.length; v++)
					{
						if (matched[v] == true && !localIdb.get(i).constant[v].name.equals(instance[v]))
						{
							//traceOn("at " + v + " disagreement matching" + "\n");
							canMatch = false;
							break;
						}
					}
					if (canMatch == true)
					{
						traceOn("\t matching£º" + Print.printFactString(localIdb.get(i)).substring(0, Print.printFactString(localIdb.get(i)).length()-1) + " :\n");
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == false)
							{
								traceOn("\t \t add to substitution£º" + currentSubgoal.varia[v].name + "=="+ localIdb.get(i).constant[v].name+"\n");
								substitution.put(currentSubgoal.varia[v].name, localIdb.get(i).constant[v].name);
							}
						}

						boolean next = false;
						if (usedNewFacts)
							next = true;
						else
							next = localIdb.get(i).lastGenerated;

						if (depth < matchRule.body.size() - 1)
							semimatch(depth + 1, substitution, matchRule, next);
						else
						{
							if (next)
							{
								Term[] consta = new Term[matchRule.head.varia.length];
								for (int v = 0; v < matchRule.head.varia.length; v++)
								{
									if (matchRule.head.varia[v].isVariable)
										consta[v] = new Term(substitution.get(matchRule.head.varia[v].name), false);
									else
										consta[v] = matchRule.head.varia[v];
								}
								Fact newfact = new Fact(-1, matchRule.head.predicate, consta);

								boolean canInsert = true;
								ArrayList<Fact> al = null;
								al = idb.get(newfact.predicate);
								if (al != null)
								{
									for (int f = 0; f < al.size(); f++)
									{
										Fact currentFact = al.get(f);
										boolean allequal = true;
										for (int j = 0; j < currentFact.constant.length; j++)
										{
											if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
											{
												allequal = false;
											}
										}
										if (allequal == true)
										{
											canInsert = false;
										}
									}
								}
								al = null;
								al = newidb.get(newfact.predicate);
								if (al != null)
								{
									for (int f = 0; f < al.size(); f++)
									{
										Fact currentFact = al.get(f);
										boolean allequal = true;
										for (int j = 0; j < currentFact.constant.length; j++)
										{
											if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
											{
												allequal = false;
											}
										}
										if (allequal == true)
										{
											canInsert = false;
										}
									}
								}
								if (canInsert == true)
								{
									Print.printFact(newfact);
									ArrayList<Fact> nal = null;
									nal = newidb.get(newfact.predicate);
									if (nal == null)
									{
										nal = new ArrayList<Fact>();
									}
									nal.add(newfact);
									newidb.put(newfact.predicate, nal);
									traceOn("\t \t \t generate new fact:"+Print.printFactString(newfact));
									traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
								}
							}
						}
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == false)
							{
								traceOn("\t \t delete from substitution£º" + currentSubgoal.varia[v].name + "=="+ localIdb.get(i).constant[v].name + "\n");
								substitution.remove(currentSubgoal.varia[v].name);
							}
						}
					}

				}
			}
		} else// edb weici
		{
			if (currentSubgoal.predicate.charAt(0) == 33
					|| (currentSubgoal.predicate.charAt(0) >= 60 && currentSubgoal.predicate.charAt(0) <= 62))// built-in
			{
				if (matched[0] && matched[1])
				{
					if ((currentSubgoal.predicate.equals("=") && instance[0].equals(instance[1]))
							|| (currentSubgoal.predicate.equals("!=") && !instance[0].equals(instance[1]))
							|| (currentSubgoal.predicate.equals(">") && stringCompare(instance[0], instance[1]) == 1)
							|| (currentSubgoal.predicate.equals("<") && stringCompare(instance[0], instance[1]) == -1)
							|| (currentSubgoal.predicate.equals(">=")
									&& (instance[0].equals(instance[1]) || stringCompare(instance[0], instance[1]) == 1
											|| stringCompare(instance[0], instance[1]) == 0))
							|| (currentSubgoal.predicate.equals("<=")
									&& (instance[0].equals(instance[1]) || stringCompare(instance[0], instance[1]) == -1
											|| stringCompare(instance[0], instance[1]) == 0)))
					{
						if (depth < matchRule.body.size() - 1)
							semimatch(depth + 1, substitution, matchRule, usedNewFacts);
						else
						{
							if ((!usedNewFacts && iteration == 0) || usedNewFacts)
							{
								Term[] consta = new Term[matchRule.head.varia.length];
								for (int i = 0; i < matchRule.head.varia.length; i++)
								{
									if (matchRule.head.varia[i].isVariable)
										consta[i] = new Term(substitution.get(matchRule.head.varia[i].name), false);
									else
										consta[i] = matchRule.head.varia[i];
								}
								Fact newfact = new Fact(-1, matchRule.head.predicate, consta);

								boolean canInsert = true;
								ArrayList<Fact> al = null;
								al = idb.get(newfact.predicate);
								if (al != null)
								{
									for (int i = 0; i < al.size(); i++)
									{
										Fact currentFact = al.get(i);
										boolean allequal = true;
										for (int j = 0; j < currentFact.constant.length; j++)
										{
											if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
											{
												allequal = false;
											}
										}
										if (allequal == true)
										{
											canInsert = false;
										}
									}
								}
								al = null;
								al = newidb.get(newfact.predicate);
								if (al != null)
								{
									for (int f = 0; f < al.size(); f++)
									{
										Fact currentFact = al.get(f);
										boolean allequal = true;
										for (int j = 0; j < currentFact.constant.length; j++)
										{
											if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
											{
												allequal = false;
											}
										}
										if (allequal == true)
										{
											canInsert = false;
										}
									}
								}
								if (canInsert == true)
								{
									Print.printFact(newfact);
									ArrayList<Fact> nal = null;
									nal = newidb.get(newfact.predicate);
									if (nal == null)
									{
										nal = new ArrayList<Fact>();
									}
									nal.add(newfact);
									newidb.put(newfact.predicate, nal);
									traceOn("\t \t \t generate new fact:"+Print.printFactString(newfact));
									traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
								}
							}
						}
					}
				}
			} else// edb weici
			{
				ArrayList<Fact> localEdb = null;
				localEdb = edb.get(currentSubgoal.predicate);
				if (localEdb != null)
				{
					for (int e = 0; e < localEdb.size(); e++)
					{
						//traceOn("matching£º" + e + localEdb.get(e).predicate + "\n");
						boolean canMatch = true;
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == true && !localEdb.get(e).constant[v].name.equals(instance[v]))
							{
								//traceOn("at " + v + " disagreement matching" + "\n");
								canMatch = false;
								break;
							}
						}
						if (canMatch == true)
						{
							traceOn("\t matching£º" + Print.printFactString(localEdb.get(e)).substring(0, Print.printFactString(localEdb.get(e)).length()-1) + " :\n");
							for (int v = 0; v < currentSubgoal.varia.length; v++)
							{
								if (matched[v] == false)
								{
									traceOn("\t \t add to substitution£º" + currentSubgoal.varia[v].name + "=="+ localEdb.get(e).constant[v].name+"\n");
									substitution.put(currentSubgoal.varia[v].name, localEdb.get(e).constant[v].name);
								}
							}
							if (depth < matchRule.body.size() - 1)
								semimatch(depth + 1, substitution, matchRule, usedNewFacts);
							else
							{
								if ((!usedNewFacts && iteration == 0) || usedNewFacts)
								{
									Term[] consta = new Term[matchRule.head.varia.length];
									for (int i = 0; i < matchRule.head.varia.length; i++)
									{
										if (matchRule.head.varia[i].isVariable)
											consta[i] = new Term(substitution.get(matchRule.head.varia[i].name), false);
										else
											consta[i] = matchRule.head.varia[i];
									}
									Fact newfact = new Fact(-1, matchRule.head.predicate, consta);

									boolean canInsert = true;
									ArrayList<Fact> al = null;
									al = idb.get(newfact.predicate);
									if (al != null)
									{
										for (int i = 0; i < al.size(); i++)
										{
											Fact currentFact = al.get(i);
											boolean allequal = true;
											for (int j = 0; j < currentFact.constant.length; j++)
											{
												if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
												{
													allequal = false;
												}
											}
											if (allequal == true)
											{
												canInsert = false;
											}
										}
									}
									al = null;
									al = newidb.get(newfact.predicate);
									if (al != null)
									{
										for (int f = 0; f < al.size(); f++)
										{
											Fact currentFact = al.get(f);
											boolean allequal = true;
											for (int j = 0; j < currentFact.constant.length; j++)
											{
												if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
												{
													allequal = false;
												}
											}
											if (allequal == true)
											{
												canInsert = false;
											}
										}
									}
									if (canInsert == true)
									{
										Print.printFact(newfact);
										ArrayList<Fact> nal = null;
										nal = newidb.get(newfact.predicate);
										if (nal == null)
										{
											nal = new ArrayList<Fact>();
										}
										nal.add(newfact);
										newidb.put(newfact.predicate, nal);
										traceOn("\t \t \t generate new fact:"+Print.printFactString(newfact));
										traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
									}
								}
							}
							for (int v = 0; v < currentSubgoal.varia.length; v++)
							{
								if (matched[v] == false)
								{
									traceOn("\t \t delete from substitution£º" + currentSubgoal.varia[v].name + "=="+ localEdb.get(e).constant[v].name + "\n");
									substitution.remove(currentSubgoal.varia[v].name);
								}
							}
						}

					}
				}
			}

		}
	}

	public static void naivematch(int depth, HashMap<String, String> parentSubstitution, Rule matchRule)
	{
		traceOn("Subgoal: " + Print.printLiteral(matchRule.body.get(depth)) + ":\n");
		HashMap<String, String> substitution = new HashMap<String, String>();
		for (String str : parentSubstitution.keySet())
		{
			substitution.put(str, parentSubstitution.get(str));
		}
		Literal currentSubgoal = matchRule.body.get(depth);
		String[] instance = new String[currentSubgoal.varia.length];
		boolean[] matched = new boolean[currentSubgoal.varia.length];
		for (int v = 0; v < currentSubgoal.varia.length; v++)
		{
			if (!currentSubgoal.varia[v].isVariable)
			{
				instance[v] = currentSubgoal.varia[v].name;
				matched[v] = true;
			}
		}
		for (int v = 0; v < currentSubgoal.varia.length; v++)
		{
			if (!matched[v])
			{
				String substi = null;
				substi = substitution.get(currentSubgoal.varia[v].name);
				if (substi != null)
				{
					matched[v] = true;
					//traceOn(v + " matched true: " + substi + ";" + "\n");
				}
				instance[v] = substi;
			}
		}

		if (Codalog.predicates.get(currentSubgoal.predicate).ie.equals("i"))// idbÎ½´Ê
		{
			ArrayList<Fact> localIdb = null;
			localIdb = idb.get(currentSubgoal.predicate);
			if (localIdb != null)
			{
				for (int i = 0; i < localIdb.size(); i++)
				{
					//traceOn("matching£º" + i + localIdb.get(i).predicate + "\n");
					boolean canMatch = true;
					for (int v = 0; v < currentSubgoal.varia.length; v++)
					{
						if (matched[v] == true && !localIdb.get(i).constant[v].name.equals(instance[v]))
						{
							//traceOn("at " + v + " disagreement matching" + "\n");
							canMatch = false;
							break;
						}
					}
					if (canMatch == true)
					{
						traceOn("\t matching£º" + Print.printFactString(localIdb.get(i)).substring(0, Print.printFactString(localIdb.get(i)).length()-1) + " :\n");
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == false)
							{
								traceOn("\t \t add to substitution£º" + currentSubgoal.varia[v].name + "=="+ localIdb.get(i).constant[v].name+"\n");
								substitution.put(currentSubgoal.varia[v].name, localIdb.get(i).constant[v].name);
							}
						}

						if (depth < matchRule.body.size() - 1)
							naivematch(depth + 1, substitution, matchRule);
						else
						{
							Term[] consta = new Term[matchRule.head.varia.length];
							for (int v = 0; v < matchRule.head.varia.length; v++)
							{
								if (matchRule.head.varia[v].isVariable)
									consta[v] = new Term(substitution.get(matchRule.head.varia[v].name), false);
								else
									consta[v] = matchRule.head.varia[v];
							}
							Fact newfact = new Fact(-1, matchRule.head.predicate, consta);

							boolean canInsert = true;
							ArrayList<Fact> al = null;
							al = idb.get(newfact.predicate);
							if (al != null)
							{
								for (int f = 0; f < al.size(); f++)
								{
									Fact currentFact = al.get(f);
									boolean allequal = true;
									for (int j = 0; j < currentFact.constant.length; j++)
									{
										if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
										{
											allequal = false;
										}
									}
									if (allequal == true)
									{
										canInsert = false;
									}
								}
							}
							al = null;
							al = newidb.get(newfact.predicate);
							if (al != null)
							{
								for (int f = 0; f < al.size(); f++)
								{
									Fact currentFact = al.get(f);
									boolean allequal = true;
									for (int j = 0; j < currentFact.constant.length; j++)
									{
										if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
										{
											allequal = false;
										}
									}
									if (allequal == true)
									{
										canInsert = false;
									}
								}
							}
							if (canInsert == true)
							{
								Print.printFact(newfact);
								ArrayList<Fact> nal = null;
								nal = newidb.get(newfact.predicate);
								if (nal == null)
								{
									nal = new ArrayList<Fact>();
								}
								nal.add(newfact);
								newidb.put(newfact.predicate, nal);
								traceOn("\t \t \t generate new fact:"+Print.printFactString(newfact));
								traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
							}
						}
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == false)
							{
								traceOn("\t \t delete from substitution£º" + currentSubgoal.varia[v].name + "=="+ localIdb.get(i).constant[v].name + "\n");
								substitution.remove(currentSubgoal.varia[v].name);
							}
						}
					}

				}
			}
		} else// edb weici
		{
			if (currentSubgoal.predicate.charAt(0) == 33
					|| (currentSubgoal.predicate.charAt(0) >= 60 && currentSubgoal.predicate.charAt(0) <= 62))// built-in
			{
				if (matched[0] && matched[1])
				{
					if ((currentSubgoal.predicate.equals("=") && instance[0].equals(instance[1]))
							|| (currentSubgoal.predicate.equals("!=") && !instance[0].equals(instance[1]))
							|| (currentSubgoal.predicate.equals(">") && stringCompare(instance[0], instance[1]) == 1)
							|| (currentSubgoal.predicate.equals("<") && stringCompare(instance[0], instance[1]) == -1)
							|| (currentSubgoal.predicate.equals(">=")
									&& (instance[0].equals(instance[1]) || stringCompare(instance[0], instance[1]) == 1
											|| stringCompare(instance[0], instance[1]) == 0))
							|| (currentSubgoal.predicate.equals("<=")
									&& (instance[0].equals(instance[1]) || stringCompare(instance[0], instance[1]) == -1
											|| stringCompare(instance[0], instance[1]) == 0)))
					{
						if (depth < matchRule.body.size() - 1)
							naivematch(depth + 1, substitution, matchRule);
						else
						{
							Term[] consta = new Term[matchRule.head.varia.length];
							for (int i = 0; i < matchRule.head.varia.length; i++)
							{
								if (matchRule.head.varia[i].isVariable)
									consta[i] = new Term(substitution.get(matchRule.head.varia[i].name), false);
								else
									consta[i] = matchRule.head.varia[i];
							}
							Fact newfact = new Fact(-1, matchRule.head.predicate, consta);

							boolean canInsert = true;
							ArrayList<Fact> al = null;
							al = idb.get(newfact.predicate);
							if (al != null)
							{
								for (int i = 0; i < al.size(); i++)
								{
									Fact currentFact = al.get(i);
									boolean allequal = true;
									for (int j = 0; j < currentFact.constant.length; j++)
									{
										if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
										{
											allequal = false;
										}
									}
									if (allequal == true)
									{
										canInsert = false;
									}
								}
							}
							al = null;
							al = newidb.get(newfact.predicate);
							if (al != null)
							{
								for (int f = 0; f < al.size(); f++)
								{
									Fact currentFact = al.get(f);
									boolean allequal = true;
									for (int j = 0; j < currentFact.constant.length; j++)
									{
										if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
										{
											allequal = false;
										}
									}
									if (allequal == true)
									{
										canInsert = false;
									}
								}
							}
							if (canInsert == true)
							{
								Print.printFact(newfact);
								ArrayList<Fact> nal = null;
								nal = newidb.get(newfact.predicate);
								if (nal == null)
								{
									nal = new ArrayList<Fact>();
								}
								nal.add(newfact);
								newidb.put(newfact.predicate, nal);
								traceOn("\t \t \t generate new fact:"+Print.printFactString(newfact));
								traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
							}
						}
					}
				}
			} else// edb weici
			{
				ArrayList<Fact> localEdb = null;
				localEdb = edb.get(currentSubgoal.predicate);
				if (localEdb != null)
				{
					for (int e = 0; e < localEdb.size(); e++)
					{
						//traceOn("matching£º" + e + localEdb.get(e).predicate + "\n");
						boolean canMatch = true;
						for (int v = 0; v < currentSubgoal.varia.length; v++)
						{
							if (matched[v] == true && !localEdb.get(e).constant[v].name.equals(instance[v]))
							{
								//traceOn("at " + v + " disagreement matching" + "\n");
								canMatch = false;
								break;
							}
						}
						if (canMatch == true)
						{
							traceOn("\t matching£º" + Print.printFactString(localEdb.get(e)).substring(0, Print.printFactString(localEdb.get(e)).length()-1) + " :\n");
							for (int v = 0; v < currentSubgoal.varia.length; v++)
							{
								if (matched[v] == false)
								{
									traceOn("\t \t add to substitution£º" + currentSubgoal.varia[v].name + "=="+ localEdb.get(e).constant[v].name+"\n");
									substitution.put(currentSubgoal.varia[v].name, localEdb.get(e).constant[v].name);
								}
							}
							if (depth < matchRule.body.size() - 1)
								naivematch(depth + 1, substitution, matchRule);
							else
							{
								Term[] consta = new Term[matchRule.head.varia.length];
								for (int i = 0; i < matchRule.head.varia.length; i++)
								{
									if (matchRule.head.varia[i].isVariable)
										consta[i] = new Term(substitution.get(matchRule.head.varia[i].name), false);
									else
										consta[i] = matchRule.head.varia[i];
								}
								Fact newfact = new Fact(-1, matchRule.head.predicate, consta);

								boolean canInsert = true;
								ArrayList<Fact> al = null;
								al = idb.get(newfact.predicate);
								if (al != null)
								{
									for (int i = 0; i < al.size(); i++)
									{
										Fact currentFact = al.get(i);
										boolean allequal = true;
										for (int j = 0; j < currentFact.constant.length; j++)
										{
											if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
											{
												allequal = false;
											}
										}
										if (allequal == true)
										{
											canInsert = false;
										}
									}
								}
								al = null;
								al = newidb.get(newfact.predicate);
								if (al != null)
								{
									for (int f = 0; f < al.size(); f++)
									{
										Fact currentFact = al.get(f);
										boolean allequal = true;
										for (int j = 0; j < currentFact.constant.length; j++)
										{
											if (!currentFact.constant[j].name.equals(newfact.constant[j].name))
											{
												allequal = false;
											}
										}
										if (allequal == true)
										{
											canInsert = false;
										}
									}
								}
								if (canInsert == true)
								{
									Print.printFact(newfact);
									ArrayList<Fact> nal = null;
									nal = newidb.get(newfact.predicate);
									if (nal == null)
									{
										nal = new ArrayList<Fact>();
									}
									nal.add(newfact);
									newidb.put(newfact.predicate, nal);
									traceOn("\t \t \t generate new fact:"+Print.printFactString(newfact));
									traceOn("\t \t \t substitution:"+Print.printSubstitution(substitution)+"\n");
								}

							}
							for (int v = 0; v < currentSubgoal.varia.length; v++)
							{
								if (matched[v] == false)
								{
									traceOn("\t \t delete from substitution£º" + currentSubgoal.varia[v].name + "=="+ localEdb.get(e).constant[v].name + "\n");
									substitution.remove(currentSubgoal.varia[v].name);
								}
							}
						}

					}
				}
			}

		}

	}

	public static int stringCompare(String str1, String str2)
	{
		int ans = 2;
		boolean isNumber1 = false;
		boolean isCharacter1 = false;
		boolean otherSymbol1 = false;
		boolean isNumber2 = false;
		boolean isCharacter2 = false;
		boolean otherSymbol2 = false;

		for (int i = 0; i < str1.length(); i++)
		{
			if (str1.charAt(i) >= 48 && str1.charAt(i) <= 57)
			{
				isNumber1 = true;
			}
			if ((str1.charAt(i) >= 65 && str1.charAt(i) <= 90) || (str1.charAt(i) >= 97 && str1.charAt(i) <= 122))
			{
				isCharacter1 = true;
			}
			if (!(str1.charAt(i) >= 48 && str1.charAt(i) <= 57) && !((str1.charAt(i) >= 65 && str1.charAt(i) <= 90)
					|| (str1.charAt(i) >= 97 && str1.charAt(i) <= 122)))
			{
				otherSymbol1 = true;
			}
		}
		for (int i = 0; i < str2.length(); i++)
		{
			if (str2.charAt(i) >= 48 && str2.charAt(i) <= 57)
			{
				isNumber2 = true;
			}
			if ((str2.charAt(i) >= 65 && str2.charAt(i) <= 90) || (str2.charAt(i) >= 97 && str2.charAt(i) <= 122))
			{
				isCharacter2 = true;
			}
			if (!(str2.charAt(i) >= 48 && str2.charAt(i) <= 57) && !((str2.charAt(i) >= 65 && str2.charAt(i) <= 90)
					|| (str2.charAt(i) >= 97 && str2.charAt(i) <= 122)))
			{
				otherSymbol2 = true;
			}
		}

		if (isNumber1 && isNumber2 && !isCharacter1 && !isCharacter2 && !otherSymbol1 && !otherSymbol2)
		{
			int i1 = Integer.valueOf(str1);
			int i2 = Integer.valueOf(str2);
			if (i1 > i2)
				ans = 1;
			if (i1 < i2)
				ans = -1;
			if (i1 == i2)
				ans = 0;
		}
		if (!isNumber1 && !isNumber2 && isCharacter1 && isCharacter2 && !otherSymbol1 && !otherSymbol2)
		{
			if (str1.compareToIgnoreCase(str2) < 0)
				ans = -1;
			else
				ans = 1;
		}
		return ans;
	}

	public static void traceOn(String str)
	{
		if (Codalog.trace)
		{
			System.out.print(str);
		}
	}

	public static void printOn(String str)
	{
		if (Codalog.fileOption == 2 || Codalog.fileOption == 0)
		{
			Output.writeOutput(str);
		}
	}

	public static boolean factArrayCompare(ArrayList<Fact> al1, ArrayList<Fact> al2)
	{
		boolean canmatch = true;
		for (Fact fa1 : al1)
		{
			boolean match = false;
			for (Fact fa2 : al2)
			{
				if (factCompare(fa1, fa2))
				{
					match = true;
				}
			}
			if (!match)
				canmatch = false;
		}

		boolean canmatch2 = true;
		for (Fact fa2 : al2)
		{
			boolean match2 = false;
			for (Fact fa1 : al1)
			{
				if (factCompare(fa1, fa2))
				{
					match2 = true;
				}
			}
			if (!match2)
				canmatch2 = false;
		}

		if (canmatch && canmatch2)
			return true;
		else
			return false;
	}

	public static boolean factCompare(Fact f1, Fact f2)
	{
		if (!f1.predicate.equals(f2.predicate))
		{
			return false;
		}
		if (f1.constant.length != f2.constant.length)
		{
			return false;
		}
		for (int i = 0; i < f1.constant.length; i++)
		{
			if (!f1.constant[i].name.equals(f2.constant[i].name))
			{
				return false;
			}
		}
		return true;
	}
}