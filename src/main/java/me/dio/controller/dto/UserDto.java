package me.dio.controller.dto;

import me.dio.domain.model.User;
import me.dio.domain.model.movie.Movie;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public record UserDto(
        Long id,
        String name,
        List<MovieDto> watchedMovies,
        List<MovieDto> favoriteMovies,
        List<String> preferredGenres) {

    public UserDto(User model) {
        this(
                model.getId(),
                model.getName(),
                Optional.ofNullable(model.getWatchedMovies()).orElse(emptyList()).stream().map(MovieDto::new).collect(toList()),
                Optional.ofNullable(model.getFavoriteMovies()).orElse(emptyList()).stream().map(MovieDto::new).collect(toList()),
                Optional.ofNullable(model.getPreferredGenres()).orElse(emptyList())
        );
    }

    public User toModel() {
        User model = new User();
        model.setId(this.id);
        model.setName(this.name);
        model.setWatchedMovies(Optional.ofNullable(this.watchedMovies).orElse(emptyList()).stream().map(MovieDto::toModel).collect(toList()));
        model.setFavoriteMovies(Optional.ofNullable(this.favoriteMovies).orElse(emptyList()).stream().map(MovieDto::toModel).collect(toList()));
        model.setPreferredGenres(Optional.ofNullable(this.preferredGenres).orElse(emptyList()));
        return model;
    }

    public static class MovieDto {
        Long id;
        String title;
        String genre;

        public MovieDto(Long id, String title, String genre) {
            this.id = id;
            this.title = title;
            this.genre = genre;
        }

        public MovieDto(Movie model) {
            this(model.getId(), model.getTitle(), model.getGenre());
        }

        public Movie toModel() {
            Movie model = new Movie();
            model.setId(this.id);
            model.setTitle(this.title);
            model.setGenre(this.genre);
            return model;
        }
    }
}
