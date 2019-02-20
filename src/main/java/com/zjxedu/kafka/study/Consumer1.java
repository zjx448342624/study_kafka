package com.zjxedu.kafka.study;

import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

/**
 * @author zjx
 *
 * 自动提交
 */
public class Consumer1 extends ShutdownableThread {
    //High Level consumer 里面封装了大量的信息细节和复杂的操作，如果不需要定制化的东西就使用这个
    //Low level consumer  里面的信息更加的灵活可以动态的更改offset等，这个的工作量比较多，如果需要定制化的东西可以去使用这个

    private final KafkaConsumer<Integer, String> consumer;

    public Consumer1() {
        super("KafkaConsumerTest", false);

        Properties props = new Properties();
        // 服务器地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Kafka_Properties.KAFKA_BROKER_LIST);
        //group id  消息所属的分组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoGroup1");

        // 消息是否自动提交：offst
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // 消息自动提交的间隔
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // 设置使用最开始的offset偏移量为当前group.id 的最早的消息
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //设置心跳时间
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        //对key和value设置反序列化对象
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        this.consumer = new KafkaConsumer<Integer, String>(props);

        /*本段代码用来实现kafka的指定分区消费，指定分区消费和dowork的topic互斥，只能开一个
        TopicPartition p0 = new TopicPartition(Kafka_Properties.TOPIC,0);
        this.consumer.assign(Arrays.asList(p0));*/
    }

    @Override
    public void doWork() {
        consumer.subscribe(Collections.singletonList(Kafka_Properties.TOPIC));
        ConsumerRecords<Integer, String> records = consumer.poll(1000);

        for (ConsumerRecord record : records) {
            System.out.println("Consumer1:" + "[" + record.partition() +
                    "]receiver message:[" + record.key() + "->" + record.value() + "]");
        }
    }

    public static void main(String[] args) {
        Consumer1 consumer = new Consumer1();
        consumer.start();
    }


}
