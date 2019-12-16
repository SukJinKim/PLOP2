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
import edu.handong.csee.plt.ds.ASub;
import edu.handong.csee.plt.ds.DefrdSub;
import edu.handong.csee.plt.ds.MtSub;
import edu.handong.csee.plt.val.ClosureV;
import edu.handong.csee.plt.val.FAEValue;
import edu.handong.csee.plt.val.NumV;

public class Interpreter {
	
	public FAEValue interp(AST ast, DefrdSub ds) throws FreeIdentifierException {
		if(ast instanceof Num) {
			
			return new NumV(((Num)ast).getStrNum()); //[num    (n)      (numV n)]
		}
		
		if(ast instanceof Add) {
			Add add = (Add)ast;
		
			return numPlus(interp(add.getLhs(), ds), interp(add.getRhs(), ds)); //[add (l r) (num+ (interp l) (interp r))] 
		}
		
		if(ast instanceof Sub) {
			Sub sub = (Sub)ast;
			
			return numMinus(interp(sub.getLhs(), ds), interp(sub.getRhs(), ds)); //[sub    (l r)    (num- (interp l ds) (interp r ds))]
		}
		
		if(ast instanceof Id) {
			
			return lookUp(((Id)ast).getId(), ds); //[id     (s)      (lookup s ds)]
		}
		
		if(ast instanceof Fun) {
			Fun fun = (Fun)ast;
			char p = fun.getParam();
			AST b = fun.getBody();
			
			return new ClosureV(p, b, ds); //[fun    (p b)    (closureV p b ds)]
		}
	
		if(ast instanceof App) {
			 //[app    (f a)    (local [(define f-val (interp f ds))
		       //                        (define a-val (interp a ds))]
		         //              (interp (closureV-body f-val)
		           //                    (aSub (closureV-param f-val)
		             //                        a-val
		               //                      (closureV-ds f-val))))]
			
			App app = (App) ast;
			AST f = app.getFtn();
			AST a = app.getArg();
			
			ClosureV fVal = (ClosureV)interp(f, ds);
			FAEValue aVal = interp(a, ds);
			
			return interp(fVal.getBody(), new ASub(fVal.getName(), aVal, fVal.getDs()));
		}
		
		return null;
		
	}
	
	
	private static FAEValue lookUp(char name, DefrdSub ds) throws FreeIdentifierException{
		if(ds instanceof MtSub){
			
			throw new FreeIdentifierException("Free identifier"); //[mtSub () (error 'lookup "free identifier")]
		}
		
		if(ds instanceof ASub) {
			char i = ((ASub) ds).getName();
			FAEValue v = ((ASub) ds).getValue();
			DefrdSub saved = ((ASub) ds).getDs();
			
			
			return (name == i) ? v : lookUp(name, saved); //[aSub (i v saved) (if (symbol=? i name) v (lookup name saved))]
			
		}
		
		return null;
	}
	
	private static FAEValue numOp(char op, FAEValue lhs, FAEValue rhs) {
		
		int l = Integer.parseInt(((NumV)lhs).getStrNum());
		int r = Integer.parseInt(((NumV)rhs).getStrNum());
		
		String add = "" + (l + r);
		String sub = "" + (l - r);
		
		return (op == '+') ? new NumV(add) : new NumV(sub); 
	}
	
	private static FAEValue numPlus(FAEValue faeValue, FAEValue faeValue2) {
		
		return numOp('+', faeValue, faeValue2);
	}
	
	private static FAEValue numMinus(FAEValue lhs, FAEValue rhs) {
		
		return numOp('-', lhs, rhs);
	}

	/**
	 * @deprecated it's not used anymore
	 * @param ast
	 * @return
	 * @throws FreeIdentifierException
	 *
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
	*/
	
	/**
	 * @deprecated it's not used anymore.
	 * @param expr
	 * @param idtf
	 * @param val
	 * @return
	 *
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
	}*/
}
