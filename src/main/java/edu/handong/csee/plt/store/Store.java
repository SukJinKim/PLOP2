package edu.handong.csee.plt.store;

public class Store {
	public String getStore() {
		String store = "";
		
		//MtSto
		if(this instanceof MtSto)
			store = ((MtSto)this).getStore();
		
		//ASto
		if(this instanceof ASto)
			store = ((ASto)this).getStore();
		
		return store;
	}

}
