package com.ericsson.de.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ericsson.de.entity.KpiDefinitions;

@Repository
public interface KpiDefinitionsRepository extends CrudRepository<KpiDefinitions, Integer> {

	@Query("select kpi from KpiDefinitions kpi where kpi.node in (?1) ")
	List<KpiDefinitions>  findByNode(String nodes);
	@Query("select kpi from KpiDefinitions kpi where kpi.node in (?1) ")
	List<KpiDefinitions>  findByNodes(Set<String> nodes);
}
