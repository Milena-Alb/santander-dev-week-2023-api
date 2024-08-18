package me.dio.service.impl;

import me.dio.domain.model.User;
import me.dio.domain.model.movie.Movie;
import me.dio.domain.model.movie.Rating;
import me.dio.domain.repository.UserRepository;
import me.dio.domain.repository.movie.MovieRepository;
import me.dio.domain.repository.movie.RatingRepository;
import me.dio.service.UserService;
import me.dio.service.exception.BusinessException;
import me.dio.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class UserServiceImpl implements UserService {

    /**
     * ID de usuário utilizado na Santander Dev Week 2023.
     * Por isso, vamos criar algumas regras para mantê-lo integro.
     */
    private static final Long UNCHANGEABLE_USER_ID = 1L;

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;

    public UserServiceImpl(UserRepository userRepository, MovieRepository movieRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new NotFoundException("Movie not found"));
    }

    @Transactional
    public User create(User userToCreate) {
        ofNullable(userToCreate).orElseThrow(() -> new BusinessException("User to create must not be null."));

        this.validateChangeableId(userToCreate.getId(), "created");
        if (userRepository.existsById(userToCreate.getId())) {
            throw new BusinessException("This user already exists.");
        }
        return this.userRepository.save(userToCreate);
    }

    @Transactional
    public User update(Long id, User userToUpdate) {
        this.validateChangeableId(id, "updated");
        User dbUser = this.findById(id);
        if (!dbUser.getId().equals(userToUpdate.getId())) {
            throw new BusinessException("Update IDs must be the same.");
        }

        dbUser.setName(userToUpdate.getName());
        dbUser.setWatchedMovies(userToUpdate.getWatchedMovies());
        dbUser.setFavoriteMovies(userToUpdate.getFavoriteMovies());
        dbUser.setPreferredGenres(userToUpdate.getPreferredGenres());

        return this.userRepository.save(dbUser);
    }

    @Transactional
    public void delete(Long id) {
        this.validateChangeableId(id, "deleted");
        User dbUser = this.findById(id);
        this.userRepository.delete(dbUser);
    }

    @Transactional(readOnly = true)
    public List<Movie> getRecommendedMovies(Long userId) {
        User user = this.findById(userId);
        // Implementar lógica de recomendação baseada nas preferências do usuário
        return movieRepository.findByGenreIn(user.getPreferredGenres());
    }

    @Transactional
    public void rateMovie(Long userId, Long movieId, int ratingValue) {
        User user = this.findById(userId);
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new NotFoundException("Movie not found"));

        Rating rating = new Rating(user, movie, ratingValue);
        ratingRepository.save(rating);
    }

    private void validateChangeableId(Long id, String operation) {
        if (UNCHANGEABLE_USER_ID.equals(id)) {
            throw new BusinessException("User with ID %d cannot be %s.".formatted(UNCHANGEABLE_USER_ID, operation));
        }
    }
}
