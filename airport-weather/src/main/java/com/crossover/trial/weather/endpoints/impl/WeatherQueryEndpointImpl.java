package com.crossover.trial.weather.endpoints.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.endpoints.WeatherQueryEndpoint;
import com.crossover.trial.weather.exceptions.WeatherException;
import com.crossover.trial.weather.model.AtmosphericData;
import com.crossover.trial.weather.services.AirportService;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently,
 * all data is held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class WeatherQueryEndpointImpl implements WeatherQueryEndpoint {

  private final static Logger LOGGER = Logger.getLogger(WeatherQueryEndpointImpl.class.getName());

  /** shared gson json to object factory */
  private static final Gson gson = new Gson();

  /**
   * Retrieve service health including total size of valid data points and request frequency
   * information.
   *
   * @return health stats for the service as a string
   */
  @Override
  public String ping() {
    Map<String, Object> retval = AirportService.instance().queryPing();
    return gson.toJson(retval);
  }

  /**
   * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport
   * information and return a list of matching atmosphere information.
   *
   * @param iata the iataCode
   * @param radiusString the radius in km
   *
   * @return a list of atmospheric information
   */
  @Override
  public Response weather(String iata, String radiusString) {
    LOGGER.fine(String.format("query = %s : %s", iata, radiusString));
    String radius = radiusString == null || radiusString.trim().isEmpty() ? "0" : radiusString;
    List<AtmosphericData> retval;
    try {
      retval = AirportService.instance().getWeatherNearBy(iata, radius);
    } catch (WeatherException e) {
      LOGGER.warning("Exception occurred: e = " + e.getMessage());
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.status(Response.Status.OK).entity(retval).build();
  }
}
