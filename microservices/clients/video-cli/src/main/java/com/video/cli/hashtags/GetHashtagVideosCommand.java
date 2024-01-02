package com.video.cli.hashtags;

import com.video.cli.domain.Video;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-hashtag-videos", description="Gets all videos linked to a hashtag", mixinStandardHelpOptions = true)
public class GetHashtagVideosCommand implements Runnable {

	@Inject
	private HashtagsClient client;

	@Parameters(index="0", description="id of the hashtag")
	private Long id;

	@Override
	public void run() {
		Iterable<Video> videos = client.getHashtagVideos(id);
		for (Video video : videos) {
			System.out.println(video);
		}
	}

	
}