package com.ericsson.de.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ericsson.de.entity.KpiDefinitions;
import com.ericsson.de.repository.KpiDefinitionsRepository;

@Service
public class KpiDefinitionsServiceImp implements KpiDefinitionsService {

	@Autowired
	KpiDefinitionsRepository kpiDefinitionsRepository;
	
	@Override
	public List<KpiDefinitions> getAllKpiDefinitions() {
		return (List<KpiDefinitions>) kpiDefinitionsRepository.findAll();
	}

	@Override
	public List<KpiDefinitions>  getKpiDefinitionsByNode(String node) {
		return (List<KpiDefinitions>) kpiDefinitionsRepository.findByNode(node);
	}
	
	@Override
	public List<KpiDefinitions> getKpiDefinitionsByNodes(Set<String> nodes) {
		return (List<KpiDefinitions>) kpiDefinitionsRepository.findByNodes(nodes);
	}

	 
}
