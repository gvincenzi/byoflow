package org.byoflow.services.conf;

import java.util.HashSet;
import java.util.Set;

import org.byoflow.model.entity.FlowResource;
import org.byoflow.services.sensors.IFlowSensor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("default")
@PropertySource("classpath:application-service.properties")
public class DefaultServiceConfig {
	@Bean
	Set<IFlowSensor<FlowResource>> sensors(){
		Set<IFlowSensor<FlowResource>> sensors = new HashSet<>();
		
		return sensors;
	}
}
