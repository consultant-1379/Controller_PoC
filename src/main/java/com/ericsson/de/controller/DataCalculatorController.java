package com.ericsson.de.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.de.entity.KpiDefinitions;
import com.ericsson.de.service.KpiDefinitionsService;

@RestController
public class DataCalculatorController {

	private static final String APP_RUNNING_STATUS = "UP";

	@Autowired
	private KpiDefinitionsService kpiDefinitionsService;

	@GetMapping("app-status")
	public String getAppRunningStatus() {
		return APP_RUNNING_STATUS;
	}

	@GetMapping("getallkpidefinitions")
	public List<KpiDefinitions> getAllKpiDefinitions() {
		return kpiDefinitionsService.getAllKpiDefinitions();
	}
	
	@GetMapping("getkpidefinitions/{node}")
	public List<KpiDefinitions> getKpiDefinitionsByNode(@PathVariable String node) {
		return kpiDefinitionsService.getKpiDefinitionsByNode(node);
	}
	
}
