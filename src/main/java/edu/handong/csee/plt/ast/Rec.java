package edu.handong.csee.plt.ast;

public class Rec extends AST {
	char name;
	AST namedExpr;
	AST fstCall;
	
	public Rec(char name, AST namedExpr, AST fstCall) {
		this.name = name;
		this.namedExpr = namedExpr;
		this.fstCall = fstCall;
	}

	public char getName() {
		return name;
	}

	public AST getNamedExpr() {
		return namedExpr;
	}

	public AST getFstCall() {
		return fstCall;
	}

	@Override
	public String getASTCode() {
		
		return "(rec " + name + " " + namedExpr.getASTCode() + " " + fstCall.getASTCode() + ")";
	}
}
