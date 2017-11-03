package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.endpoints.WeatherCollectorEndpoint;
import com.crossover.trial.weather.endpoints.WeatherQueryEndpoint;
import com.crossover.trial.weather.endpoints.impl.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.endpoints.impl.WeatherQueryEndpointImpl;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericData;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.services.AirportService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WeatherEndpointTest {

  private WeatherQueryEndpoint _query = new WeatherQueryEndpointImpl();

  private WeatherCollectorEndpoint _collector = new WeatherCollectorEndpointImpl();

  private Gson _gson = new Gson();

  private DataPoint _dp;

  private void loadTestData() {
    AirportService.clearAll();
    _collector.addAirport("BOS", "42.364347", "-71.005181");
    _collector.addAirport("EWR", "40.6925", "-74.168667");
    _collector.addAirport("JFK", "40.639751", "-73.778925");
    _collector.addAirport("LGA", "40.777245", "-73.872608");
    _collector.addAirport("MMU", "40.79935", "-74.4148747");
  }

  @Before
  public void setUp() throws Exception {
    loadTestData();
    _dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30)
        .withMean(22).build();
    _collector.updateWeather("BOS", "wind", _gson.toJson(_dp));
    _query.weather("BOS", "0").getEntity();
  }

  @Test
  public void testQueryPing() throws Exception {
    String ping = _query.ping();
    JsonElement pingResult = new JsonParser().parse(ping);
    assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
    assertEquals(5,
        pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetWeather() throws Exception {
    List<AtmosphericData> ais = (List<AtmosphericData>) _query.weather("BOS", "0").getEntity();
    assertEquals(ais.get(0).getWind(), _dp);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetWeatherNearby() throws Exception {
    // check datasize response
    _collector.updateWeather("JFK", "wind", _gson.toJson(_dp));
    _dp.setMean(40);
    _collector.updateWeather("EWR", "wind", _gson.toJson(_dp));
    _dp.setMean(30);
    _collector.updateWeather("LGA", "wind", _gson.toJson(_dp));

    List<AtmosphericData> ais = (List<AtmosphericData>) _query.weather("JFK", "200").getEntity();
    assertEquals(3, ais.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUpdateWeather() throws Exception {

    DataPoint windDp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20)
        .withLast(30).withMean(22).build();
    _collector.updateWeather("BOS", "wind", _gson.toJson(windDp));
    _query.weather("BOS", "0").getEntity();

    String ping = _query.ping();
    JsonElement pingResult = new JsonParser().parse(ping);
    assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

    DataPoint cloudCoverDp = new DataPoint.Builder().withCount(4).withFirst(10).withMedian(60)
        .withLast(100).withMean(50).build();
    _collector.updateWeather("BOS", "cloudcover", _gson.toJson(cloudCoverDp));

    List<AtmosphericData> ais = (List<AtmosphericData>) _query.weather("BOS", "0").getEntity();
    assertEquals(ais.get(0).getWind(), windDp);
    assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
  }

  @Test
  public void testCollectPing() throws Exception {
    String response = (String) _collector.ping().getEntity();
    assertEquals(response, "ready");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAirports() throws Exception {
    Set<String> airports = (Set<String>) _collector.getAirports().getEntity();
    String[] expected = {"BOS", "EWR", "JFK", "LGA", "MMU"};
    airports.removeAll(Arrays.asList(expected));
    assertEquals(0, airports.size());
  }

  @Test
  public void testGetAirport() throws Exception {
    AirportData ad1 = (AirportData) _collector.getAirport("BOS").getEntity();
    assertEquals("BOS", ad1.getIata());
    AirportData ad2 = (AirportData) _collector.getAirport("XYZ").getEntity();
    assertEquals(null, ad2);
  }

  @Test
  public void testAddAirport() throws Exception {
    _collector.addAirport("BLR", "22.22", "33.33");
    AirportData ad = (AirportData) _collector.getAirport("BLR").getEntity();
    assertEquals("BLR", ad.getIata());
  }

  @Test
  public void testDeleteAirport() throws Exception {
    _collector.deleteAirport("MMU");
    AirportData mmu = (AirportData) _collector.getAirport("MMU").getEntity();
    assertNull(mmu);
  }
}
