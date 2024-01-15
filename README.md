# ENG2-Assessment

First time setup
================

1. - Ensure the docker daemon is running, either through the command line or by starting docker desktop

NOTE - I ran into a variety of issues surrounding docker contexts, e.g. images only being accessible for the super user
	- its worth checking what docker context you are using with 'docker context list'
	- switch to the 'default' context with 'docker context use default'
	- If you dont think these issues will affect you due to your environment or experience, continue.

2. - run ./gradlew dockerBuild in each of the microservices root folders to create the docker images for i.e.
	- 'cd microservices/video-microservice' then './gradlew dockerBuild'
	- 'cd microservices/trending-hashtag-microservice' then './gradlew dockerBuild'
	- 'cd microservices/subscription-microservice' then './gradlew dockerBuild'

	- NOTE - If you are running into an issue that looks like 'java.io.IOException: native connect() failed : Permission denied' ensure your docker.sock has the correct permissions by running 'sudo chmod 666 /var/run/docker.sock' (on linux)

3. - We need to create a few kafka topics before running the microservices
	1. - Start the kafka cluster with 'docker compose up -d kafka-0 kafka-1 kafka-2'
	2. - Now kafka is running, either run the convenience script './microservices/create-microservice-topics.sh' OR run the following commands to create the six topics used by the microservices
		docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-watched --replication-factor 3 --partitions 6
		docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-posted --replication-factor 3 --partitions 6
		docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-disliked --replication-factor 3 --partitions 6
		docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic video-liked --replication-factor 3 --partitions 6
		docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic hashtag-subscribed --replication-factor 3 --partitions 6
		docker compose exec -e JMX_PORT= kafka-0 kafka-topics.sh --bootstrap-server kafka-0:9092 --create --topic hashtag-unsubscribed --replication-factor 3 --partitions 6

4. - run 'docker compose up -d' to start the microservices

5. - confirm the containers are running with 'docker ps'

Now you are ready to use the microservices!

Stopping the services
=====================

run 'docker compose down'


Restarting the services
=======================

run 'docker compose down' then run 'docker compose up -d'


Running the tests
=================

The tests for each microservice can be ran using ‘./gradlew test’, however, the docker containers for the kafka cluster, video-db, thm-db and subscription-db must be running (run 'docker compose up -d kafka-0 kafka1 kafka2 video-db thm-db subscription-db)

Using the graphical concrete syntax
===================================

The design project for the syntax can be found under modelling/metamodel/design. 
1. - Prior to creation of a diagram the Y3887302.ecore must be registered as an EPackage (right click -> register EPackages). 
2. - Then from the design project, open the project.aird file and add the microserviceArchitecture metamodel, and add the acme.model from the filesystem. 
3. - Enable the viewpoint
4. - Create a new diagram, selecting the top level Application node from the model.

Running the constraints against the model
=======================================
Running the constraints -  Load and use the launch configuration in modelling/configs/ model-constraints.launch to run the constraints in Eclipse under �Run As� -> �run configurations� -> model-constraints. If this fails, ensure that the configuration is pointing to the correct model and that model is present.