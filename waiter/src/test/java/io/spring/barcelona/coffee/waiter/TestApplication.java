package io.spring.barcelona.coffee.waiter;

import org.springframework.boot.SpringApplication;

public class TestApplication {
  public static void main(String[] args) {
    SpringApplication
      .from(WaiterApplication::main)
      .with(ContainersConfig.class)
      .run(args);
  }
}
