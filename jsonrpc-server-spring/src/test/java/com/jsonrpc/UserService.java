package com.jsonrpc;

import java.util.Arrays;
import java.util.List;

public class UserService {

	public User findUserByName(String name) {
		return new User(name, 24, true);
	}

	public List<User> findUsers(String name) {
		return Arrays.asList(new User("UA", 24, true), new User("UB", 25, true), new User("UC", 26, true));
	}

	public User[] array() {
		return new User[] { new User("UA", 24, true), new User("UB", 25, true), new User("UC", 26, true) };
	}

	public User save(String name, User user) {
		user.setName(name);
		user.setAge(24);
		return user;
	}
}
