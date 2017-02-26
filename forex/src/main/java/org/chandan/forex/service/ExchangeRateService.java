package org.chandan.forex.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chandan.forex.dao.ExchangeRateDAO;
import org.chandan.forex.model.ExchangeRate;
import org.chandan.forex.model.ExchangeRates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExchangeRateService {

	@Autowired
	private ExchangeRateDAO dao;

	ExchangeRateService() {		
	}
	
	public ExchangeRates getAllExchangeRates() {
		return getExchangeRates("USD", null);
	}
	
	public ExchangeRates getAllExchangeRates(String base) {
		return getExchangeRates(base, null);
	}
	
	public ExchangeRates getExchangeRateForQuote(String quote) {
		return getExchangeRates("USD", quote);
	}
	
	public ExchangeRates getExchangeRateForQuote(String base, String quote) {
		return getExchangeRates(base, quote);
	}
	
	private ExchangeRates getExchangeRates(String base, String quote) {
		
		ExchangeRates xrates = new ExchangeRates(base);
		
		if (base.equals("USD")) {
			if (StringUtils.isEmpty(quote)) {
				xrates.setExchangeRates(dao.listExchangeRates());
			}
			else {
				xrates.setExchangeRates(dao.listExchangeRates(quote));
			}
		}
		else {
			List<ExchangeRate> xratesInUSD = dao.listExchangeRates();			
			
			Map<String, Float> curr2USDRateMap = new HashMap<String, Float>();			
			for(ExchangeRate xrate: xratesInUSD) {
				curr2USDRateMap.put(xrate.getCode(), xrate.getRate());
			}
			
			float newBasePerUnitRateInUSD = curr2USDRateMap.get(base);
			List<ExchangeRate> xratesAsPerBase = new ArrayList<ExchangeRate>();
			for(ExchangeRate xrate: xratesInUSD) {
				if (StringUtils.isEmpty(quote) || quote.equals(xrate.getCode())) {				
					 float rate = xrate.getRate()/newBasePerUnitRateInUSD;
					 xratesAsPerBase.add(new ExchangeRate(xrate.getCode(), rate));
				}
			}
				
			xrates.setExchangeRates(xratesAsPerBase);
		}
		
		return xrates;
	}	
}
