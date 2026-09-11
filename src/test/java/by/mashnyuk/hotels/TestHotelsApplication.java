package by.mashnyuk.hotels;

import org.springframework.boot.SpringApplication;

public class TestHotelsApplication {

	public static void main(String[] args) {
		SpringApplication.from(HotelsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
