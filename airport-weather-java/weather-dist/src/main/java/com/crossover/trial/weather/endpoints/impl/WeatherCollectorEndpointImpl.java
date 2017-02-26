package com.crossover.trial.weather.endpoints.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.endpoints.WeatherCollectorEndpoint;
import com.crossover.trial.weather.exceptions.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.services.AirportService;
import com.google.gson.Gson;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class WeatherCollectorEndpointImpl implements WeatherCollectorEndpoint {
  private final static Logger LOGGER =
      Logger.getLogger(WeatherCollectorEndpointImpl.class.getName());

  /** shared gson json to object factory */
  private final static Gson gson = new Gson();

  @Override
  public Response ping() {
    return Response.status(Response.Status.OK).entity("ready").build();
  }

  @Override
  public Response updateWeather(String iata, String pointType, String datapointJson) {
    LOGGER.fine(String.format("updateWeather = %s : %s : %s", iata, pointType, datapointJson));
    try {
      AirportService.instance().updateAtmosphericData(iata, pointType,
          gson.fromJson(datapointJson, DataPoint.class));
      return Response.status(Response.Status.OK).build();
    } catch (WeatherException e) {
      LOGGER.warning("Exception occurred: e = " + e.getMessage());
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @Override
  public Response getAirports() {
    Set<String> retval = new HashSet<>();
    for (AirportData ad : AirportService.instance().getAirports()) {
      retval.add(ad.getIata());
    }
    return Response.status(Response.Status.OK).entity(retval).build();
  }

  @Override
  public Response getAirport(String iata) {
    LOGGER.fine("getAirport: iata code = " + iata);
    AirportData ad = AirportService.instance().getAirport(iata);
    if (ad == null) {
      LOGGER.warning("Unknown iata code = " + iata);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.status(Response.Status.OK).entity(ad).build();
  }

  @Override
  public Response addAirport(String iata, String latString, String longString) {
    LOGGER.fine(String.format("addAirport = %s : %s : %s", iata, latString, longString));
    AirportService.instance().addAirport(iata, Double.valueOf(latString),
        Double.valueOf(longString));
    return Response.status(Response.Status.OK).build();
  }

  @Override
  public Response deleteAirport(String iata) {
    LOGGER.fine("deleteAirport: iata code = " + iata);
    AirportService.instance().removeAirport(iata);
    return Response.status(Response.Status.OK).build();
  }

  @Override
  public Response exit() {
    LOGGER.info("Receieved exit command, shutting down!");
    System.exit(0);
    return Response.noContent().build();
  }
}
