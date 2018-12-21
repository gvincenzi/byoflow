package org.byochain.services.sensors;

import java.util.Calendar;
import java.util.Set;

import org.byochain.commons.exceptions.BYOFlowException;
import org.byochain.model.entity.FlowResource;

/**
 * IFlowSensor
 * @author Giuseppe Vincenzi
 *
 */
public interface IFlowSensor<T extends FlowResource> {
	void onChange(Set<T> resources) throws BYOFlowException;
	public Calendar getLastChangeDate();
	void start() throws BYOFlowException;
}
