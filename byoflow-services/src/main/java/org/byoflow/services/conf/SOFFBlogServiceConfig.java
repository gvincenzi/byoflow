package org.byoflow.services.conf;

import java.util.HashSet;
import java.util.Set;

import org.byoflow.model.entity.FlowResource;
import org.byoflow.services.sensors.IFlowSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("soffblog")
@PropertySource("classpath:application-service-soffblog.properties")
public class SOFFBlogServiceConfig {
	@Autowired
	private IFlowSensor<FlowResource> soffBlogRssFlowSensor;
	
	@Bean(name = "sensors")
	Set<IFlowSensor<FlowResource>> sensors(){
		Set<IFlowSensor<FlowResource>> sensors = new HashSet<>();
		sensors.add(soffBlogRssFlowSensor);
		
		return sensors;
	}
}
