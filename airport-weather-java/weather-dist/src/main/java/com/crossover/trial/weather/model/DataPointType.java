package com.crossover.trial.weather.model;

import com.crossover.trial.weather.exceptions.WeatherException;

/**
 * The various types of data points we can collect.
 *
 * @author code test administrator
 */
public enum DataPointType {
  WIND, TEMPERATURE, HUMIDTY, PRESSURE, CLOUDCOVER, PRECIPITATION;

  /**
   * Covert data point type string into enum
   *
   * @param dpType data point type
   */
  public static DataPointType lookup(String dpType) throws WeatherException {
    for (DataPointType dpt : values()) {
      if (dpt.toString().equals(dpType))
        return dpt;
    }
    throw new WeatherException("Invalid DataPointType value: " + dpType);
  }
}
