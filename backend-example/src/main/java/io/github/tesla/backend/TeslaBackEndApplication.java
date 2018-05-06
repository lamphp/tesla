package io.github.tesla.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;


@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class TeslaBackEndApplication {
  public static void main(String[] args) {
    SpringApplication.run(TeslaBackEndApplication.class, args);
  }

}
