package org.example;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @KafkaListener(topics = "quotes", groupId = "quote-listener")
    public void consume(Quote quote){

        System.out.println(quote.getSymbol());

    }

}
