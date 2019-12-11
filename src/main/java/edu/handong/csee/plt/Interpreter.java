package edu.handong.csee.plt;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.Add;
import edu.handong.csee.plt.ast.Id;
import edu.handong.csee.plt.ast.Num;
import edu.handong.csee.plt.ast.Sub;
import edu.handong.csee.plt.ast.With;
import edu.handong.csee.plt.ast.exception.FreeIdentifierException;

public class Interpreter {

	public String interp(AST ast) throws FreeIdentifierException {
		
		if(ast instanceof Num) {
			return ((Num)ast).getStrNum();
		}
		
		if(ast instanceof Add) {
			Add add = (Add)ast;
			
			return "" + (Integer.parseInt(interp(add.getLhs())) + Integer.parseInt(interp(add.getRhs())));
		}
		
		if(ast instanceof Sub) {
			Sub sub = (Sub)ast;
			return "" + (Integer.parseInt(interp(sub.getLhs())) - Integer.parseInt(interp(sub.getRhs())));
		}
		
		if(ast instanceof With) {
			With with = (With)ast;
			AST expr = with.getExpr();
			char idtf = with.getIdtf();
			AST val = with.getVal();
			
			return "" + interp(subst(expr, idtf, interp(val))); //[with (i v e)(interp (subst e i (interp v)))]
		}
		
		if(ast instanceof Id) {
			throw new FreeIdentifierException("Free identifier"); //[id (s) (error 'interp "free identifier ~a" s)]
		}
		
		return null;
	}
	
	private static AST subst(AST expr, char idtf, String val) {
		
		if(expr instanceof Num) { //[num (n) wae]
			return ((Num)expr); 
		}
		
		if(expr instanceof Add) { //[add (l r) (add (subst l idtf val) (subst r idtf val))]
			Add add = (Add)expr;
			AST lhs = add.getLhs();
			AST rhs = add.getRhs();
			
			return new Add(subst(lhs, idtf, val), subst(rhs, idtf, val));
		}
		
		if(expr instanceof Sub) { //[sub (l r) (sub (subst l idtf val) (subst r idtf val))]
			Sub sub = (Sub)expr;
			AST lhs = sub.getLhs();
			AST rhs = sub.getRhs();
			
			return new Sub(subst(lhs, idtf, val), subst(rhs, idtf, val));
		}
		
		if(expr instanceof With) { //[with (i v e) (with i (subst v idtf val) (if (symbol=? i idtf) e (subst e idtf val)))] 
								   // i, v, e from new one, idtf, val from old one
			With with = (With)expr;
			
			char i = with.getIdtf();
			AST v = with.getVal();
			AST e = with.getExpr();
			
			return new With(idtf,
							subst(v, idtf, val),
							(i == idtf) ? e : subst(e, idtf, val)); //(if (symbol=? i idtf) e (subst e idtf val))
		}
		
		if(expr instanceof Id) { //[id (s) (if (symbol=? s idtf) (num val) wae)])
			Id id = (Id)expr;
			char s = id.getId();
			
			return (s == idtf) ? new Num(val) : id; //(if (symbol=? s idtf) (num val) wae)
		}
		
		return expr;
	}
}
