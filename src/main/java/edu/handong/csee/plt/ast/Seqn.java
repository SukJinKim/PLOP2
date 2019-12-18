package edu.handong.csee.plt.ast;

public class Seqn extends AST {
	AST expr1 = new AST();
	AST expr2 = new AST();
	
	public Seqn(AST expr1, AST expr2) {
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	public AST getExpr1() {
		return expr1;
	}

	public AST getExpr2() {
		return expr2;
	}

	@Override
	public String getASTCode() {
		
		return "(seqn " + expr1.getASTCode() + " " + expr2.getASTCode() + ")";
	}
	
}
