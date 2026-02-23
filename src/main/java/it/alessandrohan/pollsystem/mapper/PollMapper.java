package it.alessandrohan.pollsystem.mapper;

import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.web.dto.request.PollCreateRequest;
import it.alessandrohan.pollsystem.web.dto.response.PollResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PollMapper {

    @Mapping(target = "owner", source = "owner.username")
    PollResponse pollToPollResponse(Poll poll);

    List<PollResponse> pollListToPollResponseList(List<Poll> polls);

    Poll pollCreateReqToPoll(PollCreateRequest request);
}
