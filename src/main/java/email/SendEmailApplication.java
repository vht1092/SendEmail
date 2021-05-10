package email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableCaching
@EncryptablePropertySource(name = "EncryptedProperties", value = "classpath:encrypted.properties")
public class SendEmailApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SendEmailApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SendEmailApplication.class, args);
	}

}