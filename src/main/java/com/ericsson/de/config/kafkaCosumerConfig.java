package  com.ericsson.de.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@Configuration
public class kafkaCosumerConfig {
	@Value("${schema.registry.url}")
	private String schemaRegistryUrl;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootStrapServers;

	//@Value("${spring.kafka.consumer.max-poll-records}")
	//private int maxPollRecords;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupIds;

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		//props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupIds);
		props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		return props;
	}

	@Bean
	public ConsumerFactory<String, GenericRecord> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}

	@Bean("batchFactory")
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, GenericRecord>> batchFactory() {
		ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
		try {
			factory.setConsumerFactory(consumerFactory());
			factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
			factory.setBatchListener(true);
			factory.getContainerProperties().setSyncCommits(true);
			factory.setConcurrency(1);
			factory.setBatchErrorHandler(new SeekToCurrentBatchErrorHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return factory;
	}
}
