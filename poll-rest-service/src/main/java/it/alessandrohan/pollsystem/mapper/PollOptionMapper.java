package it.alessandrohan.pollsystem.mapper;

import it.alessandrohan.pollsystem.model.PollOption;
import it.alessandrohan.pollsystem.web.dto.request.PollOptionCreateRequest;
import it.alessandrohan.pollsystem.web.dto.response.PollOptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PollOptionMapper {

    PollOption pollOptionCreateReqToPollOption(PollOptionCreateRequest request);

    @Mapping(source = "id", target = "id")
    PollOptionResponse pollOptionToPollOptionResponse(PollOption pollOption);

    List<PollOptionResponse> pollOptionListToPollOptionResponseList(List<PollOption> options);
}
