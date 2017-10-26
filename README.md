# JMS
JMS and Kafka Documentation

# KAFKA

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


