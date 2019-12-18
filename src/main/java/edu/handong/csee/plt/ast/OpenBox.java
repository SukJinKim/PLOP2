package edu.handong.csee.plt.ast;

public class OpenBox extends AST {
	AST val = new AST();

	public OpenBox(AST val) {
		super();
		this.val = val;
	}
	
	public AST getVal() {
		return val;
	}

	@Override
	public String getASTCode() {
		
		return "(openbox " + val.getASTCode() + ")";
	}
	
}
