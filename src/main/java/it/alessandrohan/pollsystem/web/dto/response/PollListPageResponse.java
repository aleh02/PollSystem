package it.alessandrohan.pollsystem.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PollListPageResponse {
    private boolean first;

    private boolean last;

    private int size;

    private Long totalElements;

    private int totalPages;

    private int number;

    private List<PollResponse> contents;
}
