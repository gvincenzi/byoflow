package org.byochain.services.actuators.impl;

import java.util.Calendar;
import java.util.Set;

import org.byochain.commons.exceptions.BYOFlowException;
import org.byochain.model.entity.FlowResource;
import org.byochain.services.actuators.IFlowActuator;
import org.byochain.services.ingesters.impl.WordPressIngester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("soffblog")
public class SOFFBlogActuator implements IFlowActuator<FlowResource> {
	@Autowired
	private WordPressIngester ingester;
	
	@Override
	public void doAction(Set<FlowResource> resources) throws BYOFlowException {
		ingester.ingest(resources);
	}
	
	public Calendar getLastPostDate() throws BYOFlowException{
		return ingester.getLastPostDate();
	}

}
