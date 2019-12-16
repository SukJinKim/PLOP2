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
		
		
		
		return faeValue;
		
	}
}