package edu.handong.csee.plt.store;

public class MtSto extends Store {
	String sto;

	public MtSto() {
		this.sto = "(mtSto)";
	}

	public String getSto() {
		return sto;
	}

	@Override
	public String getStore() {
		
		return sto;
	}

}
