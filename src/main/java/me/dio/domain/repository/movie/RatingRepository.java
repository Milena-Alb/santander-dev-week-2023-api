package me.dio.domain.repository.movie;

import me.dio.domain.model.movie.Rating;

import java.util.List;

public interface RatingRepository {
    void save(Rating rating);

    List<Rating> findByUserId(Long userId);
}
