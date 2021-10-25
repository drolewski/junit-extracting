package model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Origin {
    private final String reality;
    private final String city;
}
