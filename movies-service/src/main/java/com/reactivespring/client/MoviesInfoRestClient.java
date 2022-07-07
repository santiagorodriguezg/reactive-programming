package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.validation.Valid;
import java.time.Duration;

@Component
@Slf4j
public class MoviesInfoRestClient {

    @Autowired
    private WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId){

        var retrySpec = Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(ex-> ex instanceof MoviesInfoClientException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())));

        var url = moviesInfoUrl.concat("/{id}");
        return webClient
                .get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.info("Status code is : {}", clientResponse.statusCode().value());
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return Mono.error(new MoviesInfoClientException("there is no MovieInfo Available for the passed Id: "+ movieId,
                                clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                    responseMessage, clientResponse.statusCode().value()
                            )) );
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.info("Status code is : {}", clientResponse.statusCode().value());

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException(
                                    "Server Exception in MoviesInfoService " + responseMessage)));
                })
                .bodyToMono(MovieInfo.class)
                .retryWhen(retrySpec)
                .log();

    }
}
