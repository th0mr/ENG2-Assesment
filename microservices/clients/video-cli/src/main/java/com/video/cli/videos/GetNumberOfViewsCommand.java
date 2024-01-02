package com.video.cli.videos;

import com.video.cli.domain.User;
import com.video.cli.domain.Video;
import com.video.cli.dto.VideoDTO;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-views", description="Gets the number of views a specific video has", mixinStandardHelpOptions = true)
public class GetNumberOfViewsCommand implements Runnable {

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
		// Get the viewers
		Iterable<User> users = client.getViewers(id);
		int view_count = 0;
		for (User user: users) {
			view_count += 1;
		}
		System.out.println("VideoID: " + id + " - Title: '" + video.getTitle() + "' - View Count: " + view_count);
	}
	
}
