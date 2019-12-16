package edu.handong.csee.plt.ds;

public class MtSub extends DefrdSub{
	String ds;

	public MtSub() {
		this.ds = "(mtSub)";
	}

	public String getStrDs() {
		return ds;
	}

	@Override
	public String getDefrdSub() {
		
		return ds;
	}
}
