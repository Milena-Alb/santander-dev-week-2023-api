package me.dio.domain.repository.movie;

import me.dio.domain.model.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.dio.domain.model.User;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<User, Long> {
    
    boolean existsByAccountNumber(String number);

    boolean existsByCardNumber(String number);

    List<Movie> findTopRatedByGenres(List<String> preferredGenres);

    List<Movie> findByGenreIn(List<String> preferredGenres);
}
