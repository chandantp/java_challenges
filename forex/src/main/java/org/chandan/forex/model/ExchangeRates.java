package org.chandan.forex.model;

import java.util.List;

public class ExchangeRates {

	private String base;
	private long timestamp;
	private List<ExchangeRate> exchangeRates;
	
	ExchangeRates() {
		this.timestamp = System.currentTimeMillis();
		this.base = "USD";		
	}
	
	public ExchangeRates(String base) {
		this.timestamp = System.currentTimeMillis();
		this.base = base;		
	}

	public String getBase() {
		return base;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public List<ExchangeRate> getExchangeRates() {
		return exchangeRates;
	}
	
	public void setExchangeRates(List<ExchangeRate> xrates) {
		this.exchangeRates = xrates;
	}
}