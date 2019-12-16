package edu.handong.csee.plt.box;

public class Box<T> {
	private T boxVal;
	
	public Box(T boxVal) {
		this.boxVal = boxVal;
	}

	public T getBoxVal() {
		return boxVal;
	}

	public void setBoxVal(T boxVal) {
		this.boxVal = boxVal;
	}
	
}
