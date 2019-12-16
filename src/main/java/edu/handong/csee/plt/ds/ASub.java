package edu.handong.csee.plt.ds;

import edu.handong.csee.plt.val.FAEValue;

public class ASub extends DefrdSub {
	char name;
	FAEValue value;
	DefrdSub ds;
	
	public ASub(char name, FAEValue value, DefrdSub ds) {
		this.name = name;
		this.value = value;
		this.ds = ds;
	}

	public char getName() {
		return name;
	}

	public FAEValue getValue() {
		return value;
	}

	public DefrdSub getDs() {
		return ds;
	}
	
	@Override
	public String getDefrdSub() {
		
		return "(aSub " + name + " " + value.getFAEValue() + " " + ds.getDefrdSub() + ")";
	}
}
