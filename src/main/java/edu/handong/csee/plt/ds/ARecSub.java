package edu.handong.csee.plt.ds;

import edu.handong.csee.plt.box.Box;
import edu.handong.csee.plt.val.FAEValue;

public class ARecSub extends DefrdSub {
	char name;
	Box<FAEValue> valueBox;
	DefrdSub ds;
	
	public ARecSub(char name, Box<FAEValue> valueBox, DefrdSub ds) {
		this.name = name;
		this.valueBox = valueBox;
		this.ds = ds;
	}

	public char getName() {
		return name;
	}

	public Box<FAEValue> getValueBox() {
		return valueBox;
	}

	public DefrdSub getDs() {
		return ds;
	}

	@Override
	public String getDefrdSub() {
		
		return "(aRecSub " + name + " " + valueBox.getBoxVal().getFAEValue() + " " + ds.getDefrdSub() + ")";
	}
	
}
