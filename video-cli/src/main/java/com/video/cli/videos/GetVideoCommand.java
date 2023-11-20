package com.video.cli.videos;

import com.video.cli.dto.VideoDTO;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="get-video", description="Gets a specific video", mixinStandardHelpOptions = true)
public class GetVideoCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Parameters(index="0", description="id of the video")
	private Long id;

	@Override
	public void run() {
		VideoDTO book = client.getVideo(id);
		if (book == null) {
			System.err.println("Book not found!");
			System.exit(1);
		} else {
			System.out.println(book);
		}
	}

	
}
