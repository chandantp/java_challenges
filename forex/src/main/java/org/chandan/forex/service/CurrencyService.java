package org.chandan.forex.service;

import java.util.List;

import org.chandan.forex.dao.CurrencyDAO;
import org.chandan.forex.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrencyService {

	@Autowired
	private CurrencyDAO dao;

	CurrencyService() {		
	}
	
	public List<Currency> getCurrencies() {
		return dao.listCurrencies();
	}
	
	public boolean isValidCurrency(String quote) {
		return (dao.getCurrency(quote) != null);
	}	
}