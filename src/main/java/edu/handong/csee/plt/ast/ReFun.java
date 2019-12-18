package edu.handong.csee.plt.ast;

public class ReFun extends AST {
	char param;
	AST body = new AST();
	
	public ReFun(char param, AST body) {
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
		
		return "(refun " + param + " " + body.getASTCode() + ")" ; //(refun p (parse b))
	}
	
}
