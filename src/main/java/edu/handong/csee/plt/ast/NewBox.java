package edu.handong.csee.plt.ast;

public class NewBox extends AST {
	AST val = new AST();

	public NewBox(AST val) {
		this.val = val;
	}

	public AST getVal() {
		return val;
	}

	@Override
	public String getASTCode() {
		
		return "(newbox " + val.getASTCode() + ")";
	}

}
