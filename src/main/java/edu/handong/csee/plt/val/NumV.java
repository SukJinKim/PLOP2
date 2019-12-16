package edu.handong.csee.plt.val;

public class NumV extends FAEValue {
	String num = "0";
	
	public NumV(String num){
		this.num = num;
	}
	
	public String getStrNum() {
		return num;
	}

	@Override
	public String getFAEValue() {
	
		return "(numV " + num +")";
	}
}
