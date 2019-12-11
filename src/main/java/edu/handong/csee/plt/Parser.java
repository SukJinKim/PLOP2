package edu.handong.csee.plt;

import java.util.ArrayList;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.Add;
import edu.handong.csee.plt.ast.Id;
import edu.handong.csee.plt.ast.Num;
import edu.handong.csee.plt.ast.Sub;
import edu.handong.csee.plt.ast.With;

public class Parser {

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
			String val = InV.substring(2, InV.length()-1).trim();//{+ 1 2} InV
			String expr = subExpressions.get(2);
			
			//TEST
//			System.out.println(InV);
//			System.out.println(idtf);
//			System.out.println(val);
//			System.out.println(expr);
		
			return new With(subExpressions.get(1).charAt(1), parse(val), parse(expr)); //idtf, parse(val), parse(expr)
		}
		
		//id
		if(subExpressions.size() == 1 && Character.isLetter(subExpressions.get(0).charAt(0))) {
			char id = subExpressions.get(0).charAt(0);
			
			return new Id(id);
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
			exampleCode = exampleCode.substring(1, exampleCode.length()-1); //{ ... } 기준으로 자르기 


		return getSubExpressions(exampleCode); 
	}



	/**
	 * This method return a list of sub-expression from the given expression.
	 * For example, {+ 3 {+ 3 4}  -> +, 2, {+ 3 4}
	 * TODO JC was sleepy while implementing this method...it has complex logic and might be buggy...
	 * You can do better or find an external library.
	 * @param exampleCode
	 * @return list of sub expressions 
	 */
	private ArrayList<String> getSubExpressions(String exampleCode) {

		ArrayList<String> sexpressions = new ArrayList<String>();
		int openingParenthesisCount = 0;
		String strBuffer = ""; //sub expression 담기 위한 buffer 
		for(int i=0; i < exampleCode.length()  ;i++) {
			if(i==0 || (i==0 && exampleCode.charAt(i) == '{')) {
				strBuffer = strBuffer + exampleCode.charAt(i); //{{ 이런 경우에 두번째 { 담기 
				continue;
			} else if(exampleCode.charAt(i)==' ' && openingParenthesisCount==0){
				// buffer is ready to be a subexpression
				if(!strBuffer.isEmpty()) {
					sexpressions.add(strBuffer);
					strBuffer = ""; // Ready to start a new buffer
				}
				continue;
			} else {
				if(exampleCode.charAt(i)=='{' && openingParenthesisCount==0){
					openingParenthesisCount++;
					// Ready to start a new buffer
					strBuffer = "" + exampleCode.charAt(i);
					continue;
				} else if(exampleCode.charAt(i)=='{') {
					openingParenthesisCount++;
					strBuffer = strBuffer + exampleCode.charAt(i);
					continue;
				} else if(exampleCode.charAt(i)=='}' && openingParenthesisCount>0) {
					openingParenthesisCount--;
					strBuffer = strBuffer + exampleCode.charAt(i);
					continue;
				} else if(exampleCode.charAt(i)=='}') {
					// buffer is ready to be a subexpression
					sexpressions.add(strBuffer);
					continue;
				}
			}
			strBuffer = strBuffer + exampleCode.charAt(i);
		}
		
		sexpressions.add(strBuffer);
		
//		TEST
//		for(int i = 0; i < sexpressions.size(); i++) {
//			System.out.println(sexpressions.get(i));
//		}

		return sexpressions;
	}

	public static boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
