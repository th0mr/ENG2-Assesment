package com.thm.cli.trendingHashtags;

import com.thm.cli.domain.HashLikesPair;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;

@Command(name="get-trending-hashtags", description="Get the top ten trending hashtags from the last hour based on their likes / dislikes,"
		+ " negative values indicate videos with more dislikes than likes. These are still considered 'trending'", mixinStandardHelpOptions = true)
public class GetTrendingHashtagsCommand implements Runnable {

	@Inject
	private TrendingHashtagsClient client;

	@Override
	public void run() {
		
		Iterable<HashLikesPair> HashLikePairs = client.list();
		
		// Get number of hashlikepairs
		int count = 0;
		for (HashLikesPair hlp : HashLikePairs) {
			count+=1;
		}
		
		System.out.println("The following are the top 10 hashtags in the last hour, less than 10 may be present if less hashtags are trending\n"
				+ "The last number also takes into account the dislikes of a video i.e.  = likes - dislikes\n");
		
		// Count down the top hashtags
		for (HashLikesPair hlp : HashLikePairs) {
			System.out.println("#" + count + " |   '" + hlp.getHashname() + "'  = " + hlp.getLikeCount() + " likes");
			count-=1;
		}
	}
}
