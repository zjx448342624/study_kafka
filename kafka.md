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

1. tar -zxvf 

2. 进入到config目录下修改server.properties

   + broker.id=唯一的一个
   + isteners=PLAINTEXT://192.168.177.131:9092(当前节点的ip)
   + zookeeper.connect (zookeeper的链接地址)

3. 启动

   1.  sh kafka-server-start.sh -daemon ./config/server.properties
   2. sh kafka-server-stop.sh

4. zookeeper 上注册的节点

   **cluster, controller, brokers, zookeeper, dubbo, admin, isr_change_notification, log_dir_event_notification, dubbo-dev, controller_epoch, consumers, LOCKS, latest_producer_id_block, config**

   1.  controller : 控制节点(leader)
   2.  controller_epoch : 集群中控制节点选举的次数
   3.  brokers : kafka集群的broker信息，topic
   4.  isr_change_notification : 实现kafka分区同步监听任务
   5.  consumers : 记录当前消费者信息， ids/owners/offsets

#### 基本操作

- 创建topic 

  ``` sh kafka-topics.sh --create --zookeeper 192.168.177.129:2181 --replication-factor 1 --partitions 1 --topic mytopic```

  - replication-factor: 为副本数
  - partitions: 为分片数

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
  - 零拷贝:消息从发送到落地保存，broker维护的消息日志本身就是文件目录，每个文件都是二进制保存，生产者和消费者使用相同的格式来处理。在消费者获取消息时，服务器先从硬盘读取数据到内存，然后把内存中的数据原封不懂的通过socket发送给消费者。虽然这个操作描述起来很简单，但实际上经历了很多步骤：通过“零拷贝”技术可以去掉这些没必要的数据复制操作，同时也会减少上下文切换次数FIleChannel.transferTo
- 日志策略
  - 日志保留策略：根据消息保留时间和数据大小来确定
  - 日志压缩策略：对相同的key的值进行合并（只保留最新的值）

#### kafka的可靠性

- 消息发送可靠性：生产者发送消息到broker，有三种确认方式(request,required,acks)
  - acks= 0 ：producer不会等待broker（leader）发送ack。因为发送消息网络超时或broker crash（

  1.Partition的Leader还没有commit消息    2.Leader与Follower数据不同步），极有可能丢失也有可能重发
  - acks=1：当leader接收到消息之后发送ack，丢会重发，丢的概率很小
  - acks=-1：当所有的foller都同步消息成功后发送ack，丢失消息可能性比较低

- 消息存储可靠性：会根据partition规则选择被存储到哪一个partition，通过key的路由进行存储，如果消息规则设置合理就可以社保证消息均匀分不到partition中，从而实现水平扩展。

  kafka可以通过副本机制可以再高性能和高可靠之间切换

#### 副本机制

kafka通过ISO（副本同步队列）用来维护的是有资格的foller节点，如果leader挂掉以后就从iso队列中选取出新的leader，数据进入leader中foller会从leader拉去数据，如果foller不满足下方阈值那么就会被提出ISO中，如果foller的数据满足阈值就会被加入到队列中

- 副本的所有节点都必须要和zookeeper保持连接状态 
- 副本的最后一条消息的offset和leader副本的最后一条消息的offset之间的差值不能超过指定的阀值，这个阀值是可以设置的（replica.lag.max.messages） 

- kafka为了提高可靠性提供了副本机制，他可以通过副本来实现故障转移。对于副本来说包括两个角色（leader和foller)，当kafka中的leader挂掉以后会通过zookeeper实现leader选举

#### HW&LEO

当HW==LEO的时候也就是说leader的数据在foller中都同步了，那么所有的foller节点就会进入阻塞状态，一旦HW增加，那么会通知所有的foller节点进行数据抓取。

- HW（HighWatermark ）：显示当前leader中所有的数据的offset的一个值，
- LEO（Log End Offset ）：显示当前foller中都同步完成的一个值，如果所有的foller都同步到4，但是HW的值位0，那么LEO=4，HW=5







