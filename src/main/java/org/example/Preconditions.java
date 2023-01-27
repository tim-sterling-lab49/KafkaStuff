package org.example;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Preconditions {

    private final ApplicationContext ctx;

    private final AdminClient adminClient;

    public Preconditions(ApplicationContext ctx, AdminClient adminClient) throws BeansException {

        this.ctx = ctx;

        this.adminClient = adminClient;

    }

    @PostConstruct
    public void checkPreconditions() {

        try {

            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true); // includes internal topics such as __consumer_offsets

            ListTopicsResult res = adminClient.listTopics();

            res.names().whenComplete((set, err) -> {

              if(err!=null){

                  System.out.println(err.getMessage());

              } else {

                  // TODO: this is all async ... countdownlatch
                  //       inject expected topic names and compare against this.

                  System.out.println(set);

              }

            });

        } catch (Exception e){

            e.printStackTrace();

        }

    }

}
