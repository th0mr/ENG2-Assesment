package com.sm.cli.subscriptions;

import java.util.List;

import com.sm.cli.domain.VideoViewsPair;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-subscription-recommendations", description="given a valid userId and hashtagId pair for which a subscription exists, fetch the"
		+ "top 10 videos with the most views that the user has not seen yet since their subscription", mixinStandardHelpOptions = true)
public class GetSubscriptionRecommendationsCommand implements Runnable{
	
	@Inject
	private SubscriptionsClient client;

	@Parameters(index="0", description="Id of the user who has subscribed")
	private Long userId;

	@Parameters(index="1", description="Id of the hashtag which the user has subscribed to")
	private Long hashtagId;

	@Override
	public void run() {
		List<VideoViewsPair> vidViewPairs = client.getTopTenVideos(userId, hashtagId);
		
		System.out.println("The following are the top 10 viewed videos that you have not seen since subscribing to this hashtag\n"
				+ "Less than 10 may be present if enough videos do not exist to satisfy this criteria\n");
		
		int count = 1;
		
		// Count down the top hashtags
		for (VideoViewsPair vvp : vidViewPairs) {
			System.out.println("#" + count + " |   'vidID=" + vvp.getVideo().getId() + " - " + vvp.getVideo().getTitle() + "' created by user '" + vvp.getVideo().getCreator().getUsername()  + "' - views =" + vvp.getViews());
			count+=1;
		}
	}
}
