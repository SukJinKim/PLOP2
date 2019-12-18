package edu.handong.csee.plt.ast;

public class SetBox extends AST {
	AST boxName = new AST();
	AST val = new AST();
	
	public SetBox(AST boxName, AST val) {
		this.boxName = boxName;
		this.val = val;
	}

	public AST getBoxName() {
		return boxName;
	}

	public AST getVal() {
		return val;
	}

	@Override
	public String getASTCode() {
		
		return "(setbox " + boxName.getASTCode() + " " + val.getASTCode() + ")";
	}
	
}
