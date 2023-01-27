package org.example;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
//import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializerConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {

    private static final String BOOTSTRAP_SERVER = "localhost:9092";

    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";

    @Bean
    public AdminClient adminClient(){

        Map<String, Object> configs = new HashMap<>();

        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);

        return AdminClient.create(configs);

    }

    @Bean
    public KafkaAdmin kafkaAdmin() {

        Map<String, Object> configs = new HashMap<>();

        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);

        return new KafkaAdmin(configs);
    }

    @Bean
    public Map<String, Object> producerConfigs(){

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        props.put(ProducerConfig.CLIENT_ID_CONFIG, "simple-kafka-producer");

        // location of URL to load schema from
        //
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);

        //very specific settings for KafkaJsonSchemaSerializer class
        props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, Boolean.FALSE);
        props.put(AbstractKafkaSchemaSerDeConfig.LATEST_COMPATIBILITY_STRICT, Boolean.FALSE);


        // do we need specify schema version or just take the latest one?
        //
        props.put(AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION,  Boolean.TRUE);

        // this switch is turning schema validation on
        //
        props.put(KafkaJsonSchemaSerializerConfig.FAIL_INVALID_SCHEMA, Boolean.TRUE);

        return props;

    }

    @Bean
    public ProducerFactory<String, Quote> producerFactory() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());

    }

    @Bean
    public KafkaTemplate<String, Quote> kafkaTemplate(){

		return new KafkaTemplate<>(producerFactory());

    }

}

