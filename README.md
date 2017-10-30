# JMS
JMS and Kafka Documentation

# KAFKA

**Topics and Partitions**
1. The Offset only have meaning  for a specific partition
2. Order is guaranteed only within a pratition
3. Data is kept for a limited time
4. Once the data is written, it can't be modify

**Broker**
1. Each borker is identified by an ID
2. Each broker contains specific topic partitions
3. After connecting to any broker, you will be connected to the entire cluster

The topics have a replication factor ( >1 ), so if a broker is down, another broker can serve the data.

*Only ONE broker can be a leader for a given partition* = Only that leader can receive and serve data for this partition. And the other brokers will synchronize the data. 

### Zookeeper

* Zookeeper manages brokers (keep a list of them)
* Zookeeper helps in performing leader election for partitions
* Zookeeper sends notification to Kafka (new topic, new broker, broker dies, delete topics, etc..)
* **Kafka can't work without Zookeper**
* Zookeper works in clusters and has also a leader and followers

### KafkaConsumer

The consumers have to specify the topic and one broker to connect, and Kafka will take care of pulling the data from the right brokers.
The data is read in order from each partition.

## Consumer Groups
* Consumers read data in groups
* Each consumer reads from exclusive partitions
* If you have more consumer than partitions, some will be inactive
* When a consumer has processed data received , it should be committing the offsets


### KafkaProducer

For producing messages to Kafka:
  1. The first is to create a ***ProducerRecord***, (includes the topic, value and we can also specify a key and partition)
  2. The *ProducerRecord* serializes the key and value objects to ByteArrays
  3. The data is sent to a partitioner. If we didn't specify a partition in the *ProducerRecord*, the partitioner will choose a partition for us. 
  4. Once a partition is selected, the producer knows which topic and partition the record will go to. Then adds the record to a batch of records that will be sent to the same topic and partition. 
  5. When the broker receives the batch , it sends back a response. If everything was fine, it returns a ***RecordMetadata*** (a object with the topic, partition, and the offset of the record in the partition). If the broker failed to write the message, it will return an error. The producer will retry a few more times before giving up and returning an error. 


The Kafka producer has 3 mandatory properties:

* **bootstrap.servers** => ´host:port´ pair of brokers that the producer will use to establish initial connection to the Kafaka CLuster
* **key.serializer** => Class that will serialize the keys of the records. This class should implement that Kafka *Serializer* interface
* **value.serializer** => Class that will serialize the values of the records

A producer has to know how to convert the object to byteArrays.

The producer has 3 ways in order to send  a message

  1. **Fire and forget**
  2. **Synchronous** - the method *send()* returns a Future object. We use *get()* to wait for the result
  3. **Asynchronous** - the method *send()* is called with a callback function, which is executed when it receives a response from the        Kafka broker. The callback function must be a class that implements the Kafka *Callback* interface (one method *onCompletion*)

The Producers can choose to receive acknowledge of the sent data:

  1. Acks=0. Producer won't wait for response (possible data loss)
  2. Acks=1. Producer will wait for leader acknowledgment (limited data loss)
  3. Acks=2. Producer will wait for leader + replicas acknowledgment (no data loss)
  
  
  


