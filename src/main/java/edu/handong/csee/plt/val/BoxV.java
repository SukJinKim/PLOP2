package edu.handong.csee.plt.val;

public class BoxV extends FAEValue {
	int addr;

	public BoxV(int addr) {
		super();
		this.addr = addr;
	}

	public int getAddr() {
		return addr;
	}

	@Override
	public String getFAEValue() {
		
		return "(boxV " + addr + ")";
	}
}
