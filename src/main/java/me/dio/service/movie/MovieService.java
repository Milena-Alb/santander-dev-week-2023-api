package me.dio.service.movie;

import me.dio.controller.dto.UserDto;

import java.util.List;

public interface MovieService {
    List<UserDto.MovieDto> getRecommendations(Long userId);
}
