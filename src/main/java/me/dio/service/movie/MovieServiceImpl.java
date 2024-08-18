package me.dio.service.movie;

import me.dio.domain.model.movie.Movie;
import me.dio.controller.dto.UserDto;
import me.dio.domain.model.movie.Rating;
import me.dio.domain.repository.movie.MovieRepository;
import me.dio.domain.repository.movie.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, RatingRepository ratingRepository) {
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public List<UserDto.MovieDto> getRecommendations(Long userId) {
        // 1. Obter as avaliações do usuário
        List<Rating> userRatings = ratingRepository.findByUserId(userId);

        if (userRatings.isEmpty()) {
            // Se o usuário não tem avaliações, retorne uma lista vazia ou algum filme popular
            return new ArrayList<>();
        }

        // 2. Identificar os gêneros preferidos
        Map<String, Integer> genreCount = new HashMap<>();
        for (Rating rating : userRatings) {
            Movie movie = rating.getMovie();
            String genre = movie.getGenre();
            genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
        }

        // Ordenar gêneros por preferência
        List<String> preferredGenres = genreCount.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 3. Buscar filmes bem avaliados nos gêneros preferidos
        List<Movie> recommendedMovies = movieRepository.findTopRatedByGenres(preferredGenres);

        // 4. Converter os filmes para DTO e retornar
        List<UserDto.MovieDto> movieDtos = recommendedMovies.stream()
                .map(movie -> new UserDto.MovieDto(movie.getId(), movie.getTitle(), movie.getGenre()))
                .collect(Collectors.toList());

        return movieDtos;
    }
}

