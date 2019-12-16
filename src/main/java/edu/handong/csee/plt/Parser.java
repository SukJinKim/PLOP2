package edu.handong.csee.plt;

import java.util.ArrayList;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.Add;
import edu.handong.csee.plt.ast.App;
import edu.handong.csee.plt.ast.Fun;
import edu.handong.csee.plt.ast.Id;
import edu.handong.csee.plt.ast.Num;
import edu.handong.csee.plt.ast.Sub;
import edu.handong.csee.plt.ast.With;

public class Parser {
	//TEST
//	static int cnt = 0;

	AST parse(String exampleCode) {
		ArrayList<String> subExpressions = splitExpressionAsSubExpressions(exampleCode);

		// num
		if(subExpressions.size() == 1 && isNumeric(subExpressions.get(0))) {

			return new Num(subExpressions.get(0));
		}

		// add
		if(subExpressions.get(0).equals("+")) {
			
			return new Add(parse(subExpressions.get(1)),parse(subExpressions.get(2)));
		}
		
		// sub
		if(subExpressions.get(0).equals("-")) {
			
			return new Sub(parse(subExpressions.get(1)),parse(subExpressions.get(2)));
		}
		
		//with
		if(subExpressions.get(0).equals("with")) {
			String InV = subExpressions.get(1); //ex. {x {+ 1 2}}
			char idtf = InV.charAt(1); //x in InV
			String val = InV.substring(2, InV.length()-1).trim();//{+ 1 2} in InV
			String expr = subExpressions.get(2);
		
			return new App(new Fun(idtf, parse(expr)), parse(val));
			//return new With(subExpressions.get(1).charAt(1), parse(val), parse(expr)); //idtf, parse(val), parse(expr)
		}
		
		//id
		if(subExpressions.size() == 1 && Character.isLetter(subExpressions.get(0).charAt(0))) {
			char id = subExpressions.get(0).charAt(0);
			
			return new Id(id);
		}
		
		//fun
		//TEST cases
		//{fun {y} {+ x y}}
		if(subExpressions.get(0).equals("fun")) {
			String pList = subExpressions.get(1); //{y}
			char p = pList.charAt(1); //y
			
			String body = subExpressions.get(2); //{+ x y}
			
			return new Fun(p, parse(body)); //[(list 'fun (list p) b) (fun p (parse b))]
		}
		
		//app
		//TEST cases
		//{{fun {x} {+ x 1}} 10}
		//{with {x 3} {with {f {fun {y} {+ x y}}} {with {x 5} {f 4}}}}
		if(subExpressions.size() == 2) {
			
			String ftn = subExpressions.get(0);
			String arg = subExpressions.get(1);
			
			return new App(parse(ftn), parse(arg)); //[(list f a) (app (parse f) (parse a))]
		}
		
		return null;
	}

	private ArrayList<String> splitExpressionAsSubExpressions(String exampleCode) {

		// deal with brackets first.
		if((exampleCode.startsWith("{") && !exampleCode.endsWith("}"))
				|| (!exampleCode.startsWith("{") && exampleCode.endsWith("}"))) {
			System.out.println("Syntax error");
			System.exit(0);
		}

		if(exampleCode.startsWith("{"))
			exampleCode = exampleCode.substring(1, exampleCode.length()-1); //{ ABC } -> ABC


		return getSubExpressions(exampleCode); 
	}



	/**
	 * This method return a list of sub-expression from the given expression.
	 * For example, {+ 3 {+ 3 4}}  -> +, 3, {+ 3 4}
	 * TODO JC was sleepy while implementing this method...it has complex logic and might be buggy...
	 * You can do better or find an external library.
	 * @param exampleCode
	 * @return list of sub expressions 
	 */
	
	private ArrayList<String> getSubExpressions(String exampleCode) {

		ArrayList<String> sexpressions = new ArrayList<String>();
		int openingParenthesisCount = 0; //{ 의 갯수 
		String strBuffer = ""; //sub expression 담기 위한 buffer 
		
		for(int i=0; i < exampleCode.length()  ;i++) {
			if(i==0 || (i==0 && exampleCode.charAt(i) == '{')) {//처음 character 혹은 처음 character가 { 인 경우 
				strBuffer = strBuffer + exampleCode.charAt(i); //buffer에 character store한다.  
				continue;
			} else if(exampleCode.charAt(i)==' ' && openingParenthesisCount==0){ //character가 ' ' 이고 { 가 한 개도 없는 code인 경우
				// buffer is ready to be a subexpression
				if(!strBuffer.isEmpty()) {
					sexpressions.add(strBuffer);
					strBuffer = ""; // Ready to start a new buffer
				}
				continue;
			} else {
				if(exampleCode.charAt(i)=='{' && openingParenthesisCount==0){ //character가 { 이고 { 가 한 개도 없었던 code인 경우 
					openingParenthesisCount++;
					// Ready to start a new buffer
					strBuffer = "" + exampleCode.charAt(i);
					continue;
				} else if(exampleCode.charAt(i)=='{') { //character가 { 인 경우 
					openingParenthesisCount++;
					strBuffer = strBuffer + exampleCode.charAt(i);
					continue;
				} else if(exampleCode.charAt(i)=='}' && openingParenthesisCount>0) { //character가 } 이고 { 의 갯수가 양수인 경우 
					openingParenthesisCount--;
					strBuffer = strBuffer + exampleCode.charAt(i);
					continue;
				} else if(exampleCode.charAt(i)=='}') { //character가 } 인 경우 
					// buffer is ready to be a subexpression
					sexpressions.add(strBuffer);
					continue;
				}
			}
			strBuffer = strBuffer + exampleCode.charAt(i);
		}
		
		sexpressions.add(strBuffer);
		
		//TODO It's needed to deal with lambda function call like {{fun {x} {+ x 1}} 10}, but it's not smart code....
		if((sexpressions.get(0).equals("{fun")) && sexpressions.size() >= 3){
			
			String ftn = sexpressions.get(0)
					+ " "
					+ sexpressions.get(1)
					+ " "
					+ sexpressions.get(2)
					+ "}";
			
			String arg = sexpressions.get(sexpressions.size()-1); //the last one is the argument
			
			
			sexpressions.clear(); //remove original list
			
			sexpressions.add(ftn);
			sexpressions.add(arg);
		}
		
//		TEST
//		System.out.println("\t cnt : " + cnt);
//		
//		for(int i = 0; i < sexpressions.size(); i++) {
//			System.out.println(sexpressions.get(i));
//		}
//		
//		cnt++;

		return sexpressions;
	}

	public static boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
