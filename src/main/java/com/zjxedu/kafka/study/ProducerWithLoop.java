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
 *
 * 循环发送
 */
public class ProducerWithLoop implements Runnable {

    private final KafkaProducer<Integer, String> producer;

    public ProducerWithLoop() {
        Properties prop = new Properties();
        prop.put("bootstrap.servers",Kafka_Properties.KAFKA_BROKER_LIST);
        prop.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        prop.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        prop.put("partitioner.class","com.zjxedu.kafka.study.MyPartition");
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
        ProducerWithLoop producer = new ProducerWithLoop();
        new Thread(producer).start();

    }


    public void run() {
        int messageNo = 0;
        while(true){
            String messageStr = "我是zjx" + messageNo;
            producer.send(new ProducerRecord<Integer, String>(Kafka_Properties.TOPIC, messageNo, messageStr), new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("message send to:["+recordMetadata.partition()+"]");
                    System.out.println("offset:["+recordMetadata.offset()+"]");
                }
            });
            messageNo++;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
