package com.ericsson.de.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ericsson.de.entity.KafkaMeasurements;

@Component
public class SqlQueryConstructor {
	public String columnsString(List<KafkaMeasurements> measurements) {
		Set<String> columnsSet = new HashSet<>();
		for(KafkaMeasurements measurement : measurements) 
			columnsSet.add(measurement.getManagedObject() +"."+measurement.getCounters());
		return String.join(",", columnsSet);
	}
	public String tableString(List<KafkaMeasurements> measurements) {
		Set<String> tablesSet = new HashSet<>();
		for(KafkaMeasurements measurement : measurements) 
			tablesSet.add(measurement.getManagedObject() +" AS "+measurement.getManagedObject());
		return String.join(",", tablesSet);
	}
}
