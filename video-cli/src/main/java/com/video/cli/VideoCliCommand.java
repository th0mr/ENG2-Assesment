package com.video.cli;

import com.video.cli.users.AddUserCommand;
import com.video.cli.users.DeleteUserCommand;
import com.video.cli.users.GetUserCommand;
import com.video.cli.users.GetUsersCommand;
import com.video.cli.users.UpdateUserCommand;
import com.video.cli.videos.AddVideoCommand;
import com.video.cli.videos.AddVideoViewerCommand;
import com.video.cli.videos.DeleteVideoCommand;
import com.video.cli.videos.DeleteVideoViewerCommand;
import com.video.cli.videos.GetVideoCommand;
import com.video.cli.videos.GetVideoViewersCommand;
import com.video.cli.videos.GetVideosCommand;
import com.video.cli.videos.UpdateVideoCommand;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "video-cli", description = "...",
        mixinStandardHelpOptions = true,
		subcommands = {
				AddUserCommand.class, DeleteUserCommand.class, GetUsersCommand.class, GetUserCommand.class, UpdateUserCommand.class,
				AddVideoCommand.class, AddVideoViewerCommand.class, DeleteVideoCommand.class, DeleteVideoViewerCommand.class, GetVideosCommand.class,
				GetVideoCommand.class, GetVideoViewersCommand.class, UpdateVideoCommand.class
		})
public class VideoCliCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(VideoCliCommand.class, args);
    }

    public void run() {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
