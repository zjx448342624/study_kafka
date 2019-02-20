package com.zjxedu.kafka.study;

import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author zjx
 *
 * 手动批量提交
 */
public class CommitDemoConsumer extends ShutdownableThread {
    //High Level consumer 里面封装了大量的信息细节和复杂的操作，如果不需要定制化的东西就使用这个
    //Low level consumer  里面的信息更加的灵活可以动态的更改offset等，这个的工作量比较多，如果需要定制化的东西可以去使用这个

    private final KafkaConsumer<Integer, String> consumer;

    Logger LOG = LoggerFactory.getLogger(this.getClass());

    private List<ConsumerRecord> buffer = new ArrayList<ConsumerRecord>();

    public CommitDemoConsumer() {
        super("KafkaConsumerTest", false);

        Properties props = new Properties();
        // 服务器地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Kafka_Properties.KAFKA_BROKER_LIST);
        //group id  消息所属的分组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoGroup1");

        // 消息是否自动提交：offst
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // 消息自动提交的间隔
        // props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // 设置使用最开始的offset偏移量为当前group.id 的最早的消息
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //设置心跳时间
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        //对key和value设置反序列化对象
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        this.consumer = new KafkaConsumer<Integer, String>(props);

    }

    @Override
    public void doWork() {
        consumer.subscribe(Collections.singletonList(Kafka_Properties.TOPIC));
        ConsumerRecords<Integer, String> records = consumer.poll(1000);

        for (ConsumerRecord record : records) {
            System.out.println("[" + record.partition() +
                    "]receiver message:[" + record.key() + "->" + record.value() + "]");
            buffer.add(record);
        }

        if(buffer.size() >= 5){
            LOG.info("Begin Excecute Comiit Offset Operation");
            // 同步提交
            consumer.commitSync();
            // 异步提交
            //consumer.commitAsync();
            buffer.clear();
        }
    }

    public static void main(String[] args) {
        CommitDemoConsumer consumer = new CommitDemoConsumer();
        consumer.start();
    }


}
