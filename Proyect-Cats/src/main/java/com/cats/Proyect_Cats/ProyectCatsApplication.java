package com.cats.Proyect_Cats;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ProyectCatsApplication {
    
	private static final Logger log = LoggerFactory.getLogger(ProyectCatsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ProyectCatsApplication.class, args);
		log.info("🚀 ProyectCatsApplication iniciado correctamente");
	}

}
