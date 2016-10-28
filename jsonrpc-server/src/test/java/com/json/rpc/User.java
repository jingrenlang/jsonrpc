package com.json.rpc;

public class User {

	private String name;
	private int age;
	private boolean ok;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	@Override
	public String toString() {
		return "name:" + name + ",age:" + age + ",ok:" + ok;
	}

}
