package  com.ericsson.de.listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.ericsson.de.entity.KafkaMeasurements;
import com.ericsson.de.entity.KpiDefinitions;

@Component
@Configuration
public class KafkaConsumer {

	@Value("product.topic")
	private String topics;
	
	//collecting number of records for calculation
	int waitCount = 3;

	List<KpiDefinitions> KpiDefinitionsList;
    List<KafkaMeasurements> kafkaMeasurements;
    List<KpiDefinitions> kpiDefinitionsMeasurementsList;
    Map<String,GenericRecord> erbsRecords = new ConcurrentHashMap<>();
    
    SparkSession sparkSession = SparkSession.builder().master("local[1]").appName("sparkSession_"+topics).getOrCreate();
    JavaSparkContext jsc = new JavaSparkContext(sparkSession.sparkContext());

	@KafkaListener(topics = "${product.topic}", containerFactory = "batchFactory")
	public void listen(ConsumerRecords<String, GenericRecord> consumerRecords, Acknowledgment acknowledgment) {

		try {
			consumerRecords.forEach(record -> {
				erbsRecords.put(record.key(), record.value());
			});

			System.out.println("erbsRecords >>" + erbsRecords);
			/*
			if (waitCount == erbsRecords.size()) {
				List<String> mos = new ArrayList<>();
				kpiDefinitionsMeasurementsList = kpiDefinitionsMeasurementsService.getKpiDefinitionsByNode(topics);
				List<String> queries = new ArrayList<String>();
				for (KpiDefinitions def : kpiDefinitionsMeasurementsList) {
					List<KafkaMeasurements> mlist = def.getKafkaMeasurements();

					String tables="", othercols="";
					for (KafkaMeasurements m : mlist) {

						System.out.println(def.getKpiName() + "==" + def.getFormula() + "==" + def.getNode());
						String mo = m.getManagedObject();

						mos.add(mo);
						List<String> cols = Arrays.asList(def.getKeys().split(",")).stream().distinct()
								.collect(Collectors.toList());
						for (String col : cols) {
							tables = tables + mo + ", ";
						}
						for (String col : cols) {
							othercols = othercols + mo + "." + col + ", ";
						}
					}		 
					String query = "select " + def.getFormula() + "   from " + tables;	
					System.out.println("query >>" + query);
					queries.add(query.substring(0, query.length() - 2));
				}
				Map<String, List<String>> map = new HashMap<>();
				mos.forEach(x -> map.put(x, new ArrayList<String>()));

				for (Map.Entry<String, GenericRecord> entry : erbsRecords.entrySet()) {
					if (map.containsKey(entry.getKey())) {
						map.get(entry.getKey()).add((entry.getValue()).toString());
					}
				}
				for (Map.Entry<String, List<String>> entry : map.entrySet()) {
					JavaRDD<String> batteryBackupRDD1 = jsc.parallelize(entry.getValue());
					Dataset<Row> rowDataSet1 = sparkSession.read().json(batteryBackupRDD1);
					String st = entry.getKey();
					rowDataSet1.createOrReplaceTempView(st);
				}
				
				//just to check dataframes
				sparkSession.sql("select *from BatteryBackup").show();
				sparkSession.sql("select * from CapacityConnectedUsers").show();
				sparkSession.sql("select * from SecurityHandling").show();

				queries.forEach(query -> {
					sparkSession.sql(query).show();
				});
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		acknowledgment.acknowledge();
	}
}
