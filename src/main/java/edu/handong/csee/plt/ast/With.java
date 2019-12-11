package edu.handong.csee.plt.ast;

public class With extends AST {
	char idtf;
	AST val = new AST();
	AST expr = new AST();
	
	public With(char idtf, AST val, AST expr) {
		this.idtf = idtf;
		this.val = val;
		this.expr = expr;
	}
	
	public char getIdtf() {
		return idtf;
	}

	public AST getVal() {
		return val;
	}

	public AST getExpr() {
		return expr;
	}

	@Override
	public String getASTCode() {
		return "(with " + idtf + " " + val.getASTCode() + " " + expr.getASTCode() + ")"; //(with i (parse v) (parse e))
	}
}
