package com.video.cli.videos;

import com.video.cli.domain.User;
import com.video.cli.domain.Video;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-dislikes", description="Gets the number of dislikes a specific video has", mixinStandardHelpOptions = true)
public class GetNumberOfDislikesCommand implements Runnable {

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
		// Get the dislikers
		Iterable<User> users = client.getDislikers(id);
		int dislike_count = 0;
		for (User user: users) {
			dislike_count += 1;
		}

		System.out.println("VideoID: " + id + " - Title: '" + video.getTitle() + "' - Dislike Count: " + dislike_count);
	}
	
}
