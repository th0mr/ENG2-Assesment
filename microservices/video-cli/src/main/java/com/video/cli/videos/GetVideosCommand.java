package com.video.cli.videos;

import com.video.cli.domain.Video;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;

@Command(name="get-videos", description="Gets all the videos", mixinStandardHelpOptions = true)
public class GetVideosCommand implements Runnable {

	@Inject
	private VideosClient client;

	@Override
	public void run() {
		for (Video v : client.list()) {
			System.out.println(v);
		}
	}

}