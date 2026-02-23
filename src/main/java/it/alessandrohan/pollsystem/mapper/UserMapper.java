package it.alessandrohan.pollsystem.mapper;

import it.alessandrohan.pollsystem.model.User;
import it.alessandrohan.pollsystem.web.dto.request.RegistrationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userCreateReqToUser(RegistrationRequest request);

}
