package com.ericsson.de.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.ericsson.de.constants.SqlConstants;
import com.ericsson.de.entity.KafkaMeasurements;
import com.ericsson.de.entity.KpiDefinitions;
import com.ericsson.de.service.KpiDefinitionsService;
import com.ericsson.de.utils.SqlQueryConstructor;

@Component
@Configuration
public class MdcParserListener {
	
	@Value("${product.topic}")
	private String topic;
	
	public String profile;
	
	@Autowired
	KpiDefinitionsService kpiDefinitionsMeasurementsService	;
	
	@Autowired
	SqlQueryConstructor sqlQueryConstructor;
	
	SparkSession sparkSession = null;
    JavaSparkContext jsc = null;
   
	public MdcParserListener(@Value("${spring.profiles.active}") String profile){
    	this.profile = profile;
		if("dev".equals(profile)) {//for local
			sparkSession = SparkSession.builder()
								.master("local[1]")
									.appName("Mdc_Spark_Session")
											.getOrCreate();
		      jsc = new JavaSparkContext(sparkSession.sparkContext());
		}  else {//for cluster
			  sparkSession = SparkSession.builder()
			        .master("spark://10.101.216.176:7077")//service/eric-data-engine-sk
			        	.appName("Mdc_Spark_Session")
			        		.config("spark.submit.deployMode","cluster")
			        			.config("spark.driver.host","10.63.132.124")//cluster ip
			        				.config("spark.driver.bindAddress","0.0.0.0")
			        					.config("spark.driver.port","30319")//service/poc-dc-calculator-driver
			        							.config("spark.driver.blockManager.port","30318")//service/poc-dc-calculator-broadcast 
			        								.config("spark.ui.port","30320")//service/poc-dc-calculator-broadcast
			        									.getOrCreate();
			  /*sparkSession = SparkSession.builder()
					.master("spark://10.107.222.45:7077")
		        .appName("Erbs_demo_sparkSession")
		        .config("spark.submit.deployMode","cluster")
		        .config("spark.driver.host","10.63.132.124")
		        .config("spark.driver.bindAddress","0.0.0.0")
		        .config("spark.driver.port","30199")
		        .config("spark.driver.blockManager.port","30299")
		        .config("spark.ui.port","30499")
		        .getOrCreate();*/
		  jsc = new JavaSparkContext(sparkSession.sparkContext());
		}
	}
	
	
	 
	@KafkaListener(topics = "#{'${product.topic}'.split(',')}", containerFactory = "batchFactory")
    public void listen(ConsumerRecords<String, GenericRecord> consumerRecords, Acknowledgment acknowledgment) {
		Map<String,List<String>>  records = new HashMap<>();
		try {
			//reading records
			consumerRecords.forEach(record -> {
				if(records.get(record.key()) == null){
					List<String> list = new ArrayList<>();
					list.add(record.value().toString());
					records.put(record.key(),list);
				}
				else{
					List<String> list = records.get(record.key());
					list.add(record.value().toString());
				}
			});
			System.out.println("records >> "+ records);
			
			//get kpi definitions
			List<KpiDefinitions> KpiDefinitionsList 
			   = kpiDefinitionsMeasurementsService.getKpiDefinitionsByNodes(Arrays.asList(topic.split(",")).stream().collect(Collectors.toSet()));
			
			//form queries with kpi definitions
			List<String> kpiQueries = new ArrayList<>();
			KpiDefinitionsList.stream().forEach(
					kpiDefinitions -> kpiQueries.add(
							SqlConstants.SELECT + " " + kpiDefinitions.getKeys() + ","+ kpiDefinitions.getFormula() +" "+SqlConstants.AS+" "
							+ kpiDefinitions.getKpiName() + ", " +sqlQueryConstructor.columnsString(kpiDefinitions.getKafkaMeasurements())
								+" "+SqlConstants.FROM + " "+ sqlQueryConstructor.tableString(kpiDefinitions.getKafkaMeasurements()
						)));
		 
			System.out.println("kpiQueries >>" + kpiQueries);
			
			//creating maps with mo keys and empty list as values
			Map<String, List<String>> map = new HashMap<>();
			for( KpiDefinitions  kpi : KpiDefinitionsList) {
				List<KafkaMeasurements> ments = kpi.getKafkaMeasurements();
				for( KafkaMeasurements ment : ments) {
					map.put(ment.getManagedObject(), new ArrayList<String>());
				}
			}
			
			//fill data to lists in map according to keys
			for (Map.Entry<String, List<String>> entry : records.entrySet()) {
				if (map.containsKey(entry.getKey())) {
					map.get(entry.getKey()).add((entry.getValue().toString()));
				}
			}
			
			//create dataframes with map
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				JavaRDD<String> batteryBackupRDD1 = jsc.parallelize(entry.getValue());
				Dataset<Row> rowDataSet1 = sparkSession.read().json(batteryBackupRDD1);
				rowDataSet1.createOrReplaceTempView(entry.getKey());
			}
			
			//just to check dataframes
			//sparkSession.sql("select *from ExternalUtranCellFDD").show();
			
			//execute queries
			kpiQueries.forEach(query -> {
				sparkSession.sql(query).show();
			});
		 
		
		sparkSession.catalog().clearCache();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		acknowledgment.acknowledge();
	}
	
}
