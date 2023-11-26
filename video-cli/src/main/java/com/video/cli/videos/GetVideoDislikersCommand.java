package com.video.cli.videos;

import com.video.cli.domain.User;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-video-dislikers", description="Gets the dislikers of a specific video", mixinStandardHelpOptions = true)
public class GetVideoDislikersCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="The Id of the video to fetch dislikers for")
	private Long id;

	@Override
	public void run() {
		Iterable<User> users = client.getDislikers(id);
		for (User user : users) {
			System.out.println(user);
		}
	}
	
}
