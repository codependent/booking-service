Start a local Kafka server:

```
$ KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
$ bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/kraft/server.properties
$ bin/kafka-server-start.sh config/kraft/server.properties
```

API invocation:
```
curl http://localhost:8080/api/bookings -d '{"customerId":"1"}' -H "Content-Type: application/json" -v
```
