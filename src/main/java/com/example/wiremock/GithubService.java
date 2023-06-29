package com.example.wiremock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class GithubService {
	private final String githubApiBaseUrl;
	private final RestTemplate restTemplate;
	public GithubService(@Value("${github.api.base-url}") String githubApiBaseUrl){
		this.githubApiBaseUrl = githubApiBaseUrl;
		this.restTemplate = new RestTemplate();
	}
	public GithubUser getGithubUserProfile(String username) {
		try{
			log.info("Github API BaseUrl:" + githubApiBaseUrl);
			return restTemplate.getForObject(githubApiBaseUrl + "/users/" + username, GithubUser.class);
		}catch (RuntimeException e){
			log.error("Fail to fetch github profile", e);
			throw new GithubServiceException("Fail to fetch github profile for " + username);
		}
	}
}
