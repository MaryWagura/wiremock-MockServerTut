package com.example.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WiremockStubTests {
    @Autowired
    protected MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(
                    wireMockConfig()
                            .dynamicPort()
                            .usingFilesUnderClasspath("wiremock")
            )
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        System.out.println("baseUrl: " + wireMockServer.baseUrl());
        registry.add("github.api.base-url", wireMockServer::baseUrl);
    }

    @Test
    void shouldGetGithubProfile() throws Exception{
        String username = "marywagura";
        this.mockMvc.perform(get("/api/users/" + username))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", is(username)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("marywagura")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.twitter_username", is("")));
    }
}
