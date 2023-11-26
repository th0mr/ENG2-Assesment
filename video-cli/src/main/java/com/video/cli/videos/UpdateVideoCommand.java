package com.video.cli.videos;

import com.video.cli.domain.Hashtag;
import com.video.cli.domain.User;
import com.video.cli.dto.VideoDTO;
import com.video.cli.users.UsersClient;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="update-video", description="Updates a video", mixinStandardHelpOptions = true)
public class UpdateVideoCommand implements Runnable {

	@Parameters(index="0", description="ID of the video to be updated")
	private Long id;

	@Option(names = {"-t", "--title"}, description="Title of the video")
	private String title;

	@Option(names = {"-c", "--creatorId"}, description="ID of the creator of the video")
	private Long creatorId;

	@Option(names = {"-h", "--hashtagString"}, description="Hashtags of the video comma seperated e.g. 'tag1,tag2'")
	private String hashtagString;
	// TODO FIX HASHTAGS
	
	@Inject
	private VideosClient client;
	
	@Inject
	private UsersClient userClient;

	@Override
	public void run() {
		VideoDTO videoDetails = new VideoDTO();
		if (title != null) {
			videoDetails.setTitle(title);
		}
		if (creatorId != null) {
			videoDetails.setCreatorId(creatorId);
		}
		if (hashtagString != null) {
			videoDetails.setHashtagString(hashtagString);
		}
        
		HttpResponse<Void> response = client.updateVideo(id, videoDetails);
		System.out.println("Server responded with: " + response.getStatus());
	}

	
}
