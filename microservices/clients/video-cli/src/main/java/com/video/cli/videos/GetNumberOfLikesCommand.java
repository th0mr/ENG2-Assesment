package com.video.cli.videos;

import com.video.cli.domain.User;
import com.video.cli.domain.Video;
import com.video.cli.dto.VideoDTO;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-likes", description="Gets the number of likes a specific video has", mixinStandardHelpOptions = true)
public class GetNumberOfLikesCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="id of the video")
	private Long id;
	
	@Override
	public void run() {
		// Get video for the sake of pulling the name out
		Video video = client.getVideo(id);
		if (video == null) {
			System.err.println("video not found!");
			System.exit(1);
		}
		// Get the likers
		Iterable<User> users = client.getLikers(id);
		int like_count = 0;
		for (User user: users) {
			like_count += 1;
		}

		System.out.println("VideoID: " + id + " - Title: '" + video.getTitle() + "' - Like Count: " + like_count);
	}
	
}