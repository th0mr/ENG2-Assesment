package com.thm.cli.domain;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class HashLikesPair {
	
	private String hashname;
	private int likeCount;
	
	public String getHashname() {
		return hashname;
	}
	
	public void setHashname(String hashname) {
		this.hashname = hashname;
	}
	
	public int getLikeCount() {
		return likeCount;
	}
	
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	
	@Override
	public String toString() {
		return "HashLikesPair [hashname=" + hashname + ", likeCount=" + likeCount + "]";
	}

	
}