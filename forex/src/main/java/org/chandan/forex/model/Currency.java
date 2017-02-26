package org.chandan.forex.model;

public class Currency {

	private String code;	
	private String displayName;
	
	public Currency() {		
	}
	
	public Currency(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}