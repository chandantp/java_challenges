package com.crossover.trial.weather.clients;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * @author code test administrator
 */
public class AirportLoader {

  /** end point for read queries */
  @SuppressWarnings("unused")
  private static WebTarget query;

  /** end point to supply updates */
  private static WebTarget collect;

  private static final String BASE_URI = "http://%s:%s/%s";
  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_PORT = "8080";
  private static final String PROPERTIES_FILE = "config.properties";

  private static enum AirportDataType {
    SERIAL_NO, NAME, CITY, COUNTRY, IATA, ICAO, LATITUDE, LONGITUDE, ALTITUDE, TZ_OFFSET, DST
  }

  public AirportLoader() {
    Properties prop = new Properties();
    try {
      prop.load(AirportLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
    } catch (Exception e) {
      // swallow
    }
    String host = (String) prop.getOrDefault("host", DEFAULT_HOST);
    String port = (String) prop.getOrDefault("port", DEFAULT_PORT);

    Client client = ClientBuilder.newClient();
    query = client.target(String.format(BASE_URI, host, port, "query"));
    collect = client.target(String.format(BASE_URI, host, port, "collect"));
  }

  private void addAirport(String iata, String latitude, String longitude) {
    String airport = String.format("/airport/%s/%s/%s", iata, latitude, longitude);
    Response response =
        collect.path(airport).request().post(Entity.entity(null, MediaType.TEXT_PLAIN));
    if (Status.fromStatusCode(response.getStatus()) != Response.Status.OK
        && Status.fromStatusCode(response.getStatus()) != Response.Status.ACCEPTED) {
      System.out.println(String.format("Failed to add aiport = %s : %s : %s, HTTP Response = %d",
          iata, latitude, longitude, response.getStatus()));
    }
  }

  private void upload(File file) throws IOException {
    String line;
    BufferedReader br = new BufferedReader(new FileReader(file));
    while ((line = br.readLine()) != null) {
      String[] tokens = line.split(",");
      String iata = tokens[AirportDataType.IATA.ordinal()].replace("\"", "");
      String latitude = tokens[AirportDataType.LATITUDE.ordinal()];
      String longitude = tokens[AirportDataType.LONGITUDE.ordinal()];
      addAirport(iata, latitude, longitude);
    }
    br.close();
  }

  public static void main(String args[]) throws IOException {
    if (args.length != 1) {
      System.err.println("USAGE: java com.crossover.trial.weather.AirportLoader <FILE_PATH>");
      System.exit(1);
    }

    File airportDataFile = new File(args[0]);
    if (!airportDataFile.exists() || airportDataFile.length() == 0) {
      System.err.println(airportDataFile + " is not a valid input");
      System.exit(1);
    }

    AirportLoader al = new AirportLoader();
    al.upload(airportDataFile);
    System.exit(0);
  }
}
