package com.video.cli.users;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import com.video.cli.domain.User;
import com.video.cli.dto.UserDTO;

@Command(name="get-user", description="Gets a specific user", mixinStandardHelpOptions = true)
public class GetUserCommand implements Runnable {

	@Inject
	private UsersClient client;

	@Parameters(index="0", description="ID of the user")
	private Long id;

	@Override
	public void run() {
		User user = client.getUser(id);
		if (user == null) {
			System.err.println("User not found!");
			System.exit(1);
		} else {
			System.out.println(user);
		}
	}
}
