package edu.handong.csee.plt.ast;

public class Id extends AST {
	char id;

	public Id(char id) {
		this.id = id;
	}

	public char getId() {
		return id;
	}

	@Override
	public String getASTCode() {
		return "(id " + id  +")"; //(id sexp)
	}
}
