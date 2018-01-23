package parser;

import java.io.*;
import java.util.*;
import codalog.*;
import util.*;

public class Parser
{
	public static boolean sentenceCheck(String sentence, int index)
	{
		if (sentence.charAt(sentence.length() - 1) != '.')
		{
			//System.out.println(sentence.charAt(sentence.length() - 1));
			ErrorOutput.Output("At line " + index + ": wrong sentence used, without '.'");
			return false;
		}

		int i = -1;
		i = sentence.indexOf(":-");

		if ((i == -1 && factSentenceCheck(sentence, index)) || (i != -1 && ruleSentenceCheck(sentence, index)))
		{
			return true;
		} else
		{
			// ErrorOutput.Output("At line " + index + ": wrong sentence used.");
			return false;
		}
	}

	public static Fact factCheck(String factSentence, int index)
	{
		int left = -1;
		left = factSentence.indexOf('(');
		if (left == -1 || left == 0)
		{
			ErrorOutput.Output("At line " + index + ": wrong fact used.");
			return null;
		}

		String predicate = factSentence.substring(0, left);
		String[] consta = factSentence.substring(left + 1, factSentence.length() - 1).split(",");
		Term[] constant = new Term[consta.length];

		for (int i = 0; i < consta.length; i++)
		{
			if (consta[i].length() == 0)
			{
				ErrorOutput.Output("At line " + index + ": wrong fact used. Wrong constant length.");
				return null;
			}
			Term newTerm = null;
			newTerm = termCheck(consta[i], 0);
			if (newTerm == null)
			{
				ErrorOutput.Output("At line " + index + ": wrong fact used. Wrong constant symbol.");
				return null;
			}
			constant[i] = newTerm;
		}
		Fact newfact = new Fact(index, predicate, constant);
		return newfact;
	}

	public static boolean factSentenceCheck(String factSentence, int index)
	{
		boolean flag = true;
		while (true)
		{
			int right = -1;
			right = factSentence.indexOf(')');
			if (right == -1)// || left == 0 || factSentence.charAt(factSentence.length() - 2) != ')')
			{
				ErrorOutput.Output("At line " + index + ": wrong fact used.");
				return false;
			}

			String afact = factSentence.substring(0, right + 1);
			Fact newFact = null;
			newFact = factCheck(afact, index);
			if (newFact == null)
			{
				// ErrorOutput.Output("At line " + (index + 1) + ": wrong fact used. Wrong
				// constant symbol.");
				return false;
			} else
			{
				Codalog.facts.add(newFact);
			}

			if (right + 2 >= factSentence.length())
				break;
			else
				factSentence = factSentence.substring(right + 2, factSentence.length());
		}
		return flag;
	}

	public static Rule ruleCheck(String ruleSentence, int index)
	{
		// System.out.println(ruleSentence);
		int colon = ruleSentence.indexOf(":-");
		Literal head = null;
		head = literalCheck(ruleSentence.substring(0, colon));
		if (head == null)
		{
			ErrorOutput.Output("At line " + index + ": wrong head used.");
			return null;
		}

		ArrayList<Literal> body = null;
		body = bodyCheck(ruleSentence.substring(colon + 2, ruleSentence.length() - 1), index);
		if (body == null)
		{
			// ErrorOutput.Output("At line " + index + ": wrong body used.");
			return null;
		}
		Rule newrule = new Rule(index, head, body);
		return newrule;
	}

	public static boolean ruleSentenceCheck(String ruleSentence, int index)
	{
		boolean flag = true;
		while (true)
		{
			int colon = ruleSentence.indexOf(":-");
			if (colon == -1)
			{
				ErrorOutput.Output("At line " + index + ": wrong rule used.");
				return false;
			}
			int end = -1;
			boolean find = false;
			for (int i = colon + 1; i < ruleSentence.length() - 2; i++)
			{
				if (ruleSentence.charAt(i) == ':' && ruleSentence.charAt(i + 1) == '-')
				{
					for (int j = i; j > colon; j--)
					{
						if (ruleSentence.charAt(j) == '.')
						{
							end = j;
							find = true;
							break;
						}
					}
				}
				if (find == true)
					break;
			}
			if (end == -1)
				end = ruleSentence.length() - 1;

			// System.out.println(index+"end="+end);

			Rule newRule = null;
			newRule = ruleCheck(ruleSentence.substring(0, end + 1), index);
			if (newRule == null)
			{
				// ErrorOutput.Output("At line " + index + ": wrong rule used.");
				return false;
			} else
			{
				Codalog.rules.add(newRule);
			}
			if (end >= ruleSentence.length() - 1)
				break;
			else
			{
				ruleSentence = ruleSentence.substring(end + 1, ruleSentence.length());
			}
		}
		return flag;
	}

	public static Literal literalCheck(String lit)
	{
		int left = -1;
		left = lit.indexOf("(");
		if (left == -1 || left == 0 || lit.charAt(lit.length() - 1) != ')')
		{
			return null;
		}

		String predicate = lit.substring(0, left);
		String[] varia = lit.substring(left + 1, lit.length() - 1).split(",");
		Term[] variabl = new Term[varia.length];

		for (int i = 0; i < varia.length; i++)
		{
			if (varia[i].length() == 0)
			{
				return null;
			}
			Term nTerm = null;
			nTerm = termCheck(varia[i], 1);
			if (nTerm == null)
			{
				return null;
			}
			variabl[i] = nTerm;
		}
		Literal literal = new Literal(predicate, variabl);
		return literal;
	}

	public static Term termCheck(String termstr, int mode)// 0 for constant,1 for variable and constant
	{
		Term newTerm = null;
		if (mode == 0)// constant
		{
			if (termstr.length() >= 2 && termstr.charAt(0) == '\'' && termstr.charAt(termstr.length() - 1) == '\'')
			{
				newTerm = new Term(termstr, false);
			} else
			{
				// if((termstr.charAt(0)>=97&&termstr.charAt(0)<=122)||(termstr.charAt(0)>=48&&termstr.charAt(0)<=57))
				if (termstr.charAt(0) < 65 || termstr.charAt(0) > 90)
				{
					newTerm = new Term(termstr, false);
				} else
					return null;
			}
		} else// constant and variable
		{
			if (termstr.length() >= 2 && termstr.charAt(0) == '\'' && termstr.charAt(termstr.length() - 1) == '\'')
				newTerm = new Term(termstr, false);
			else if (termstr.charAt(0) < 65 || termstr.charAt(0) > 90)
				newTerm = new Term(termstr, false);
			else
				newTerm = new Term(termstr, true);
		}
		return newTerm;
	}

	public static ArrayList<Literal> bodyCheck(String body, int index)
	{
		ArrayList<Literal> lits = new ArrayList<Literal>();
		while (true)
		{
			int right = -1;
			right = body.indexOf(')');
			if (right == -1)
				break;
			if (right + 1 < body.length() - 1 && body.charAt(right + 1) != ',')
			{
				if (index != -1)
					ErrorOutput.Output("At line " + index + ": wrong body comma used.");
				return null;
			}
			Literal subgoal = literalCheck(body.substring(0, right + 1));
			if (subgoal == null)
			{
				if (index != -1)
					ErrorOutput.Output("At line " + index + ": wrong body literal used.");
				return null;
			}
			lits.add(subgoal);

			if (right + 2 >= body.length())
				break;
			body = body.substring(right + 2, body.length());
		}
		return lits;
	}

	public static boolean parse()
	{
		boolean flag1 = true;

		if (Codalog.printProgram)
			Print.inputPrint();

//		for (int ii = 0; ii < Codalog.inputSentences.size(); ii++)
//		{
//			String currentSentence = Codalog.inputSentences.get(ii).sentence;
//			if (ii<Codalog.inputSentences.size()-1&&((currentSentence.charAt(currentSentence.length() - 2) == ':'	&& currentSentence.charAt(currentSentence.length() - 1) == '-')||currentSentence.charAt(currentSentence.length() - 1) == ','))
//			{
//				while (ii<Codalog.inputSentences.size()-1&&currentSentence.charAt(currentSentence.length() - 1) != '.')
//				{
//					currentSentence = currentSentence + Codalog.inputSentences.get(ii + 1).sentence;
//					Codalog.inputSentences.remove(ii + 1);
//				}
//				InputSentence is=new InputSentence(currentSentence, Codalog.inputSentences.get(ii).line);
//				Codalog.inputSentences.set(ii, is);
//			}
//		}
		
		for (int ii = 0; ii < Codalog.inputSentences.size(); ii++)
		{
			String str = Codalog.inputSentences.get(ii).sentence;
			if (str.length() == 0)
			{
				Codalog.inputSentences.remove(ii);
				ii--;
			} 
			else
			{
				if (str.charAt(0) == '%')
				{
					Codalog.inputSentences.remove(ii);
					ii--;
				}
//				else 
//				{
//					if ((str.charAt(str.length() - 2) == ':'	&& str.charAt(str.length() - 1) == '-')||str.charAt(str.length() - 1) == ',')
//					{
//						while (str.charAt(str.length() - 1) != '.'&&ii + 1<Codalog.inputSentences.size())
//						{
//							str = str + Codalog.inputSentences.get(ii + 1);
//							Codalog.inputSentences.remove(ii + 1);
//							ii--;
//						}
//					}
//				}
			}
		}

		for (int ii = 0; ii < Codalog.inputSentences.size(); ii++)
		{
			String str = Codalog.inputSentences.get(ii).sentence;
			int index = str.indexOf("%");
			if (index != -1)
				str = str.substring(0, index);
			InputSentence istr = Codalog.inputSentences.get(ii);
			istr.sentence = str;
			Codalog.inputSentences.set(ii, istr);
		}

		for (int ii = 0; ii < Codalog.inputSentences.size(); ii++)
		{
			String currentSentence = Codalog.inputSentences.get(ii).sentence;
			if (currentSentence.charAt(currentSentence.length() - 2) == ':'
					&& currentSentence.charAt(currentSentence.length() - 1) == '-')
			{
				while (currentSentence.charAt(currentSentence.length() - 1) != '.')
				{
					InputSentence istr = null;
					istr = Codalog.inputSentences.get(ii + 1);
					if (istr != null)
					{
						currentSentence = currentSentence + istr.sentence;
						Codalog.inputSentences.remove(ii + 1);
					} else
					{
						break;
					}
				}
			}
		}

		// check sentence order
		boolean ff1 = false;
		for (int ii = 0; ii < Codalog.inputSentences.size(); ii++)
		{
			String currentSentence = Codalog.inputSentences.get(ii).sentence;
			if (ii > 0 && ii < Codalog.inputSentences.size())
			{
				int in1 = -1;
				int in2 = -1;
				in1 = Codalog.inputSentences.get(ii - 1).sentence.indexOf(":-");
				in2 = Codalog.inputSentences.get(ii).sentence.indexOf(":-");
				if ((in1 == -1 && in2 != -1) || (in1 != -1 && in2 == -1))
				{
					if (!ff1)
					{
						ff1 = true;
					} else
					{
						ErrorOutput.Output("Warning: At line " + Codalog.inputSentences.get(ii).line + ": wrong sentence order.");
					}
				}
			}
		}

		
		for (int ii = 0; ii < Codalog.inputSentences.size(); ii++)
		{
			InputSentence istr = Codalog.inputSentences.get(ii);
			String currentSentence = istr.sentence;
			// space process
			int index = -1;
			index = currentSentence.indexOf('\'');
			if (index != -1)
			{
				boolean f = false;
				for (int j = 0; j < currentSentence.length(); j++)
				{
					if (currentSentence.charAt(j) == '\'')
					{
						f = (f == false) ? true : false;
					}
					if (currentSentence.charAt(j) == ' ' && f != true)
					{
						currentSentence = currentSentence.substring(0, j)
								+ currentSentence.substring(j + 1, currentSentence.length());
						j--;
					}
				}
			} else
			{
				currentSentence = currentSentence.replaceAll(" ", "");
			}

			// built-in process
			for (int c = 0; c < currentSentence.length(); c++)
			{
				if (currentSentence.charAt(c) == '=' || currentSentence.charAt(c) == '!'
						|| currentSentence.charAt(c) == '<' || currentSentence.charAt(c) == '>')
				{
					int c1 = c, c2 = c;
					if (c + 1 < currentSentence.length() && (currentSentence.charAt(c + 1) == '='))
					{
						c2 = c + 1;
					}
					String vari1 = "", vari2 = "";
					int left = c1 - 1;
					int right = c2 + 1;
					for (; left >= 0; left--)
					{
						if (currentSentence.charAt(left) == ',' || currentSentence.charAt(left) == '.'
								|| currentSentence.charAt(left) == '-')
						{
							vari1 = currentSentence.substring(left + 1, c1);
							break;
						}

					}
					for (; right <= currentSentence.length(); right++)
					{
						if (currentSentence.charAt(right) == ',' || currentSentence.charAt(right) == '.')
						{
							vari2 = currentSentence.substring(c2 + 1, right);
							break;
						}

					}
					if (vari1.trim().length() == 0 || vari2.trim().length() == 0)
					{
						flag1 = false;
						ErrorOutput.Output("At line " + (ii + 1) + ": wrong built-in predicate used.");
						break;
					}
					String newp = currentSentence.substring(c1, c2 + 1) + "(" + vari1 + "," + vari2 + ")";
					String newstr = currentSentence.substring(0, left + 1) + newp+ currentSentence.substring(right, currentSentence.length());
					currentSentence = newstr;
					c = left + newp.length();
				}
			}
			istr.sentence = currentSentence;
			Codalog.inputSentences.set(ii, istr);
		}

		for (int ii = 0; ii < Codalog.inputSentences.size(); ii++)
		{
			if (sentenceCheck(Codalog.inputSentences.get(ii).sentence, Codalog.inputSentences.get(ii).line) == false)
				flag1 = false;
		}

		// Print.inputPrint();
		// for(Rule rule:Codalog.rules)
		// Print.printRule(rule);
		// for(Fact fact:Codalog.facts)
		// Print.printFact(fact);
		
		// predicate check
		// facts
		for (int i = 0; i < Codalog.facts.size(); i++)
		{
			PredicateLog pl = null;
			pl = Codalog.predicates.get(Codalog.facts.get(i).predicate);
			if (pl == null)
			{
				PredicateLog newpl = new PredicateLog("e", Codalog.facts.get(i).constant.length, false);
				Codalog.predicates.put(Codalog.facts.get(i).predicate, newpl);
			} 
			else
			{
				if (Codalog.facts.get(i).constant.length == pl.arity && pl.ie.equals("e"))
				{
					
				} 
				else
				{
					ErrorOutput.Output("At line " + Codalog.facts.get(i).line + ": wrong edb predicate used.");
					flag1 = false;
				}
			}
			Fact curF=Codalog.facts.get(i);
			boolean showed=false;
			for (int r = 0; r < Codalog.rules.size(); r++)
			{
				Rule tmp = Codalog.rules.get(r);
				for(int b=0;b<tmp.body.size();b++)
				{
					Literal curL=tmp.body.get(b);
					if(curL.predicate.equals(curF.predicate))
					{
						showed=true;
					}
				}
			}
			if(!showed)
			{
				ErrorOutput.Output("Warning: At line " + Codalog.facts.get(i).line + ": unused facts.");
			}
		}
		// rules
		for (int i = 0; i < Codalog.rules.size(); i++)
		{
			Rule tmp = Codalog.rules.get(i);
			// head
			PredicateLog pl = null;
			pl = Codalog.predicates.get(tmp.head.predicate);
			if (pl == null)
			{
				PredicateLog newpl = new PredicateLog("i", tmp.head.varia.length, false);
				Codalog.predicates.put(tmp.head.predicate, newpl);
			} else
			{
				if (pl.ie.equals("i") && pl.arity == tmp.head.varia.length && !pl.built_in)
				{

				} else
				{
					ErrorOutput.Output("At line " + Codalog.rules.get(i).line + ": wrong head predicate used.");
					flag1 = false;
				}
			}
			// body
			for (int j = 0; j < tmp.body.size(); j++)
			{
				Literal tmpl = tmp.body.get(j);
				PredicateLog pll = null;
				pll = Codalog.predicates.get(tmpl.predicate);
				if (pll == null)
				{
					PredicateLog newpll = new PredicateLog("i", tmpl.varia.length, false);
					Codalog.predicates.put(tmpl.predicate, newpll);
				} else
				{
					if (pll.arity == tmpl.varia.length)
					{

					} else
					{
						ErrorOutput.Output("At line " + Codalog.rules.get(i).line + ": wrong body predicate used.");
						flag1 = false;
					}
				}
			}

		}

		// safety check
		for (int i = 0; i < Codalog.rules.size(); i++)
		{
			Rule rul = Codalog.rules.get(i);
			for (int j = 0; j < rul.head.varia.length; j++)
			{
				if (rul.head.varia[j].isVariable)
				{
					boolean flag = false;
					for (int k = 0; k < rul.body.size(); k++)
					{
						Literal tmp = rul.body.get(k);
						for (int l = 0; l < tmp.varia.length; l++)
						{
							if (rul.head.varia[j].name.equals(tmp.varia[l].name))
							{
								flag = true;
							}
						}
					}
					if (flag == false)
					{
						ErrorOutput.Output("At line " + Codalog.rules.get(i).line + ": safety error.");
						flag1 = false;
					}
				}
			}
		}

		// builtin safety
		for (int ii = 0; ii < Codalog.rules.size(); ii++)
		{
			Rule currentRule = Codalog.rules.get(ii);
			for (int i = 0; i < currentRule.body.size(); i++)
			{
				Literal currentLit = currentRule.body.get(i);
				PredicateLog pl = null;
				pl = Codalog.predicates.get(currentLit.predicate);
				if (pl != null && pl.built_in)
				{
					// Print.printRule(currentRule);
					// System.out.println("curLit:"+i);
					boolean allappear = true;
					for (int j = 0; j < currentLit.varia.length; j++)
					{
						if (currentLit.varia[j].isVariable)
						{
							// System.out.println("j="+j);
							boolean variappear = false;
							for (int k = 0; k < currentRule.body.size(); k++)
							{
								Literal lit2 = currentRule.body.get(k);
								PredicateLog pl2 = null;
								pl2 = Codalog.predicates.get(lit2.predicate);
								if (pl2 != null && !pl2.built_in)
								{
									// System.out.println("k="+k);
									for (int p = 0; p < lit2.varia.length; p++)
									{
										if (lit2.varia[p].isVariable
												&& lit2.varia[p].name.equals(currentLit.varia[j].name))
										{
											variappear = true;
											break;
										}
									}
									if (variappear)
										break;
								}
							}
							if (!variappear)
							{
								allappear = false;
								break;
							}
						}
					}
					if (!allappear)
					{
						flag1 = false;
						ErrorOutput.Output("At line " + Codalog.rules.get(ii).line + ": built-in safety error.");
						break;
					}
				}
			}
		}
		// modify
		for (int r = 0; r < Codalog.rules.size(); r++)
		{
			for (int i = 0; i < Codalog.rules.get(r).body.size(); i++)
			{
				PredicateLog pLog = null;
				pLog = Codalog.predicates.get(Codalog.rules.get(r).body.get(i).predicate);

				if (pLog.ie.equals("e"))
				{
					for (int j = i + 1; j < Codalog.rules.get(r).body.size(); j++)
					{
						PredicateLog pLog2 = null;
						pLog2 = Codalog.predicates.get(Codalog.rules.get(r).body.get(j).predicate);

						if (pLog2.ie.equals("i"))
						{
							Literal swap = Codalog.rules.get(r).body.get(i);
							Codalog.rules.get(r).body.set(i, Codalog.rules.get(r).body.get(j));
							Codalog.rules.get(r).body.set(j, swap);
							break;
						}
					}
				}
			}
			for (int i = 0; i < Codalog.rules.get(r).body.size(); i++)
			{
				PredicateLog pLog = null;
				pLog = Codalog.predicates.get(Codalog.rules.get(r).body.get(i).predicate);
				if (pLog.built_in)
				{
					for (int j = i + 1; j < Codalog.rules.get(r).body.size(); j++)
					{
						PredicateLog pLog2 = null;
						pLog2 = Codalog.predicates.get(Codalog.rules.get(r).body.get(j).predicate);
						if (!pLog2.built_in)
						{
							Literal swap = Codalog.rules.get(r).body.get(i);
							Codalog.rules.get(r).body.set(i, Codalog.rules.get(r).body.get(j));
							Codalog.rules.get(r).body.set(j, swap);
							break;
						}
					}
				}
			}
		}

		return flag1;
	}

	public static boolean checkQuery(String userquery)
	{
		if (userquery.length() <= 3 || userquery.charAt(0) != '?' || userquery.charAt(1) != '-'
				|| userquery.charAt(userquery.length() - 1) != '.')
		{
			return false;
		}

		int index = -1;
		index = userquery.indexOf('\'');
		if (index != -1)
		{
			boolean f = false;
			for (int j = 0; j < userquery.length(); j++)
			{
				if (userquery.charAt(j) == '\'')
				{
					f = (f == false) ? true : false;
				}
				if (userquery.charAt(j) == ' ' && f != true)
				{
					userquery = userquery.substring(0, j) + userquery.substring(j + 1, userquery.length());
					j--;
				}
			}
		} else
		{
			userquery = userquery.replaceAll(" ", "");
		}

		// built-in process
		for (int c = 0; c < userquery.length(); c++)
		{
			if (userquery.charAt(c) == '=' || userquery.charAt(c) == '!' || userquery.charAt(c) == '<'
					|| userquery.charAt(c) == '>')
			{
				int c1 = c, c2 = c;
				if (c + 1 < userquery.length() && (userquery.charAt(c + 1) == '='))
				{
					c2 = c + 1;
				}
				String vari1 = "", vari2 = "";
				int left = c1 - 1;
				int right = c2 + 1;
				for (; left >= 0; left--)
				{
					if (userquery.charAt(left) == ',' || userquery.charAt(left) == '.' || userquery.charAt(left) == '-')
					{
						vari1 = userquery.substring(left + 1, c1);
						break;
					}

				}
				for (; right <= userquery.length(); right++)
				{
					if (userquery.charAt(right) == ',' || userquery.charAt(right) == '.')
					{
						vari2 = userquery.substring(c2 + 1, right);
						break;
					}

				}
				if (vari1.trim().length() == 0 || vari2.trim().length() == 0)
				{
					return false;
				}
				String newp = userquery.substring(c1, c2 + 1) + "(" + vari1 + "," + vari2 + ")";
				String newstr = userquery.substring(0, left + 1) + newp
						+ userquery.substring(right, userquery.length());
				userquery = newstr;
				c = left + newp.length();
			}
		}

		Codalog.userQuery = null;
		Codalog.userQuery = bodyCheck(userquery.substring(2, userquery.length()), -1);
		if (Codalog.userQuery == null)
		{
			return false;
		}

		for (int i = 0; i < Codalog.userQuery.size(); i++)
		{
			Literal currentLit = Codalog.userQuery.get(i);
			PredicateLog pl = null;
			pl = Codalog.predicates.get(currentLit.predicate);
			if (pl != null && pl.built_in)
			{
				boolean allappear = true;
				for (int j = 0; j < currentLit.varia.length; j++)
				{
					if (currentLit.varia[j].isVariable)
					{
						boolean variappear = false;
						for (int k = 0; k < Codalog.userQuery.size(); k++)
						{
							Literal lit2 = Codalog.userQuery.get(k);
							PredicateLog pl2 = null;
							pl2 = Codalog.predicates.get(lit2.predicate);
							if (pl2 != null && !pl2.built_in)
							{
								for (int p = 0; p < lit2.varia.length; p++)
								{
									if (lit2.varia[p].isVariable && lit2.varia[p].name.equals(currentLit.varia[j].name))
									{
										variappear = true;
										break;
									}
								}
								if (variappear)
									break;
							}
						}
						if (!variappear)
						{
							allappear = false;
							break;
						}
					}
				}
				if (!allappear)
				{
					return false;
				}
			}
		}

		return true;
	}

	public static void fileReader()
	{
		try
		{
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(Codalog.filePath + Codalog.fileName), "UTF-8"));
			String line = "";
			int no = 1;
			while ((line = br.readLine()) != null)
			{
				InputSentence istr = null;
				istr = new InputSentence(line.trim(), no++);
				Codalog.inputSentences.add(istr);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		// System.out.println(DatalogProgram.inputSentences.size());
	}
}