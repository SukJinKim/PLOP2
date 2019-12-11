package edu.handong.csee.plt;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.Add;
import edu.handong.csee.plt.ast.App;
import edu.handong.csee.plt.ast.Fun;
import edu.handong.csee.plt.ast.Id;
import edu.handong.csee.plt.ast.Num;
import edu.handong.csee.plt.ast.Sub;
import edu.handong.csee.plt.ast.With;
import edu.handong.csee.plt.ast.exception.FreeIdentifierException;

public class Interpreter {

	public AST interp(AST ast) throws FreeIdentifierException {
		
		if(ast instanceof Num) {
			
			return (Num)ast; //[num (n) fwae]
		}
		
		if(ast instanceof Add) {
			Add add = (Add)ast;
		
			return numPlus(interp(add.getLhs()), interp(add.getRhs())); //[add (l r) (num+ (interp l) (interp r))] 
		}
		
		if(ast instanceof Sub) {
			Sub sub = (Sub)ast;
			
			return numMinus(interp(sub.getLhs()), interp(sub.getRhs()));
		}
		
		if(ast instanceof With) {
			With with = (With)ast;
			AST expr = with.getExpr();
			char idtf = with.getIdtf();
			AST val = with.getVal();
			
			return interp(subst(expr, 
								idtf, 
								interp(val)));
		}
		
		if(ast instanceof Id) {
			
			throw new FreeIdentifierException("Free identifier"); //[id (s) (error 'interp "free identifier ~a" s)]
		}
		
		if(ast instanceof Fun) { 
			
			return (Fun)ast; //[fun (p b) fwae]
		}
	
		if(ast instanceof App) {
			// [app (f a) (local [(define ftn (interp f))]
            //(interp (subst (fun-body ftn) (fun-param ftn) (interp a))))]
			
			App app = (App) ast;
			Fun f = (Fun) app.getFtn();
			AST a = app.getArg();
			
			AST body = f.getBody();
			char param = f.getParam();
			
			return interp(subst(body, param, interp(a)));
		}
		
		return null;
	}
	
	private static AST numOp(char op, AST lhs, AST rhs) {
		int l = Integer.parseInt(((Num)lhs).getStrNum());
		int r = Integer.parseInt(((Num)rhs).getStrNum());
		
		String add = "" + (l+r);
		String sub = "" + (l-r);
		
		return (op == '+') ?  new Num(add) : new Num(sub);
	}
	
	private static AST numPlus(AST lhs, AST rhs) {
		
		return numOp('+', lhs, rhs);
	}
	
	private static AST numMinus(AST lhs, AST rhs) {
		
		return numOp('-', lhs, rhs);
	}
	
	private static AST subst(AST expr, char idtf, AST val) {
		
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
			
			return new With(i,
							subst(v, idtf, val),
							(i == idtf) ? e : subst(e, idtf, val));
		}
		
		if(expr instanceof Id) { //[id (name) (cond [(equal? name idtf) val] [else exp])]
			Id id = (Id)expr;
			char s = id.getId();
			
			return (s == idtf) ? val : expr;
		}
		
		if(expr instanceof App) { //[app (f arg) (app (subst f idtf val) (subst arg idtf val))]
			App app = (App)expr;
			
			AST f = app.getFtn();
			AST a = app.getArg();
			
			return new App(subst(f, idtf, val), 
						subst(a, idtf, val));
		}
		
		if(expr instanceof Fun) { //[fun (id body) (if (equal? idtf id) exp (fun id (subst body idtf val)))]
			Fun fun = (Fun) expr;
			
			char id = fun.getParam();
			AST body = fun.getBody();
			
			return (idtf == id) ? expr : new Fun(id, subst(body, idtf, val));
		}
		
		return expr;
	}
}
