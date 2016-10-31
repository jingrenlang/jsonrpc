package com.json.rpc;

import java.util.List;

public class UserService {

	public User findUserByName(String name) {
		return new User(name, 24, true);
	}

	public List<User> findUsers(String name) {
		return null;
	}

	public User save(String name, User user) {
		user.setName(name);
		user.setAge(24);
		return user;
	}
}
