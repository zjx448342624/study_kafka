# kafka

+ kafka是一款分布式消息发布和订阅系统，具有高性能、高吞吐量的特点被广泛应用在大数据传输场景。

##### 为什么要使用kafka

kafka天然具备高吞吐量、内置分区等特性，因此非常适合处理大规模的消息，所以人们使用kafka来做一些日志收集分析，消息系统，用户行为分析等



##### 概念

+ broker ： 可以理解位kafka的一个系统
+ producer： 系统的信息的生产者
+ consumer： 系统信息的消费者
+ topic ：数据集合，可以理解为类别（其他的分布式叫分布/订阅）
+ partition ： 被topic所包含，是物理上的具体的存储，存储topic的数据
+ consumer group : 用来去指定消费者的分组，每个消费者都有个特定的组  



#### 安装kafka

1.  tar -zxvf 

2.  进入到config目录下修改server.properties

   + broker.id=唯一的一个
   + isteners=PLAINTEXT://192.168.177.131:9092(当前节点的ip)
   + zookeeper.connect (zookeeper的链接地址)

3. 启动

   1.  sh kafka-server-start.sh -daemon ./config/server.properties
   2. sh kafka-server-stop.sh

4. zookeeper 上注册的节点

   **cluster, controller, brokers, zookeeper, dubbo, admin, isr_change_notification, log_dir_event_notification, dubbo-dev, controller_epoch, consumers, LOCKS, latest_producer_id_block, config**

   1.  controller： 控制节点
   2.  brokers：kafka集群的broker信息。topic也会注册在上面
   3.  consumers： ids/owners/offsets

#### 基本操作

- 创建topic 

  ``` sh kafka-topics.sh --create --zookeeper 192.168.177.129:2181 --replication-factor 1 --partitions 1 --topic mytopic```

- 显示topic

  ``` sh kafka-topics.sh --list --zookeeper 192.168.177.129:2181```

- 启动producer

  ``` sh kafka-console-producer.sh --broker-list 192.168.177.129:9092 --topic mytopic```

- 启动consumer

  ``` sh kafka-console-consumer.sh --bootstrap-server 192.168.177.129:9092 --topic mytopic --from-beginning```

  启动consumer以后会陷入阻塞状态等待接收消息 --from-beginning 是从头开始

#### kafka的实现细节

- 消息
  - 消息有key和value的形式，通过byte数组保存，key[可选]，可以通过算key的hash值来确定key的存放
  - 消息批量发送
- topick&partition
  - topic：使用来存储消息的逻辑概念，可以理解为一个消息结合，每toptic可以有多个生产者向它推送消息
  - partition： 如果topick是一个数据库，那么每个partition就是一个数据表，topic是一个总的概念，但是    数据存放在partition中，数据存放的策略是根据消息的key来计算的，每个partition都是独立有序的，由一串有序的数字组成，当producer发送消息时已追加的方式存放在partition中且不会有分区问题
- kafka的高吞吐量的原因
  - 它是一个顺序写的方式存储数据；频繁的io（网络io，磁盘io）
  - 批量发送的概念producer发送消息有一个内存缓存，发送的策略根据batch.size（批量发送的大小）/linger.ms（发送时间）
  - 零拷贝:FIleChannel.transferTo
- 日志策略
  - 日志保留策略：根据消息保留时间和数据大小来确定
  - 日志压缩策略：对相同的key的值进行合并（只保留最新的值）

#### kafka的可靠性

   









