package com.vmodev.stackphotoview;

public class Profile {
	private String name;
	private String url;
	private int resIdAvatar;

	public Profile(String name, int resIdAvatar) {
		this.name = name;
		this.resIdAvatar = resIdAvatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getResIdAvatar() {
		return resIdAvatar;
	}

	public void setResIdAvatar(int resIdAvatar) {
		this.resIdAvatar = resIdAvatar;
	}
}
