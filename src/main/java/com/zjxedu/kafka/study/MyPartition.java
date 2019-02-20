package com.zjxedu.kafka.study;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;

/**
 * @author Think
 */
public class MyPartition implements Partitioner {
    public int partition(String topic, Object key, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        List<PartitionInfo> partitionInfoList = cluster.partitionsForTopic(topic);
        //所有的分区
        int numPart = partitionInfoList.size();
        // 获得key 的hashcode
        int hashCode = key.hashCode();


        return Math.abs(hashCode % numPart);
    }

    public void close() {

    }

    public void configure(Map<String, ?> map) {

    }
}
