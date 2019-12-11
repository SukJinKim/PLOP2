package edu.handong.csee.plt.ast;

public class Fun extends AST {
	char param;
	AST body = new AST();
	
	public Fun(char param, AST body) {
		this.param = param;
		this.body = body;
	}
	
	public char getParam() {
		return param;
	}
	public AST getBody() {
		return body;
	}
	
	@Override
	public String getASTCode() {
		return "(fun " + param + " (" + body.getASTCode() + "))" ; //(fun p (parse b))
	}
}
