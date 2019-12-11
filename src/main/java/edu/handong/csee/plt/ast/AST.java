package edu.handong.csee.plt.ast;

public class AST {
	
	public String getASTCode() {
		String astCode="";
		
		if(this instanceof Add)
			astCode = ((Add)this).getASTCode();
		
		if(this instanceof Num)
			astCode = ((Num)this).getASTCode();
		
		//Sub
		if(this instanceof Sub)
			astCode = ((Sub)this).getASTCode();
		
		//With
		if(this instanceof With)
			astCode = ((With)this).getASTCode();
		
		//Id
		if(this instanceof Id)
			astCode = ((Id)this).getASTCode();

		return astCode;
	}
}

