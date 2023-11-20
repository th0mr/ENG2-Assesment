package com.video.cli.videos;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import com.video.cli.dto.VideoDTO;

@Command(name="add-video", description="Adds a video", mixinStandardHelpOptions = true)
public class AddVideoCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="Title of the video")
	private String title;

	@Parameters(index="1", description="The ID of the creator of the video")
	private Long creatorId;
	
	@Parameters(index="2", description="A list of strings to add to the video as hashtags e.g. [] or ['myTag'] or ['myTag','anotherTag']")
	private String[] hashtags;

	@Override
	public void run() {
		VideoDTO dto = new VideoDTO();
		dto.setTitle(title);
		dto.setCreatorId(creatorId);
		dto.setHashtags(hashtags);

		HttpResponse<Void> response = client.add(dto);
		System.out.println("Server responded with: " + response.getStatus());
	}

}
