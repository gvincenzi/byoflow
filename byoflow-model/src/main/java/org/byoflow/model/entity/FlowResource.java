package org.byoflow.model.entity;

import java.util.Calendar;

public class FlowResource {
	private String name;
	private String description;
	private Calendar startDateOfValidity;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the startDateOfValidity
	 */
	public Calendar getStartDateOfValidity() {
		if(startDateOfValidity == null){
			setStartDateOfValidity(Calendar.getInstance());
		}
		return startDateOfValidity;
	}

	/**
	 * @param startDateOfValidity
	 *            the startDateOfValidity to set
	 */
	public void setStartDateOfValidity(Calendar startDateOfValidity) {
		this.startDateOfValidity = startDateOfValidity;
	}
}
