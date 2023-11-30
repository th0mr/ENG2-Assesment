
# Wait for kakfa to be up
until echo "exit" | nc -zv localhost 9092; do
  sleep 1
done

# Create Kafka topics
kafka-topics.sh --create --topic video-watched --bootstrap-server localhost:9094 --partitions 6 --replication-factor 3
kafka-topics.sh --create --topic video-liked --bootstrap-server localhost:9094 --partitions 6 --replication-factor 3
kafka-topics.sh --create --topic video-disliked --bootstrap-server localhost:9094 --partitions 6 --replication-factor 3
kafka-topics.sh --create --topic video-posted --bootstrap-server localhost:9094 --partitions 6 --replication-factor 3
