package edu.handong.csee.plt.val;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ds.DefrdSub;

public class ClosureV extends FAEValue {
	char name;
	AST body = new AST();
	DefrdSub ds = new DefrdSub();
	
	public ClosureV(char name, AST body, DefrdSub ds) {
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

	//Example
	//(closureV 'x (add (id 'x) (num 1)) (mtSub))
	@Override
	public String getFAEValue() {
		
		return "(closureV " + name + " " + body.getASTCode() + " " + ds.getDefrdSub() + ")";
	}
	
}
