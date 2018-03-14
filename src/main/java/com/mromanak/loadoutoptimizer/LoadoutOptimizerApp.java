package com.mromanak.loadoutoptimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.mromanak"
})
public class LoadoutOptimizerApp {

    public static void main(String[] args) {
        SpringApplication.run(LoadoutOptimizerApp.class, args);
    }
}
