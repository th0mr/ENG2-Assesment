package com.video.dto;

public class HashtagDTO {

    private String name;

    @Override
	public String toString() {
		return "HashtagDTO [name=" + name + "]";
	}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

