package org.example;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.serializers.json.*;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializerConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;


import java.util.HashMap;
import java.util.Map;


/**
 * TODO: when using springboot, this stuff can be autoconfigured just by specifying the correct properties/yaml
 *
 */
@Configuration
public class Config {

    private static final String BOOTSTRAP_SERVER = "localhost:9092";

    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";


    /* --- admin client config ---- */

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


    /** --- producer config --- */

    @Bean
    public Map<String, Object> producerConfigs(){

        return Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER,

            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            // don't use spring json serializer ...
            //ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class,

            ProducerConfig.CLIENT_ID_CONFIG, "simple-kafka-producer",


            // location of URL to load schema from
            AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL,

            // very specific settings for KafkaJsonSchemaSerializer class
            AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, Boolean.FALSE,
            AbstractKafkaSchemaSerDeConfig.LATEST_COMPATIBILITY_STRICT, Boolean.FALSE,

            // do we need specify schema version or just take the latest one?
            AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION,  Boolean.TRUE,

            // this switch is turning schema validation on
            KafkaJsonSchemaSerializerConfig.FAIL_INVALID_SCHEMA, Boolean.TRUE

        );

    }

    @Bean
    public ProducerFactory<String, Quote> producerFactory() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());

    }

    @Bean
    public KafkaTemplate<String, Quote> kafkaTemplate(){

		return new KafkaTemplate<>(producerFactory());

    }

    /* --------- consumer config ------- */

    @Bean
    public Map<String, Object> consumerProps(){
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER,
            ConsumerConfig.GROUP_ID_CONFIG, "group",
            // don't use spring json serializer
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonDeserializer.class,
            KafkaJsonSchemaDeserializerConfig.JSON_VALUE_TYPE, Quote.class,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",

            AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL

        );
    }

    // if you provide this explicitly, spring will pick it up
    @Bean
    public ConsumerFactory<String, Quote> consumerFactory(){

        return new DefaultKafkaConsumerFactory<>(consumerProps());

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Quote> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Quote> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;

    }

}

