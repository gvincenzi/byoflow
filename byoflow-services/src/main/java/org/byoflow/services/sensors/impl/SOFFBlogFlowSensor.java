package org.byoflow.services.sensors.impl;

import java.util.Calendar;

import org.byochain.commons.exceptions.BYOFlowException;
import org.byoflow.services.actuators.impl.SOFFBlogActuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("soffblog")
public class SOFFBlogFlowSensor extends RSSFlowSensor{
	@Autowired
	public SOFFBlogFlowSensor(@Autowired SOFFBlogActuator rssFlowActuator) {
		super(rssFlowActuator);
	}

	@Override
	public Calendar getLastChangeDate() {
		try {
			return ((SOFFBlogActuator)getRssFlowActuator()).getLastPostDate();
		} catch (BYOFlowException e) {
			return super.getLastChangeDate();
		}
	}
}
