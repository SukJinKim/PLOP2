package edu.handong.csee.plt.ast;

public class SetVar extends AST {
	char name;
	AST val = new AST();
	
	public SetVar(char name, AST val) {
		this.name = name;
		this.val = val;
	}
	
	public char getName() {
		return name;
	}

	public AST getVal() {
		return val;
	}

	@Override
	public String getASTCode() {
		
		return "(setvar " + name + " " + val.getASTCode() + ")";
	}

}
