package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {

    @Autowired
    private MovieInfoRepository movieInfoRepository;



    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos(){
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id){
        return movieInfoRepository.findById(id);

    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id){

       return movieInfoRepository.findById(id)
                .flatMap(movieInfo1 -> {
                    movieInfo1.setCast(updatedMovieInfo.getCast());
                    movieInfo1.setName(updatedMovieInfo.getName());
                    movieInfo1.setRelease_date(updatedMovieInfo.getRelease_date());
                    movieInfo1.setYear(updatedMovieInfo.getYear());
                    return movieInfoRepository.save(movieInfo1);

                });
    }

    public Mono<Void> deleteMovieInfo(String id){
       return movieInfoRepository.deleteById(id);
    }
}
