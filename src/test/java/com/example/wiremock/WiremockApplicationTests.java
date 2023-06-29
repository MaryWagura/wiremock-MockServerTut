package com.example.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.nio.file.Paths.get;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WiremockApplicationTests {

	@Autowired
	protected MockMvc mockMvc;

	@RegisterExtension //use to spin a wiremock server
	static WireMockExtension wireMockServer = WireMockExtension.newInstance()
			                                                 .options(wireMockConfig().dynamicPort())
			                                                  .build(); //start wiremock server auto

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry)
	{
		registry.add("github.api.base-url", wireMockServer::baseUrl);
	}

	@Test
	void shouldGetGithubUserProfile() throws Exception {
		String username = "marywagura";
		wireMockServer.stubFor(WireMock.get(urlMatching("/users/.*"))
				.willReturn(
						aResponse()
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("""
										{
									"login": "%s",
									"name": "marywagura",
									"twitter_username": "",
									"public_repos": ""
								}
								""".formatted(username))));
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{username}",username))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.login", is(username)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name", is("marywagura")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.twitter_username", is("")));

	}
	@Test
    void shouldGetFailureResponseWhenGithubApiFailed() throws Exception {
		String username = "marywagura";

		wireMockServer.stubFor(WireMock.get(urlMatching("/users/.*"))
				.willReturn(aResponse().withStatus(500)));

		String expectedErrorMessage = "Fail to fetch github profile for " + username;
		this.mockMvc.perform((RequestBuilder) get("/api/users/{username}", username))
						.andExpect(status().is5xxServerError())
						.andExpect((ResultMatcher) jsonPath("$.message").value(expectedErrorMessage));

	}
}
