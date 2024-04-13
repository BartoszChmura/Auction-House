package com.auctionsystem.auctionhouse.mapper;


import com.auctionsystem.auctionhouse.dto.UserDto;
import com.auctionsystem.auctionhouse.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }

    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPasswordHash(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setUpdatedAt(userDto.getUpdatedAt());
        return user;
    }
}
