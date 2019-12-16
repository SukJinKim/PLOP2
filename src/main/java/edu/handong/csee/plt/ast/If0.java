package edu.handong.csee.plt.ast;

public class If0 extends AST {
	AST testExpr = new AST();
	AST thenExpr = new AST();
	AST elseExpr = new AST();
	
	public If0(AST testExpr, AST thenExpr, AST elseExpr) {
		this.testExpr = testExpr;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}

	public AST getTestExpr() {
		return testExpr;
	}

	public AST getThenExpr() {
		return thenExpr;
	}

	public AST getElseExpr() {
		return elseExpr;
	}

	@Override
	public String getASTCode() {
		
		return "(if0 " + testExpr.getASTCode() + " " + thenExpr.getASTCode() + " " + elseExpr.getASTCode() + ")";
	}

}
