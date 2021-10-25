package model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
public class Avenger {
    private final String realName;
    private final String heroName;
    private final String superPower;
    private final Double height;
    private final int age;
    private final Sex sex;
    private final Origin origin;
    private final List<Avenger> teamMates;

    public int getHeightInCm() {
        return (int) (height * 100);
    }
}
