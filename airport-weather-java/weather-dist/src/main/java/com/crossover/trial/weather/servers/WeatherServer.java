package com.crossover.trial.weather.servers;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.crossover.trial.weather.clients.AirportLoader;
import com.crossover.trial.weather.endpoints.impl.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.endpoints.impl.WeatherQueryEndpointImpl;

/**
 * This main method will be use by the automated functional grader. You shouldn't move this class or
 * remove the main method. You may change the implementation, but we encourage caution.
 *
 * @author code test administrator
 */
public class WeatherServer {

  private static final String BASE_URL_TEMPLATE = "http://%s:%s/";
  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_PORT = "8080";
  private static final String PROPERTIES_FILE = "config.properties";

  private static String baseURL;

  static {
    Properties prop = new Properties();
    try {
      prop.load(AirportLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
    } catch (Exception e) {
      // swallow
    }
    String host = (String) prop.getOrDefault("host", DEFAULT_HOST);
    String port = (String) prop.getOrDefault("port", DEFAULT_PORT);
    baseURL = String.format(BASE_URL_TEMPLATE, host, port);
  }

  public static void main(String[] args) {
    try {
      System.out.println("Starting Weather App local testing server: " + baseURL);

      final ResourceConfig resourceConfig = new ResourceConfig();
      resourceConfig.register(WeatherCollectorEndpointImpl.class);
      resourceConfig.register(WeatherQueryEndpointImpl.class);

      HttpServer server =
          GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURL), resourceConfig, false);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        server.shutdownNow();
      }));

      HttpServerProbe probe = new HttpServerProbe.Adapter() {
        @SuppressWarnings("rawtypes")
        public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection,
            Request request) {
          System.out.println(request.getRequestURI());
        }
      };
      server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);

      // the autograder waits for this output before running automated
      // tests, please don't remove it
      server.start();
      System.out.println(format("Weather Server started.\n url=%s\n", baseURL));

      // blocks until the process is terminated
      Thread.currentThread().join();
      server.shutdown();
    } catch (IOException | InterruptedException ex) {
      Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
