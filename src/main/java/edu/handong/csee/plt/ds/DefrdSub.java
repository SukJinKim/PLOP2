package edu.handong.csee.plt.ds;

public class DefrdSub {
	

	public String getDefrdSub() {
		String defrdSub = "" ;
		
		//MtSub
		if(this instanceof MtSub)
			defrdSub = ((MtSub)this).getDefrdSub();
				
		//ASub
		if(this instanceof ASub)
			defrdSub = ((ASub)this).getDefrdSub();
		
		//ARecSub
		if(this instanceof ARecSub)
			defrdSub = ((ARecSub)this).getDefrdSub();
			
		
		return defrdSub;
		
	}

}
