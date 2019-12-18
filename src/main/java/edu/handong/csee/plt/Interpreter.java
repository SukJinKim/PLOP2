package edu.handong.csee.plt;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.Add;
import edu.handong.csee.plt.ast.App;
import edu.handong.csee.plt.ast.Fun;
import edu.handong.csee.plt.ast.Id;
import edu.handong.csee.plt.ast.If0;
import edu.handong.csee.plt.ast.NewBox;
import edu.handong.csee.plt.ast.Num;
import edu.handong.csee.plt.ast.OpenBox;
import edu.handong.csee.plt.ast.ReFun;
import edu.handong.csee.plt.ast.Rec;
import edu.handong.csee.plt.ast.Seqn;
import edu.handong.csee.plt.ast.SetBox;
import edu.handong.csee.plt.ast.SetVar;
import edu.handong.csee.plt.ast.Sub;
import edu.handong.csee.plt.ast.exception.ApplyingNumberException;
import edu.handong.csee.plt.ast.exception.BoxTypeException;
import edu.handong.csee.plt.ast.exception.FreeIdentifierException;
import edu.handong.csee.plt.ast.exception.NoValueException;
import edu.handong.csee.plt.box.Box;
import edu.handong.csee.plt.ds.ARecSub;
import edu.handong.csee.plt.ds.ASub;
import edu.handong.csee.plt.ds.DefrdSub;
import edu.handong.csee.plt.ds.MtSub;
import edu.handong.csee.plt.store.ASto;
import edu.handong.csee.plt.store.MtSto;
import edu.handong.csee.plt.store.Store;
import edu.handong.csee.plt.val.BoxV;
import edu.handong.csee.plt.val.ClosureV;
import edu.handong.csee.plt.val.ExprV;
import edu.handong.csee.plt.val.FAEValue;
import edu.handong.csee.plt.val.NumV;
import edu.handong.csee.plt.val.RefClosV;
import edu.handong.csee.plt.valnsto.ValNSto;

public class Interpreter {
	
	public static ValNSto interp(AST ast, DefrdSub ds, Store st) throws FreeIdentifierException, BoxTypeException, ApplyingNumberException, NoValueException {
		if(ast instanceof Num) {
			
			return new ValNSto(new NumV(((Num)ast).getStrNum()), st); //[num (n) (v*s (numV n) st)]
		}
		
		if(ast instanceof Add) {
			Add add = (Add)ast;
		
			ThreeParameterFunction<FAEValue, FAEValue, Store, ValNSto> lambda = (v1, v2, st1) -> {
				try {
					return new ValNSto(numPlus(v1, v2), st1);
				} catch (FreeIdentifierException | BoxTypeException | ApplyingNumberException | NoValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			};
			
			return interpTwo(add.getLhs(), add.getRhs(), ds, st, lambda);
		}
		
		if(ast instanceof Sub) {
			Sub sub = (Sub)ast;
			
			ThreeParameterFunction<FAEValue, FAEValue, Store, ValNSto> lambda = (v1, v2, st1) -> {
				try {
					return new ValNSto(numMinus(v1, v2), st1);
				} catch (FreeIdentifierException | BoxTypeException | ApplyingNumberException | NoValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			};
			
			return interpTwo(sub.getLhs(), sub.getRhs(), ds, st, lambda);
		}
		
		if(ast instanceof Id) {
			Id id = (Id)ast;
			char n = id.getId();
			
			return new ValNSto(storeLookUp(lookUp(n, ds), st), st); //[id (n) (v*s (store-lookup (lookup n ds) st) st)]
		}
		
		if(ast instanceof Fun) {
			Fun fun = (Fun)ast;
			char p = fun.getParam();
			AST b = fun.getBody();
			
			return new ValNSto(new ClosureV(p, b, ds), st); //[fun (p b) (v*s (closureV p b ds) st)]
		}
		
		if(ast instanceof ReFun) {
			ReFun reFun = (ReFun)ast;
			char p = reFun.getParam();
			AST b = reFun.getBody();
			
			return new ValNSto(new RefClosV(p, b, ds), st); //[refun (p b) (v*s (refclosV p b ds) st)]
		}
		
		if(ast instanceof NewBox) {
			NewBox nb = (NewBox)ast;
			AST val = nb.getVal();
			
			ValNSto vs = interp(val, ds, st);
			FAEValue vl = vs.getVal();
			Store st1 = vs.getSto();
			
			int a = malloc(st1);
			
			return new ValNSto(new BoxV(a), new ASto(a, vl, st1));
		}
		
		if(ast instanceof SetBox) {
			SetBox sb = (SetBox)ast;
			AST bxExpr = sb.getBoxName();
			AST valExpr = sb.getVal();
			
			ThreeParameterFunction<FAEValue, FAEValue, Store, ValNSto> lambda = (bxVal, val, st1) -> new ValNSto(val, new ASto(((BoxV)bxVal).getAddr(), val, st1));
			
			return interpTwo(bxExpr, valExpr, ds, st, lambda);
		}
		
		if(ast instanceof OpenBox) {
			OpenBox ob = (OpenBox)ast;
			AST bxExpr = ob.getVal();
			
			ValNSto vs = interp(bxExpr, ds, st);
			FAEValue bxVal = vs.getVal();
			Store st1 = vs.getSto();
			int boxVAddr = ((BoxV)bxVal).getAddr();
			
			return new ValNSto(storeLookUp(boxVAddr, st1), st1);
		}
		
		if(ast instanceof Seqn) {
			Seqn s = (Seqn)ast;
			AST a = s.getExpr1();
			AST b = s.getExpr2();
			
			ThreeParameterFunction<FAEValue, FAEValue, Store, ValNSto> lambda = (v1, v2, st1) -> new ValNSto(v2, st1);
			
			return interpTwo(a, b, ds, st, lambda);
		}
		
		if(ast instanceof SetVar) {
			SetVar sv = (SetVar)ast;
			char id = sv.getName();
			AST valExpr = sv.getVal();
			
			int a = lookUp(id, ds);
			
			ValNSto vs = interp(valExpr, ds, st);
			FAEValue val = vs.getVal();
			Store sto = vs.getSto();
			
			return new ValNSto(val, new ASto(a, val, sto));
		}
		
		if(ast instanceof App) {
			App app = (App)ast;
			AST f = app.getFtn();
			AST a = app.getArg();
			
			ValNSto vs = interp(f, ds, st);
			FAEValue fValue = vs.getVal();
			Store fStore = vs.getSto();
			
			if(fValue instanceof ClosureV) {
				ClosureV cs = (ClosureV)fValue;
				char cParam = cs.getName();
				AST cBody = cs.getBody();
				DefrdSub cDS = cs.getDs();
				
				ValNSto vsa = interp(a, ds, st);
				FAEValue aValue = vsa.getVal();
				Store aStore = vsa.getSto();
				
				int newAddr = malloc(aStore);
				
				return interp(cBody, new ASub(cParam, newAddr, cDS), new ASto(newAddr, aValue, aStore));
			
			}else if(fValue instanceof RefClosV){
				RefClosV rcs = (RefClosV)fValue;
				char rcParam = rcs.getName();
				AST rcBody = rcs.getBody();
				DefrdSub rcDS = rcs.getDs();
				
				Id idA = (Id)a;
				
				int addr = lookUp(idA.getId(), ds);
				
				return interp(rcBody, new ASub(rcParam, addr, rcDS), fStore);
				
			}else {
				throw new ApplyingNumberException("Trying to apply a number");
			}
		}
		
		if(ast instanceof If0) {
//			(if (numzero? (interp test-expr ds))
//                (interp then-expr ds)
//                (interp else-expr ds))
			
			If0 ifZero = (If0) ast;
			
			
			return (isZero(interp(ifZero.getTestExpr(), ds, st).getVal())) ? interp(ifZero.getThenExpr(), ds, st) : interp(ifZero.getElseExpr(), ds, st);
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
	
			valueHolder.setBoxVal(interp(namedExpr, newDS, st).getVal());
			
			return interp(fstCall, newDS, st);
		}
		
		
		
		
		return null;
		
	}
	
//	private static FAEValue strict(FAEValue v) throws FreeIdentifierException, BoxTypeException {
//		if(v instanceof ExprV) {
//			//[exprV (expr ds v-box)
//             //(if (not (unbox v-box))
//               //  (local [(define v (strict (interp expr ds)))]
//                 //  (begin (set-box! v-box v)
//                   //       v)) ;true
//                 //(unbox v-box))] ;false
//			
//			boolean vBox = (boolean) ((ExprV) v).getVal().getBoxVal();
//			AST expr = ((ExprV) v).getExpr();
//			DefrdSub ds = ((ExprV) v).getDs();
//			
//			if(!vBox) {
//				FAEValue val = strict(interp(expr, ds));
//				((ExprV) v).setVal(new Box<FAEValue>(val));
//				
//				return v;
//			}else {
//				return (FAEValue) ((ExprV) v).getVal().getBoxVal();
//			}
//		}
//		
//		return v;
//	}
	
	@FunctionalInterface
	public interface ThreeParameterFunction<T, U, V, R> {
	    public R apply(T t, U u, V v);
	}
	
	private static ValNSto interpTwo(AST lhs, AST rhs, DefrdSub ds, Store st1, ThreeParameterFunction<FAEValue, FAEValue, Store, ValNSto> lambda) throws FreeIdentifierException, BoxTypeException, ApplyingNumberException, NoValueException
	{
		ValNSto vs1 = interp(lhs, ds, st1);
		FAEValue val1 = vs1.getVal();
		Store st2 = vs1.getSto();
		
		ValNSto vs2 = interp(rhs, ds, st2);
		FAEValue val2 = vs2.getVal();
		Store st3 = vs2.getSto();
		
		return lambda.apply(val1, val2, st3);
		
	}

//	private static FAEValue lookUp(char name, DefrdSub ds) throws FreeIdentifierException, BoxTypeException{
	private static int lookUp(char name, DefrdSub ds) throws FreeIdentifierException, BoxTypeException{
		if(ds instanceof MtSub){
			
			throw new FreeIdentifierException("Free identifier"); //[mtSub () (error 'lookup "free identifier")]
		}
		
		if(ds instanceof ASub) {
			char i = ((ASub) ds).getName();
			int addr = ((ASub)ds).getAddr();
//			FAEValue v = ((ASub) ds).getValue();
			DefrdSub saved = ((ASub) ds).getDs();
			
			return (name == i) ? addr : lookUp(name, saved); //[aSub (n addr d) (if(symbol=? n name) addr (lookup name d))]
//			return (name == i) ? strict(v) : lookUp(name, saved); //[aSub (i v saved) (if (symbol=? i name) v (lookup name saved))]
			
		}
		
//		if(ds instanceof ARecSub) {	
//			char subName = ((ARecSub)ds).getName();
//			DefrdSub restDS = ((ARecSub)ds).getDs();
//			
//			
//			return (name == subName) ? ((ARecSub)ds).getValueBox().getBoxVal() : lookUp(name, restDS); //[aRecSub (sub-name val-box rest-ds (if (symbol=? sub-name name) (unbox val-box) (lookup name rest-ds))]
//		}
		
		return (Integer) null;
	}
	
	private static FAEValue storeLookUp(int addr, Store sto) throws NoValueException {
		if(sto instanceof MtSto) {
			throw new NoValueException("No Value at address"); //[mtSto () (error 'store-lookup "No value at address")]
		}
		
//	    [aSto (location value rest-store)
//        (if(= location address)
//           value
//           (store-lookup address rest-store))]
		
		ASto asto = (ASto)sto;
		int location = asto.getAddr();
		FAEValue value = asto.getVal();
		Store rest = asto.getRest();
		
		return (location == addr) ? value : storeLookUp(addr, rest);
	}
	
	private static int malloc(Store sto) {
		
		return (1 + maxAddr(sto));
	}
	
	private static int maxAddr(Store sto) {
		int n = 0;
		
		if(sto instanceof MtSto) {
			
			return n;
		}else {
			int addr = ((ASto) sto).getAddr();
			Store _sto = ((ASto) sto).getRest();
			int maxAddress = maxAddr(_sto);
			
			return (addr > maxAddress) ? addr : maxAddress;
		}
	}
	
	private static boolean isZero(FAEValue n) {
		int num = Integer.parseInt(((NumV)n).getStrNum());
		
		return (num == 0);
	}
	
	private static FAEValue numOp(char op, FAEValue lhs, FAEValue rhs) throws FreeIdentifierException, BoxTypeException, ApplyingNumberException, NoValueException {
		
		int l = Integer.parseInt(((NumV)lhs).getStrNum());
		int r = Integer.parseInt(((NumV)rhs).getStrNum());
		
		String add = "" + (l + r);
		String sub = "" + (l - r);
		
//		NumV addNumV = (NumV) strict(new NumV(add));
//		NumV subNumV = (NumV) strict(new NumV(sub));
		
		NumV addNumV = new NumV(add);
		NumV subNumV = new NumV(sub);
		
		return (op == '+') ? addNumV : subNumV; //(numV (op (numV-n (strict x)) (numV-n (strict y))))
	}
	
	private static FAEValue numPlus(FAEValue lhs, FAEValue rhs) throws FreeIdentifierException, BoxTypeException, ApplyingNumberException, NoValueException {
		
		return numOp('+', lhs, rhs);
	}
	
	private static FAEValue numMinus(FAEValue lhs, FAEValue rhs) throws FreeIdentifierException, BoxTypeException, ApplyingNumberException, NoValueException {
		
		return numOp('-', lhs, rhs);
	}
	
/**
 * 
 * @deprecated
 * 
 * 
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
	*/

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
