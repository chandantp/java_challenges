package org.chandan.forex.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.chandan.forex.model.ExchangeRates;
import org.chandan.forex.service.CurrencyService;
import org.chandan.forex.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Path("/xrates")
public class FxRates {

	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private CurrencyService currencyService;
	
	private static final Logger logger =
	        Logger.getLogger(FxRates.class.getName());
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public ExchangeRates getXrates(@QueryParam("base") String base,
										@QueryParam("quote") String quote) {
		
		if (StringUtils.isEmpty(base)) {
			if (StringUtils.isEmpty(quote)) {
				return exchangeRateService.getAllExchangeRates();
			} else if (currencyService.isValidCurrency(quote)) {
				return exchangeRateService.getExchangeRateForQuote(quote);
			} else {
				String msg = "GetXRates: Invalid quote currency code = " + quote;
				logger.severe(msg);
				throw new NotFoundException(msg);
			}
		}
		else if (currencyService.isValidCurrency(base)) {
			if (StringUtils.isEmpty(quote)) {
				return exchangeRateService.getAllExchangeRates(base);
			} else if (currencyService.isValidCurrency(quote)) {
				return exchangeRateService.getExchangeRateForQuote(base, quote);			
			} else {
				String msg = "GetXRates: Invalid quote currency code = " + quote;
				logger.severe(msg);
				throw new NotFoundException(msg);
			}
		} else {
			String msg = "GetXRates: Invalid base currency code = " + base;
			logger.severe(msg);
			throw new NotFoundException(msg);
		}
	}
}