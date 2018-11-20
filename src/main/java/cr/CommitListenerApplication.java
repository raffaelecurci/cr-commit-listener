package cr;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import cr.annotation.QueueDefinition;

@QueueDefinition(queues = { "db" }, excludeListeners = { "ana" })
@RefreshScope
@SpringBootApplication
@EnableRabbit
@EnableDiscoveryClient
public class CommitListenerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CommitListenerApplication.class, args);
	}

}

