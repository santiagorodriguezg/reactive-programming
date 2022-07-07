package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.swing.UIManager.get;
import static org.springframework.cloud.contract.verifier.converter.YamlContract.PredefinedRegex.url;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084) // spin up a httpserver in port 8084
@TestPropertySource(
        properties = {
                "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
                "restClient.moviesInfoUrl=http://localhost:8084/v1/reviews"
        }
)
public class MoviesControllerIntgTest {
        @Autowired
        WebTestClient webTestClient;
        @Test
        void retrieveMovieById(){

             /*   var movieId = "abc";
                stubFor(get(urlEqualTo("/v1/movieinfos"+"/"+movieId))
                        .willReturn(aResponse().
                                withHeader("Content-Type","application/json")
                                .withBodyFile("movieinfo.json")));

                stubFor(get(urlEqualTo("/v1/reviews"))
                        .willReturn(aResponse().
                                withHeader("Content-Type","application/json")
                                .withBodyFile("reviews.json")));*/

                webTestClient
                        .get()
                        .uri("/v1/movies{id}")
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(Movie.class);


        }


}
