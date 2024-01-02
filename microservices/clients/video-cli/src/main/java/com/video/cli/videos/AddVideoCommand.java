package com.video.cli.videos;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.video.cli.domain.Hashtag;
import com.video.cli.domain.User;
import com.video.cli.domain.Video;
import com.video.cli.dto.HashtagDTO;
import com.video.cli.dto.UserDTO;
import com.video.cli.dto.VideoDTO;
import com.video.cli.hashtags.HashtagsClient;
import com.video.cli.users.UsersClient;

@Command(name="add-video", description="Adds a video", mixinStandardHelpOptions = true)
public class AddVideoCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="Title of the video")
	private String title;

	@Parameters(index="1", description="The name of the creator of the video")
	private Long creator;
	
	@Parameters(index="2", description="A comma seperated string of hashtags to add to the video e.g. 'tag1,tag2,tag3' or 'tag1' or '' ")
	private String hashtagString;

	@Override
	public void run() {
		VideoDTO dto = new VideoDTO();
		dto.setTitle(title);
		dto.setCreatorId(creator);
		dto.setHashtagString(hashtagString);

		HttpResponse<Void> response = client.add(dto);
		
		System.out.println("Server responded with: " + response.getStatus());
	}

}


