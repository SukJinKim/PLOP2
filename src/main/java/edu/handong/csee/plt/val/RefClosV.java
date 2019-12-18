package edu.handong.csee.plt.val;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ds.DefrdSub;

public class RefClosV extends FAEValue {
	char name;
	AST body = new AST();
	DefrdSub ds = new DefrdSub();
	
	public RefClosV(char name, AST body, DefrdSub ds) {
		super();
		this.name = name;
		this.body = body;
		this.ds = ds;
	}
	
	public char getName() {
		return name;
	}
	public AST getBody() {
		return body;
	}
	public DefrdSub getDs() {
		return ds;
	}

	@Override
	public String getFAEValue() {
		
		return "(refclosV " + name + " " + body.getASTCode() + " " + ds.getDefrdSub() + ")";
	}

}
