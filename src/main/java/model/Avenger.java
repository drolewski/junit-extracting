package model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Avenger {
    private String realName;
    private String heroName;
    private String superPower;
    private Double height;
    private int age;
    private Sex sex;
    private Origin origin;
    private List<Avenger> teammates;
    private String weapon;

    public int getHeightInCm() {
        return (int) (height * 100);
    }

    public String getWeapon() {
        return Optional.ofNullable(weapon)
                .orElse(StringUtils.EMPTY);
    }

    public String getTeam() {
        return Optional.ofNullable(teammates)
                .orElse(emptyAvenger())
                .stream()
                .map(Avenger::getHeroName)
                .reduce("", (acu, current) -> acu + current + ";");
    }

    public List<String> getTeammateWeapons() {

        return Optional.ofNullable(teammates)
                .orElse(emptyAvenger())
                .stream()
                .map(Avenger::getWeapon)
                .filter(avengerWeapon -> !avengerWeapon.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Avenger> emptyAvenger() {
        return Collections.singletonList(Avenger.builder().heroName("You have no friends").build());
    }

}
