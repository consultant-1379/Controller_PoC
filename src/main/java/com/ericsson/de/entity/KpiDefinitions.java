package com.ericsson.de.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;


@Entity
public class KpiDefinitions implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long kpiId;
	private String kpiName;
	private String formula;
	private String node;
	private String keys;
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="kpiId", referencedColumnName = "kpiId")
	private List<KafkaMeasurements> kafkaMeasurements;
	
	public Long getKpiId() {
		return kpiId;
	}
	public void setKpiId(Long kpiId) {
		this.kpiId = kpiId;
	}
	public List<KafkaMeasurements> getKafkaMeasurements() {
		return kafkaMeasurements;
	}
	public void setKafkaMeasurements(List<KafkaMeasurements> kafkaMeasurements) {
		this.kafkaMeasurements = kafkaMeasurements;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getKpiName() {
		return kpiName;
	}
	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
	
}
