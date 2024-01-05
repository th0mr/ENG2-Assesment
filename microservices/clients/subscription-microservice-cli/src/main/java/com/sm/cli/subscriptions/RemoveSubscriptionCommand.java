package com.sm.cli.subscriptions;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="unsubscribe-from-hashtag", description="unsubscribes a user given a userId fom a given hashtag provided a hashtagId ", mixinStandardHelpOptions = true)
public class RemoveSubscriptionCommand implements Runnable {
	
	@Inject
	private SubscriptionsClient client;

	@Parameters(index="0", description="Id of the user who is unsubscribing")
	private Long userId;

	@Parameters(index="1", description="Id of the hashtag which the user is unsubscribing from")
	private Long hashtagId;

	@Override
	public void run() {
		HttpResponse<Void> response = client.unsubscribeFrom(userId, hashtagId);
		
		System.out.println("Server responded with: " + response.getStatus());
	}
	
}