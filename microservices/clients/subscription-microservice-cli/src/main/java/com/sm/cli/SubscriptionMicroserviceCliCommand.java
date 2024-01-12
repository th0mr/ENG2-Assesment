package com.sm.cli;

import com.sm.cli.subscriptions.AddSubscriptionCommand;
import com.sm.cli.subscriptions.GetSubscriptionRecommendationsCommand;
import com.sm.cli.subscriptions.RemoveSubscriptionCommand;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

@Command(name = "subscription-microservice-cli", description = "...",
        mixinStandardHelpOptions = true, subcommands= {
				HelpCommand.class,
        		AddSubscriptionCommand.class,
        		RemoveSubscriptionCommand.class,
        		GetSubscriptionRecommendationsCommand.class
        })
public class SubscriptionMicroserviceCliCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(SubscriptionMicroserviceCliCommand.class, args);
    }

    public void run() {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
