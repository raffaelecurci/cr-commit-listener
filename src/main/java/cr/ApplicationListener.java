package cr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cr.generated.interf.Listener;
import cr.interf.EncryptedMessage;

@Configuration
public class ApplicationListener {
	
	@Bean
	public Listener messageListener() {
		return new Listener() {
			@Override
			public void processDb(EncryptedMessage message) {
				// TODO Auto-generated method stub
				System.out.println("received "+message);
				
			}
		};
	}
}

