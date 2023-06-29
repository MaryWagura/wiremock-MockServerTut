package com.example.wiremock;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GithubController {
	@Autowired
	private final GithubService githubService;

	@GetMapping("/users/{username}")
	public ResponseEntity<GithubUser> getGithubUserProfile(@PathVariable String username) {
		GithubUser githubUserProfile = githubService.getGithubUserProfile(username);
		return ResponseEntity.ok(githubUserProfile);
	}

	@ExceptionHandler(GithubServiceException.class)
	ResponseEntity<ApiError> handle (GithubServiceException e) {
		ApiError apiError = new ApiError((String) e.getMessage());
		return ResponseEntity.status(500).body(apiError);
	}
}
