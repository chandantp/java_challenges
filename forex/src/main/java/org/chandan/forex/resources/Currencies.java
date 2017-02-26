package org.chandan.forex.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.chandan.forex.model.Currency;
import org.chandan.forex.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/currencies")
public class Currencies {

	@Autowired
	private CurrencyService service;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public List<Currency> getCurrencies() {
		return service.getCurrencies();
	}
}