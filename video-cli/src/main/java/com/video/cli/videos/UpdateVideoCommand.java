package com.video.cli.videos;

import com.video.cli.dto.VideoDTO;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="update-video", description="Updates a video", mixinStandardHelpOptions = true)
public class UpdateVideoCommand implements Runnable {

	@Parameters(index="0")
	private Long id;

	@Option(names = {"-t", "--title"}, description="Title of the video")
	private String title;

	@Option(names = {"-c", "--creatorId"}, description="ID of the creator of the video")
	private Long creatorId;

	@Option(names = {"-h", "--hashtags"}, description="Hashtags of the video")
	private String[] hashtags;
	// TODO FIX HASHTAGS
	
	@Inject
	private VideosClient client;

	@Override
	public void run() {
		VideoDTO videoDetails = new VideoDTO();
		if (title != null) {
			videoDetails.setTitle(title);
		}
		if (creatorId != null) {
			videoDetails.setCreatorId(creatorId);
		}
		if (hashtags != null) {
			videoDetails.setHashtags(hashtags);
		}
		
		HttpResponse<Void> response = client.updateVideo(id, videoDetails);
		System.out.println("Server responded with: " + response.getStatus());
	}

	
}
