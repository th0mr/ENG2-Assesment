#!/usr/bin/env bash

docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-watched --replication-factor 3 --partitions 6
docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-posted --replication-factor 3 --partitions 6
docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-disliked --replication-factor 3 --partitions 6
docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-liked --replication-factor 3 --partitions 6
docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic hashtag-subscribed --replication-factor 3 --partitions 6
docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic hashtag-unsubscribed --replication-factor 3 --partitions 6
