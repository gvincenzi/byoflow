package org.byoflow.services.actuators.impl;

import java.util.Calendar;
import java.util.Set;

import org.byochain.commons.exceptions.BYOFlowException;
import org.byoflow.model.entity.FlowResource;
import org.byoflow.services.actuators.IFlowActuator;
import org.byoflow.services.ingesters.impl.WordPressIngester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("soffblog")
public class SOFFBlogActuator implements IFlowActuator<FlowResource> {
	private static Logger LOGGER = LoggerFactory.getLogger(SOFFBlogActuator.class);
	
	@Autowired
	private WordPressIngester ingester;
	
	@Override
	public void doAction(Set<FlowResource> resources) throws BYOFlowException {
		LOGGER.info(String.format("SOFFBlogActuator doAction has been called with [%d] resources",resources.size()));
		ingester.ingest(resources);
		LOGGER.info(String.format("SOFFBlogActuator doAction has been successfully with [%d] resources",resources.size()));
	}
	
	public Calendar getLastPostDate() throws BYOFlowException{
		return ingester.getLastPostDate();
	}

}
