package com.video.cli.videos;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="add-video-disliker", description="Adds a user as a disliker to a video", mixinStandardHelpOptions = true)
public class AddVideoDislikerCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="The ID of the video to be watched")
	private Long videoId;

	@Parameters(index="1", description="The ID of the user watching the video")
	private Long userId;

	@Override
	public void run() {
		HttpResponse<String> response = client.addDisliker(videoId, userId);
		System.out.printf("Server responded with status %s: %s%n",
			response.getStatus(), response.getBody().orElse("(no text)"));
	}

}
