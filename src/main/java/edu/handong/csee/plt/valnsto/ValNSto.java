package edu.handong.csee.plt.valnsto;

import edu.handong.csee.plt.store.Store;
import edu.handong.csee.plt.val.FAEValue;

public class ValNSto {
	FAEValue val;
	Store sto;

	public ValNSto(FAEValue val, Store sto) {
		super();
		this.val = val;
		this.sto = sto;
	}
	
	public FAEValue getVal() {
		return val;
	}
	public Store getSto() {
		return sto;
	}
	
	public String getValNSto() {
		return "(v*s " + val.getFAEValue() + " " + sto.getStore() + ")";
		
	}
	 
}
