package com.sm.cli.subscriptions;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="subscribe-to-hashtag", description="subscribes a user given a userId to a given hashtag provided a hashtagId ", mixinStandardHelpOptions = true)
public class AddSubscriptionCommand implements Runnable {
	
	@Inject
	private SubscriptionsClient client;

	@Parameters(index="0", description="Id of the user who is subscribing")
	private Long userId;

	@Parameters(index="1", description="Id of the hashtag which the user is subscribing to")
	private Long hashtagId;

	@Override
	public void run() {
		HttpResponse<Void> response = client.subscribeTo(userId, hashtagId);
		
		System.out.println("Server responded with: " + response.getStatus());
	}
	
}