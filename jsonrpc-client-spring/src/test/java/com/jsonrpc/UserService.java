package com.jsonrpc;

import java.util.List;

public interface UserService {

	public User findUserByName(String name);

	public List<User> findUsers(String name);

	public User[] array();

	public User save(String name, User user);
}
