package com.crossover.trial.weather.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import com.crossover.trial.weather.exceptions.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericData;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;

public class AirportService {

  private final static Logger LOGGER = Logger.getLogger(AirportService.class.getName());

  /** earth radius in KM */
  private static final double EARTH_RADIUS_IN_KMS = 6372.8;

  private static final long ONE_DAY_IN_MILLIS = 86400000;

  /** all known airports */
  protected static Map<String, AirportData> iata2airportData = new HashMap<>();

  /** atmospheric information for each airport */
  protected static Map<String, AtmosphericData> iata2atmosphericData = new HashMap<>();

  /**
   * Internal performance counter to better understand most requested information, this map can be
   * improved but for now provides the basis for future performance optimizations. Due to the
   * stateless deployment architecture we don't want to write this to disk, but will pull it off
   * using a REST request and aggregate with other performance metrics {@link #ping()}
   */
  private static Map<String, Integer> iata2requestFrequency = new HashMap<>();

  private static Map<String, Integer> radius2Frequency = new HashMap<>();

  private static AirportService _instance = new AirportService();

  private AirportService() {}

  public static AirportService instance() {
    return _instance;
  }

  /*
   * AirportData related methods
   */
  public synchronized Collection<AirportData> getAirports() {
    return iata2airportData.values();
  }

  public synchronized AirportData getAirport(String iata) {
    return iata2airportData.get(iata);
  }

  public synchronized void addAirport(String iata, double latitude, double longitude) {
    AirportData ad = new AirportData(iata, latitude, longitude);
    iata2airportData.put(iata, ad);
    iata2atmosphericData.put(iata, new AtmosphericData());
    iata2requestFrequency.put(iata, 0);
  }

  public synchronized void removeAirport(String iata) {
    iata2airportData.remove(iata);
    iata2atmosphericData.remove(iata);
    iata2requestFrequency.remove(iata);
  }

  /*
   * AtmosphericData related methods
   */
  public synchronized AtmosphericData getAtmosphericData(String iata) {
    return iata2atmosphericData.get(iata);
  }

  public synchronized void updateAtmosphericData(String iata, String pointType, DataPoint dp)
      throws WeatherException {
    AtmosphericData ad = iata2atmosphericData.get(iata);
    if (ad == null) {
      LOGGER.warning("Unknown iata code = " + iata);
      throw new WeatherException("Unknown iata code = " + iata);
    }
    DataPointType dpType = DataPointType.lookup(pointType.toUpperCase());
    switch (dpType) {
      case WIND:
        if (dp.getMean() >= 0)
          ad.setWind(dp);
        break;
      case TEMPERATURE:
        if (dp.getMean() >= -50 && dp.getMean() < 100)
          ad.setTemperature(dp);
        break;
      case HUMIDTY:
        if (dp.getMean() >= 0 && dp.getMean() < 100)
          ad.setHumidity(dp);
        break;
      case PRESSURE:
        if (dp.getMean() >= 650 && dp.getMean() < 800)
          ad.setPressure(dp);
        break;
      case CLOUDCOVER:
        if (dp.getMean() >= 0 && dp.getMean() < 100)
          ad.setCloudCover(dp);
        break;
      case PRECIPITATION:
        if (dp.getMean() >= 0 && dp.getMean() < 100)
          ad.setPrecipitation(dp);
        break;
      default:
        throw new WeatherException("Invalid DataPointType value: " + dpType);
    }
    ad.setLastUpdateTime(System.currentTimeMillis());
  }

  /*
   * RequestFrequency related methods
   */
  public synchronized void updateRequestFrequency(String iata) {
    iata2requestFrequency.put(iata, iata2requestFrequency.getOrDefault(iata, 0) + 1);
  }

  /*
   * RadiusFrequency related methods
   */
  public synchronized void updateRadiusFrequency(String radius) {
    radius2Frequency.put(radius, radius2Frequency.getOrDefault(radius, 0) + 1);
  }

  /*
   * Query endpoint methods
   */
  public Map<String, Object> queryPing() {
    Collection<AtmosphericData> atmosphericData = null;
    Set<Entry<String, Integer>> iata2requestFrequencyEntries = null;
    Set<Entry<String, Integer>> radius2FrequencyEntries = null;
    Optional<Double> maxRadius = null;
    int fsize = 0;

    synchronized (this) {
      atmosphericData = iata2atmosphericData.values();
      fsize = iata2requestFrequency.size();
      iata2requestFrequencyEntries = iata2requestFrequency.entrySet();
      radius2FrequencyEntries = radius2Frequency.entrySet();
      maxRadius = radius2Frequency.keySet().stream().map(Double::parseDouble)
          .max((d1, d2) -> Double.compare(d1, d2));
    }

    Map<String, Object> retval = new HashMap<>();
    int datasize = 0;
    for (AtmosphericData ai : atmosphericData) {
      // we only count recent readings i.e. updated in the last day
      if (isAtmosphericDataNotNull(ai)
          && (ai.getLastUpdateTime() > System.currentTimeMillis() - ONE_DAY_IN_MILLIS)) {
        datasize++;
      }
    }
    retval.put("datasize", datasize);

    // fraction of queries
    Map<String, Double> freq = new HashMap<>();
    for (Map.Entry<String, Integer> e : iata2requestFrequencyEntries) {
      double frac = fsize != 0 ? (double) e.getValue() / fsize : 0;
      freq.put(e.getKey(), frac);
    }
    retval.put("iata_freq", freq);

    int length = maxRadius.orElse(1000.0).intValue() + 1;
    int[] history = new int[length];
    for (Map.Entry<String, Integer> e : radius2FrequencyEntries) {
      int i = Double.valueOf(e.getKey()).intValue();
      history[i] += e.getValue();
    }
    retval.put("radius_freq", history);
    return retval;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<AtmosphericData> getWeatherNearBy(String iata, String radius)
      throws WeatherException {
    AirportData ad1 = null;
    Map<String, AtmosphericData> iata2atmosphericDataClone = null;
    Collection<AirportData> airports = null;

    synchronized (this) {
      ad1 = AirportService.instance().getAirport(iata);
      if (ad1 == null) {
        LOGGER.warning("Unknown iata code = " + iata);
        throw new WeatherException("Unknown iata code = " + iata);
      }
      airports = iata2airportData.values();
      iata2atmosphericDataClone =
          (Map<String, AtmosphericData>) ((HashMap) iata2atmosphericData).clone();
      iata2requestFrequency.put(iata, iata2requestFrequency.getOrDefault(iata, 0) + 1);
      radius2Frequency.put(radius, radius2Frequency.getOrDefault(radius, 0) + 1);
    }

    List<AtmosphericData> retval = new ArrayList<>();
    for (AirportData ad2 : airports) {
      AtmosphericData ai = iata2atmosphericDataClone.get(ad2.getIata());
      if (ai != null && isAtmosphericDataNotNull(ai)
          && calculateDistance(ad1, ad2) <= Double.valueOf(radius)) {
        retval.add(ai);
      }
    }
    return retval;
  }

  /**
   * Haversine distance between two airports.
   *
   * @param ad1 airport 1
   * @param ad2 airport 2
   * @return the distance in KM
   */
  private double calculateDistance(AirportData ad1, AirportData ad2) {
    double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
    double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
    double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
        * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
    double c = 2 * Math.asin(Math.sqrt(a));
    return EARTH_RADIUS_IN_KMS * c;
  }

  private boolean isAtmosphericDataNotNull(AtmosphericData ad) {
    if (ad.getCloudCover() != null || ad.getHumidity() != null || ad.getPressure() != null
        || ad.getPrecipitation() != null || ad.getTemperature() != null || ad.getWind() != null)
      return true;
    return false;
  }

  /*
   * For unit test purposes
   */
  public synchronized static void clearAll() {
    iata2airportData.clear();
    iata2atmosphericData.clear();
    iata2requestFrequency.clear();
    radius2Frequency.clear();
  }
}
