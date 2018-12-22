package org.byoflow;

import java.util.Set;

import org.byoflow.model.entity.FlowResource;
import org.byoflow.services.conf.DefaultServiceConfig;
import org.byoflow.services.conf.SOFFBlogServiceConfig;
import org.byoflow.services.sensors.IFlowSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({DefaultServiceConfig.class, SOFFBlogServiceConfig.class})
public class Application {
	@Autowired
	private Set<IFlowSensor<FlowResource>> sensors;

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
        	
        	// STARTING FLOW SENSORS
        	
        	for (IFlowSensor<FlowResource> sensor : sensors) {
        		sensor.start();
			}
        };
    }

}
