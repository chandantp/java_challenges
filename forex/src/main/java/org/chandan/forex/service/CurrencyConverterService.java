package org.chandan.forex.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chandan.forex.dao.ExchangeRateDAO;
import org.chandan.forex.model.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrencyConverterService {

	@Autowired
	private ExchangeRateDAO dao;

	CurrencyConverterService() {		
	}

	public float convert(String source, String target, float amount) {
		List<ExchangeRate> xratesInUSD = dao.listExchangeRates();			
		
		Map<String, Float> curr2USDRateMap = new HashMap<String, Float>();			
		for(ExchangeRate xrate: xratesInUSD) {
			curr2USDRateMap.put(xrate.getCode(), xrate.getRate());
		}
		
		float amountInUSD = amount * curr2USDRateMap.get(source);
		float targetPerUnitRateInUSD = curr2USDRateMap.get(target);
		
		return (amountInUSD/targetPerUnitRateInUSD);
	}
}