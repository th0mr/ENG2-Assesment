package com.video.cli.users;

import com.video.cli.domain.Video;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-creator-videos", description="Gets all videos belonging to a creator", mixinStandardHelpOptions = true)
public class GetUserVideosCommand implements Runnable {

	@Inject
	private UsersClient client;

	@Parameters(index="0", description="id of the creator user")
	private Long id;

	@Override
	public void run() {
		Iterable<Video> videos = client.getUserVideos(id);
		for (Video video : videos) {
			System.out.println(video);
		}
	}

	
}