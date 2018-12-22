package org.byoflow.services.ingesters;

import java.util.Set;

import org.byochain.commons.exceptions.BYOFlowException;
import org.byoflow.model.entity.FlowResource;

/**
 * IFlowIngester
 * @author Giuseppe Vincenzi
 *
 */
public interface IFlowIngester<T extends FlowResource> {
	void ingest(Set<T> contents) throws BYOFlowException;
}
