// protected region packageDefinition on begin
package todo
// protected region packageDefinition end

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface VideoProducer {

	@Topic("video-watched")
	void watchedVideo(@KafkaKey long userId, Video v);
	
	@Topic("video-posted")
	void postedVideo(@KafkaKey long userId, Video v);
	
	@Topic("video-liked")
	void likedVideo(@KafkaKey long userId, Video v);
	
	@Topic("video-disliked")
	void dislikedVideo(@KafkaKey long userId, Video v);
	

}