package it.alessandrohan.pollsystem.mapper;

import it.alessandrohan.pollsystem.model.Vote;
import it.alessandrohan.pollsystem.web.dto.response.VoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(source = "pollOption.id", target = "optionId")
    VoteResponse voteToVoteResponse(Vote vote);

    default String map(Long value) {
        return value == null ? null : value.toString();
    }
}
