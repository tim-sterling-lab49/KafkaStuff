package org.example;

import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class Producer {

    private final KafkaTemplate<String, Quote> kafkaTemplate;

    private final Quotes quotes;

    public Producer(KafkaTemplate<String, Quote> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;

        this.quotes = new Quotes();

    }

    @Scheduled(fixedRate = 5_000)
    public void run(){

        var quote = this.quotes.getQuote();

        try {

            Message<Quote> message = MessageBuilder
                    .withPayload(quote)
                    .setHeader(KafkaHeaders.TOPIC, "quotes")
                    .build();

            kafkaTemplate.send(message);

        } catch (SerializationException se){

            System.out.println(se.getCause().getMessage());

        }

    }

}
