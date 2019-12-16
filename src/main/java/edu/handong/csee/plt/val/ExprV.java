package edu.handong.csee.plt.val;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.exception.BoxTypeException;
import edu.handong.csee.plt.box.Box;
import edu.handong.csee.plt.ds.DefrdSub;

public class ExprV extends FAEValue {
	AST expr;
	DefrdSub ds;
	Box<?> val;
	
	public ExprV(AST expr, DefrdSub ds, Box<?> val) throws BoxTypeException {
		if(!(val.getBoxVal() instanceof FAEValue) && !(val.getBoxVal() instanceof Boolean)) {
			throw new BoxTypeException("Invalid Box type : FAEValue or boolean");
		}
		
		this.expr = expr;
		this.ds = ds;
		this.val = val;
	}

	public AST getExpr() {
		return expr;
	}

	public DefrdSub getDs() {
		return ds;
	}

	public Box<?> getVal() {
		return val;
	}

	public void setExpr(AST expr) {
		this.expr = expr;
	}

	public void setDs(DefrdSub ds) {
		this.ds = ds;
	}

	public void setVal(Box<?> val) {
		this.val = val;
	}

	@Override
	public String getFAEValue() {
		
		return "(exprV " + expr.getASTCode() + " " + ds.getDefrdSub() + " " + val.getBoxVal() + ")" ;
	}

}
