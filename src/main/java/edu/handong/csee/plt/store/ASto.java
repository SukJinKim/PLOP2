package edu.handong.csee.plt.store;

import edu.handong.csee.plt.val.FAEValue;

public class ASto extends Store {
	int addr;
	FAEValue val;
	Store rest;
	
	public ASto(int addr, FAEValue val, Store rest) {
		super();
		this.addr = addr;
		this.val = val;
		this.rest = rest;
	}

	public int getAddr() {
		return addr;
	}

	public FAEValue getVal() {
		return val;
	}

	public Store getRest() {
		return rest;
	}

	@Override
	public String getStore() {
		
		return "(asto " + addr + " " + val.getFAEValue() + " " + rest.getStore() + ")";
	}

}
