package com.zjxedu.kafka.study;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Think
 *
 * 消息发送
 */
public class Producer{

    private final KafkaProducer<Integer, String> producer;

    public Producer() {
        Properties prop = new Properties();
        prop.put("bootstrap.servers",Kafka_Properties.KAFKA_BROKER_LIST);
        prop.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        prop.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

        prop.put("client.id","producerDemo");

        this.producer = new KafkaProducer<Integer, String>(prop);
    }

    public void sendMap(){
        producer.send(new ProducerRecord<Integer, String>(Kafka_Properties.TOPIC, 1, "我是zjx 你是谁"), new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                System.out.println("message send to:["+recordMetadata.partition()+"]");
                System.out.println("offset:["+recordMetadata.offset()+"]");
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Producer producer = new Producer();
        producer.sendMap();
    }

}
