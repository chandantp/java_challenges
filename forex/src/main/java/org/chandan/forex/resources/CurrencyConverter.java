package org.chandan.forex.resources;

import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.chandan.forex.service.CurrencyConverterService;
import org.chandan.forex.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Path("/currency-converter")
public class CurrencyConverter {
	
	@Autowired
	private CurrencyConverterService converterService;
	
	@Autowired
	private CurrencyService currencyService;
	
	private static final Logger logger =
	        Logger.getLogger(CurrencyConverter.class.getName());
	
	@GET
    @Produces(MediaType.TEXT_PLAIN)
	public Float convertCurrency(@QueryParam("source") String source,
										@QueryParam("target") String target,
										@QueryParam("amount") Float amount) {
		
		if (StringUtils.isEmpty(source) || StringUtils.isEmpty(target) || amount == null) {
			String msg = "ConvertCurrency: One or more query parameters is empty or null!";
			logger.severe(msg);
			throw new BadRequestException(msg);
		} 
		else if (!currencyService.isValidCurrency(source) || 
				!currencyService.isValidCurrency(target)) {
			String msg = "ConvertCurrency: Source or target currency code is NOT valid!";
			logger.severe(msg);
			throw new NotFoundException(msg);
		}
		
		return converterService.convert(source, target, amount);
	}
}
