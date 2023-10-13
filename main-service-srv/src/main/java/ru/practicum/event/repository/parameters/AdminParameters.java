package ru.practicum.event.repository.parameters;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminParameters {
    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
}
