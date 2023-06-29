package com.example.wiremock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubUser {
	private Long id;
	private String login;
	private String name;
	private String company;
	private String blog;
	private String location;
	private String email;
	private String bio;
	@JsonProperty("twitter_username")
	private String twitterUsername;
	@JsonProperty("public_repos")
	private Integer publicRepos;
	private int followers;
	private int following;
	private Boolean hiretable;
}
