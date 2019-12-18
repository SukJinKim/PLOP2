package edu.handong.csee.plt.ds;

//import edu.handong.csee.plt.val.FAEValue;

public class ASub extends DefrdSub {
	char name;
//	FAEValue value;
	int addr;
	DefrdSub ds;
	
//	public ASub(char name, FAEValue value, DefrdSub ds) {
//		this.name = name;
//		this.value = value;
//		this.ds = ds;
//	}

	public ASub(char name, int addr, DefrdSub ds) {
		super();
		this.name = name;
		this.addr = addr;
		this.ds = ds;
	}
	
	public char getName() {
		return name;
	}

//	public FAEValue getValue() {
//		return value;
//	}
	public int getAddr() {
		return addr;
	}

	public DefrdSub getDs() {
		return ds;
	}
	
	@Override
	public String getDefrdSub() {
		
		return "(aSub " + name + " " + addr + " " + ds.getDefrdSub() + ")";
//		return "(aSub " + name + " " + value.getFAEValue() + " " + ds.getDefrdSub() + ")";
	}
}
