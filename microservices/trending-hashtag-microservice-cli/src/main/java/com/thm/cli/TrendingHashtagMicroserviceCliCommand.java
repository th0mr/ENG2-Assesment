package com.thm.cli;

import com.thm.cli.trendingHashtags.GetTrendingHashtagsCommand;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "trending-hashtag-microservice-cli", description = "...",
        mixinStandardHelpOptions = true,
        subcommands = {
        		GetTrendingHashtagsCommand.class
        })
public class TrendingHashtagMicroserviceCliCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(TrendingHashtagMicroserviceCliCommand.class, args);
    }

    public void run() {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
