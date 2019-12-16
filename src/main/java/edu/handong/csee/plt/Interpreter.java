package edu.handong.csee.plt;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.Add;
import edu.handong.csee.plt.ast.App;
import edu.handong.csee.plt.ast.Fun;
import edu.handong.csee.plt.ast.Id;
import edu.handong.csee.plt.ast.If0;
import edu.handong.csee.plt.ast.Num;
import edu.handong.csee.plt.ast.Rec;
import edu.handong.csee.plt.ast.Sub;
import edu.handong.csee.plt.ast.exception.BoxTypeException;
import edu.handong.csee.plt.ast.exception.FreeIdentifierException;
import edu.handong.csee.plt.box.Box;
import edu.handong.csee.plt.ds.ARecSub;
import edu.handong.csee.plt.ds.ASub;
import edu.handong.csee.plt.ds.DefrdSub;
import edu.handong.csee.plt.ds.MtSub;
import edu.handong.csee.plt.val.ClosureV;
import edu.handong.csee.plt.val.ExprV;
import edu.handong.csee.plt.val.FAEValue;
import edu.handong.csee.plt.val.NumV;

public class Interpreter {
	
	public static FAEValue interp(AST ast, DefrdSub ds) throws FreeIdentifierException, BoxTypeException {
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
//			[app  (f a) (local [(define ftn (interp f ds))]
//                    (interp (closureV-body ftn)
//                                 (aSub (closureV-param ftn)
//                                             (interp a ds)
//                                             (closureV-ds ftn))))]
			
			App app = (App) ast;
			AST f = app.getFtn();
			AST a = app.getArg();
			
			ClosureV ftn = (ClosureV) interp(f, ds);
			return interp(ftn.getBody(), new ASub(ftn.getName(), interp(a, ds), ftn.getDs()));

//			[app (f a)   (local [(define f-val (strict (interp f ds)))
//		                          (define a-val (exprV a ds (box #f)))]
//		                    (interp (closureV-body f-val)
//		                            (aSub (closureV-param f-val)
//		                                  a-val
//		                                  (closureV-ds f-val))))]
			
			
//			ClosureV fVal = (ClosureV)strict(interp(f, ds));
//			FAEValue fVal = strict(interp(f, ds));
//			ExprV aVal = new ExprV(a, ds, new Box<Boolean>(false));
			
//			return interp(fVal.getBody(), new ASub(fVal.getName(), aVal, fVal.getDs()));
	
		}
		
		if(ast instanceof If0) {
//			(if (numzero? (interp test-expr ds))
//                (interp then-expr ds)
//                (interp else-expr ds))
			
			If0 ifZero = (If0) ast;
			
			return (isZero((NumV)interp(ifZero.getTestExpr(), ds))) ? interp(ifZero.getThenExpr(), ds) : interp(ifZero.getElseExpr(), ds);
		}
		
		if(ast instanceof Rec) {
//			[rec  (bound-id named-expr fst-call)
//             (local [(define value-holder (box (numV 198)))
//                         (define new-ds (aRecSub bound-id
//                                                 value-holder
//                                                 ds))]
//               (begin
//                             (set-box! value-holder (interp named-expr new-ds))
//                             (interp fst-call new-ds)))]))
			Rec rec = (Rec) ast;
			char boundId = rec.getName();
			AST namedExpr = rec.getNamedExpr();
			AST fstCall = rec.getFstCall();
		
			Box<FAEValue> valueHolder = new Box<FAEValue>(new NumV("198"));
			ARecSub newDS = new ARecSub(boundId, valueHolder, ds);
	
			valueHolder.setBoxVal(interp(namedExpr, newDS));
			
			return interp(fstCall, newDS);
		}
		
		return null;
		
	}
	
	private static FAEValue strict(FAEValue v) throws FreeIdentifierException, BoxTypeException {
		if(v instanceof ExprV) {
			//[exprV (expr ds v-box)
             //(if (not (unbox v-box))
               //  (local [(define v (strict (interp expr ds)))]
                 //  (begin (set-box! v-box v)
                   //       v)) ;true
                 //(unbox v-box))] ;false
			
			boolean vBox = (boolean) ((ExprV) v).getVal().getBoxVal();
			AST expr = ((ExprV) v).getExpr();
			DefrdSub ds = ((ExprV) v).getDs();
			
			if(!vBox) {
				FAEValue val = strict(interp(expr, ds));
				((ExprV) v).setVal(new Box<FAEValue>(val));
				
				return v;
			}else {
				return (FAEValue) ((ExprV) v).getVal().getBoxVal();
			}
		}
		
		return v;
	}
	
	
	private static FAEValue lookUp(char name, DefrdSub ds) throws FreeIdentifierException, BoxTypeException{
		if(ds instanceof MtSub){
			
			throw new FreeIdentifierException("Free identifier"); //[mtSub () (error 'lookup "free identifier")]
		}
		
		if(ds instanceof ASub) {
			char i = ((ASub) ds).getName();
			FAEValue v = ((ASub) ds).getValue();
			DefrdSub saved = ((ASub) ds).getDs();
			
			
			return (name == i) ? strict(v) : lookUp(name, saved); //[aSub (i v saved) (if (symbol=? i name) v (lookup name saved))]
			
		}
		
		if(ds instanceof ARecSub) {	
			char subName = ((ARecSub)ds).getName();
			DefrdSub restDS = ((ARecSub)ds).getDs();
			
			
			return (name == subName) ? ((ARecSub)ds).getValueBox().getBoxVal() : lookUp(name, restDS); //[aRecSub (sub-name val-box rest-ds (if (symbol=? sub-name name) (unbox val-box) (lookup name rest-ds))]
		}
		
		return null;
	}
	
	private static boolean isZero(NumV n) {
		int num = Integer.parseInt(n.getStrNum());
		
		return (num == 0);
	}
	
	private static FAEValue numOp(char op, FAEValue lhs, FAEValue rhs) throws FreeIdentifierException, BoxTypeException {
		
		int l = Integer.parseInt(((NumV)lhs).getStrNum());
		int r = Integer.parseInt(((NumV)rhs).getStrNum());
		
		String add = "" + (l + r);
		String sub = "" + (l - r);
		
		NumV addNumV = (NumV) strict(new NumV(add));
		NumV subNumV = (NumV) strict(new NumV(sub));
		
		return (op == '+') ? addNumV : subNumV; //(numV (op (numV-n (strict x)) (numV-n (strict y))))
	}
	
	private static FAEValue numPlus(FAEValue lhs, FAEValue rhs) throws FreeIdentifierException, BoxTypeException {
		
		return numOp('+', lhs, rhs);
	}
	
	private static FAEValue numMinus(FAEValue lhs, FAEValue rhs) throws FreeIdentifierException, BoxTypeException {
		
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
