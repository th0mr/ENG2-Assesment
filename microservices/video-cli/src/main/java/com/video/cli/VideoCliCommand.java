package com.video.cli;

import com.video.cli.hashtags.GetHashtagVideosCommand;
import com.video.cli.hashtags.GetHashtagsCommand;
import com.video.cli.users.AddUserCommand;
import com.video.cli.users.DeleteUserCommand;
import com.video.cli.users.GetUserCommand;
import com.video.cli.users.GetUserDislikedVideosCommand;
import com.video.cli.users.GetUserLikedVideosCommand;
import com.video.cli.users.GetUserVideosCommand;
import com.video.cli.users.GetUserWatchedVideosCommand;
import com.video.cli.users.GetUsersCommand;
import com.video.cli.users.UpdateUserCommand;
import com.video.cli.videos.AddVideoCommand;
import com.video.cli.videos.AddVideoDislikerCommand;
import com.video.cli.videos.AddVideoLikerCommand;
import com.video.cli.videos.AddVideoViewerCommand;
import com.video.cli.videos.DeleteVideoCommand;
import com.video.cli.videos.DeleteVideoDislikerCommand;
import com.video.cli.videos.DeleteVideoLikerCommand;
import com.video.cli.videos.DeleteVideoViewerCommand;
import com.video.cli.videos.GetNumberOfDislikesCommand;
import com.video.cli.videos.GetNumberOfLikesCommand;
import com.video.cli.videos.GetNumberOfViewsCommand;
import com.video.cli.videos.GetVideoCommand;
import com.video.cli.videos.GetVideoDislikersCommand;
import com.video.cli.videos.GetVideoLikersCommand;
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
				AddUserCommand.class, AddVideoCommand.class, 
				DeleteUserCommand.class, DeleteVideoCommand.class,
				UpdateUserCommand.class, UpdateVideoCommand.class,
				GetUsersCommand.class, GetUserCommand.class, GetVideosCommand.class, GetVideoCommand.class,
				AddVideoViewerCommand.class,  AddVideoLikerCommand.class, AddVideoDislikerCommand.class,
				GetVideoViewersCommand.class, GetVideoLikersCommand.class, GetVideoDislikersCommand.class,
				DeleteVideoViewerCommand.class, DeleteVideoLikerCommand.class, DeleteVideoDislikerCommand.class,
				GetNumberOfLikesCommand.class, GetNumberOfDislikesCommand.class, GetNumberOfViewsCommand.class,
				GetHashtagVideosCommand.class, GetHashtagsCommand.class, GetUserVideosCommand.class,
				GetUserWatchedVideosCommand.class, GetUserLikedVideosCommand.class, GetUserDislikedVideosCommand.class
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
