package edu.handong.csee.plt.val;

public class FAEValue {
	
	public String getFAEValue() {
		String faeValue = "";
		
		//NumV
		if(this instanceof NumV)
			faeValue = ((NumV)this).getFAEValue();
		
		
		//ClosureV
		if(this instanceof ClosureV)
			faeValue = ((ClosureV)this).getFAEValue();
		
		//ExprV
		if(this instanceof ExprV)
			faeValue = ((ExprV)this).getFAEValue();
		
		//BoxV
		if(this instanceof BoxV)
			faeValue = ((BoxV)this).getFAEValue();
		
		//RefClosV
		if(this instanceof RefClosV)
			faeValue = ((RefClosV)this).getFAEValue();
		
		return faeValue;
		
	}
}