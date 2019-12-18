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
		
		//Fun
		if(this instanceof Fun)
			astCode = ((Fun)this).getASTCode();
		
		//App
		if(this instanceof App)
			astCode = ((App)this).getASTCode();
		
		//If0
		if(this instanceof If0)
			astCode = ((If0)this).getASTCode();
		
		//Rec
		if(this instanceof Rec)
			astCode = ((Rec)this).getASTCode();
		
		//ReFun
		if(this instanceof ReFun)
			astCode = ((ReFun)this).getASTCode();
		
		//NewBox
		if(this instanceof NewBox)
			astCode = ((NewBox)this).getASTCode();
		
		//SetBox
		if(this instanceof SetBox)
			astCode = ((SetBox)this).getASTCode();
		
		//OpenBox
		if(this instanceof OpenBox)
			astCode = ((OpenBox)this).getASTCode();
		
		//Seqn
		if(this instanceof Seqn)
			astCode = ((Seqn)this).getASTCode();
		
		//SetVar
		if(this instanceof SetVar)
			astCode = ((SetVar)this).getASTCode();
		
		return astCode;
	}
}

