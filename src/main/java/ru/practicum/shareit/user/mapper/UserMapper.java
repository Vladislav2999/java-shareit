
package ru.practicum.shareit.user.mapper;

        import org.mapstruct.Mapper;
        import org.mapstruct.ReportingPolicy;
        import ru.practicum.shareit.user.model.User;
        import ru.practicum.shareit.user.model.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

 UserDto toUserDto(User user);

 User toUser(UserDto userDto);
}