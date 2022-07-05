package com.tiendaonline.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.tiendaonline.main.entities")
@EnableJpaRepositories("com.tiendaonline.main.repositories")
@SpringBootApplication(scanBasePackages = "com.tiendaonline.main")
public class TiendaonlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaonlineApplication.class, args);
	}

}
