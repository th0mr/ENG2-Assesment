package com.video.cli.users;

import com.video.cli.domain.Video;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-user-disliked-videos", description="Gets all videos a user has disliked", mixinStandardHelpOptions = true)
public class GetUserDislikedVideosCommand implements Runnable {

	@Inject
	private UsersClient client;

	@Parameters(index="0", description="id of the user")
	private Long id;

	@Override
	public void run() {
		Iterable<Video> videos = client.getUserDislikedVideos(id);
		for (Video video : videos) {
			System.out.println(video);
		}
	}

	
}