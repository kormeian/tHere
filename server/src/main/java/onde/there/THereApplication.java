package onde.there;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class THereApplication {

	public static void main(String[] args) {
		SpringApplication.run(THereApplication.class, args);
	}

	
}
