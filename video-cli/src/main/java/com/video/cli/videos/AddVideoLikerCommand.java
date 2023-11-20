package com.video.cli.videos;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="add-video-liker", description="Adds a user as a liker to a video", mixinStandardHelpOptions = true)
public class AddVideoLikerCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="The ID of the video to be liked")
	private Long videoId;

	@Parameters(index="1", description="The ID of the user watching the video")
	private Long userId;

	@Override
	public void run() {
		HttpResponse<String> response = client.addLiker(videoId, userId);
		System.out.printf("Server responded with status %s: %s%n",
			response.getStatus(), response.getBody().orElse("(no text)"));
	}

}
