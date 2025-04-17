package ml.virtualthreads.structuredconcurrency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class FrontendApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(FrontendApplication.class, args);
	}
}
