package org.byoflow.services.sensors;

import java.util.Calendar;
import java.util.Set;

import org.byochain.commons.exceptions.BYOFlowException;
import org.byoflow.model.entity.FlowResource;

/**
 * IFlowSensor
 * @author Giuseppe Vincenzi
 *
 */
public interface IFlowSensor<T extends FlowResource> extends Runnable {
	void onChange(Set<T> resources) throws BYOFlowException;
	public Calendar getLastChangeDate();
}
