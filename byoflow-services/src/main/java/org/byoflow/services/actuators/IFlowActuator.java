package org.byoflow.services.actuators;

import java.util.Set;

import org.byochain.commons.exceptions.BYOFlowException;
import org.byoflow.model.entity.FlowResource;

/**
 * IFlowActuator
 * @author Giuseppe Vincenzi
 *
 */
public interface IFlowActuator<T extends FlowResource> {
	void doAction(Set<T> resources) throws BYOFlowException;
}
