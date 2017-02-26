package org.chandan.forex.model;

public class ExchangeRate {

	private String code;
	private float rate;
	
	public ExchangeRate() {		
	}
	
	public ExchangeRate(String code, float rate) {
		this.code = code;
		this.rate = rate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}	
}