package com.video.cli.videos;

import com.video.cli.domain.User;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-video-viewers", description="Gets the viewers of a specific video", mixinStandardHelpOptions = true)
public class GetVideoViewersCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="The Id of the video to fetch viewers for")
	private Long id;

	@Override
	public void run() {
		Iterable<User> users = client.getViewers(id);
		for (User user : users) {
			System.out.println(user);
		}
	}
	
}