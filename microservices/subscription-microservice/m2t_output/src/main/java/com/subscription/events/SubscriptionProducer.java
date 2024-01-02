// protected region packageDefinition on begin
package todo
// protected region packageDefinition end

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface SubscriptionProducer {

	@Topic("hashtag-unsubscribed")
	void unsubscribedFromHashtag(@KafkaKey long userId, long hashtagId);
	
	@Topic("hashtag-subscribed")
	void subscribedToHashtag(@KafkaKey long userId, long hashtagId);
	

}