package com.video.cli.videos;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="delete-video-disliker", description="Deletes a disliker from a video", mixinStandardHelpOptions = true)
public class DeleteVideoDislikerCommand implements Runnable {

	@Parameters(index="0")
	private Long videoId;

	@Parameters(index="1")
	private Long userId;

	@Inject
	private VideosClient client;

	@Override
	public void run() {
		HttpResponse<String> response = client.removeDisliker(videoId, userId);
		System.out.printf("Server responded with status %s: %s%n",
			response.getStatus(), response.getBody().orElse("(no text)"));
	}

}
