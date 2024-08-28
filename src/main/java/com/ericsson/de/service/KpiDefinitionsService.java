package com.ericsson.de.service;

import java.util.List;
import java.util.Set;

import com.ericsson.de.entity.KpiDefinitions;

public interface KpiDefinitionsService {

	List<KpiDefinitions> getAllKpiDefinitions();
	List<KpiDefinitions> getKpiDefinitionsByNode(String nodes);
	List<KpiDefinitions> getKpiDefinitionsByNodes(Set<String> nodes);
}
