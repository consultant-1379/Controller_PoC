package com.ericsson.de.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class KafkaMeasurements implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long kafkaMeasurementsId;
	private String node;
	private String managedObject;
	private String counters;

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getManagedObject() {
		return managedObject;
	}

	public void setManagedObject(String managedObject) {
		this.managedObject = managedObject;
	}

	public String getCounters() {
		return counters;
	}

	public void setCounters(String counters) {
		this.counters = counters;
	}

}
