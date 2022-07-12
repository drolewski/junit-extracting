import model.Avenger;
import model.Origin;
import model.Sex;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.iterable.Extractor;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.in;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class AvengerTest {

    private static final String SHIELD = "Shield";

    public static Avenger THOR = Avenger.builder()
            .heroName("Thor")
            .realName("Thor")
            .age(1500)
            .height(1.91)
            .superPower("Power of Storm + Hammer")
            .sex(Sex.MALE)
            .origin(new Origin("Space", "Asgard"))
            .teammates(Collections.EMPTY_LIST)
            .weapon("Hammer")
            .build();

    public static Avenger IRON_MAN = Avenger.builder()
            .heroName("Iron Man")
            .realName("Tony Stark")
            .age(48)
            .height(1.98)
            .superPower("Money, Brain and Rocket Suit")
            .sex(Sex.MALE)
            .origin(new Origin("Earth", "Los Angeles"))
            .teammates(Collections.singletonList(THOR))
            .weapon("Suit")
            .build();

    public static Avenger HULK = Avenger.builder()
            .heroName("Hulk")
            .realName("Robert Bruce Banner")
            .age(49)
            .height(2.5)
            .superPower("GREEN")
            .sex(Sex.MALE)
            .origin(new Origin("Earth", "Dayton"))
            .teammates(Collections.singletonList(THOR))
            .weapon(null)
            .build();

    public static Avenger CAPTAIN_AMERICA = Avenger.builder()
            .heroName("Captain America")
            .realName("Steve Rogers")
            .age(93)
            .height(1.86)
            .superPower("Shield")
            .sex(Sex.MALE)
            .origin(new Origin("Earth", "New York City"))
            .teammates(Arrays.asList(THOR, HULK, IRON_MAN))
            .weapon(SHIELD)
            .build();

    public static Avenger CAPTAIN_MARVEL = Avenger.builder()
            .heroName("Captain Marvel")
            .realName("Carol Danvers")
            .age(56)
            .height(1.70)
            .superPower("Strength")
            .sex(Sex.FEMALE)
            .origin(new Origin("Earth", "Dallas"))
            .teammates(Collections.EMPTY_LIST)
            .build();

    public static Avenger VENOM = Avenger.builder()
            .heroName("Venom")
            .realName("Eddie Brock")
            .age(40)
            .height(1.91)
            .superPower("Alien")
            .sex(Sex.MALE)
            .origin(new Origin("Earth", "New York"))
            .teammates(null)
            .build();

    @Test
    void assertionDescription() {
        // test description
        assertThat(THOR.getHeight()).as("check %s's height", THOR.getHeroName()).isEqualTo(1.913);
    }

    @Test
    void filteredOn() {
        var avengers = Arrays.asList(THOR, IRON_MAN, HULK, CAPTAIN_AMERICA, CAPTAIN_MARVEL);

        // containsOnly - duplicates insensitive (check set)
        assertThat(avengers).filteredOn(avenger -> avenger.getHeight() < 2)
                .containsOnly(THOR, IRON_MAN, CAPTAIN_AMERICA, CAPTAIN_MARVEL);

        // containsExactly - duplicates sensitive (fails if number of elements are differed)
        assertThat(avengers).filteredOn(avenger -> avenger.getHeroName().contains("a"))
                .containsExactlyInAnyOrder(IRON_MAN, CAPTAIN_AMERICA, CAPTAIN_MARVEL);

        // field (nested fields too: origin.city)
        assertThat(avengers).filteredOn("sex", in(Sex.MALE))
                .containsOnly(THOR, IRON_MAN, HULK, CAPTAIN_AMERICA);

        assertThat(avengers).filteredOn(originCondition())
                .containsExactlyInAnyOrder(IRON_MAN, HULK, CAPTAIN_AMERICA, CAPTAIN_MARVEL);
    }

    @Test
    void containsExactly() {
        var avengers = Arrays.asList(THOR, IRON_MAN, HULK, CAPTAIN_AMERICA, THOR);

        assertThat(avengers).containsOnly(THOR, IRON_MAN, HULK, CAPTAIN_AMERICA)
                .containsExactlyInAnyOrder(THOR, IRON_MAN, HULK, CAPTAIN_AMERICA, THOR)
                .containsExactlyInAnyOrder(THOR, IRON_MAN, HULK, CAPTAIN_AMERICA); //
    }

    @Test
    void extracting() {
        var avengers = Arrays.asList(THOR, IRON_MAN);

        assertThat(avengers).extracting(Avenger::getHeroName)
                .contains(THOR.getHeroName(), IRON_MAN.getHeroName(), HULK.getHeroName())
                .doesNotContain(HULK.getHeroName(), CAPTAIN_MARVEL.getHeroName());

        assertThat(avengers).extracting("heroName", "origin.city", "sex")
                .containsExactlyInAnyOrder(getHeroTuple(THOR), getHeroTuple(IRON_MAN))
                .doesNotContain(getHeroTuple(HULK));
    }

    @Test
    void flatExtracting() {
        var customExtractor = new teammatesExtractor();
        var avengers = Arrays.asList(THOR, IRON_MAN, CAPTAIN_AMERICA);

        assertThat(avengers).flatExtracting("teammates")
                .containsOnly(THOR, IRON_MAN, HULK);

        // great example of containsExactly
        assertThat(avengers).flatExtracting("teammates")
                .containsExactlyInAnyOrder(THOR, THOR, IRON_MAN, HULK);

        // custom extractor
        assertThat(avengers).flatExtracting(customExtractor)
                .containsOnly(THOR, IRON_MAN, HULK);
    }

    @Test
    void extractingResultOf() {
        var avengers = Arrays.asList(THOR, IRON_MAN, CAPTAIN_AMERICA);

        // extract method result
        assertThat(avengers).extractingResultOf("getHeightInCm")
                .contains(191, 198, 186);
    }

    @Test
    void softAssertions() {
        // run all assertions
        // collect all failed assertions
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(THOR.getAge()).as("Age").isEqualTo(1501);
            softly.assertThat(THOR.getHeroName()).as("HeroName").isEqualTo("Thors");
        });
    }

    @Test
    void fieldByFieldComparison() {
        // compare only given fields
        assertThat(THOR).isEqualToComparingOnlyGivenFields(IRON_MAN, "sex");
        assertThat(IRON_MAN).isEqualToComparingOnlyGivenFields(CAPTAIN_MARVEL, "origin.reality");

        // failed, sex is not equal
        assertThat(HULK).isEqualToComparingOnlyGivenFields(CAPTAIN_MARVEL, "origin.reality", "sex");
    }

    @Test
    void optionalWeaponAssertion() {
        var avengers = Arrays.asList(CAPTAIN_MARVEL, HULK, CAPTAIN_AMERICA);

        assertThat(avengers).extractingResultOf("getWeapon").containsExactly(StringUtils.EMPTY, StringUtils.EMPTY, SHIELD);
    }

    @Test
    void reduceTeam() {
        assertThat(Collections.singletonList(VENOM)).extractingResultOf("getTeam").containsExactly("You have no friends;");
        assertThat(Collections.singletonList(HULK)).extractingResultOf("getTeam").containsExactly("Thor;");
    }

    @Test
    void filterAndCollect() {
        assertThat(Collections.singletonList(HULK)).extractingResultOf("getTeammateWeapons").containsExactly(Collections.singletonList(THOR.getWeapon()));
        assertThat(Collections.singletonList(VENOM)).extractingResultOf("getTeammateWeapons").containsExactly(Collections.emptyList());
    }

    static class teammatesExtractor implements Extractor<Avenger, List<Avenger>> {
        @Override
        public List<Avenger> extract(Avenger avenger) {
            return avenger.getTeammates();
        }
    }

    private static Tuple getHeroTuple(Avenger hero) {
        return tuple(hero.getHeroName(), hero.getOrigin().getCity(), hero.getSex());
    }

    private static Condition<Avenger> originCondition() {
        return new Condition<>() {
            @Override
            public boolean matches(Avenger avenger) {
                return avenger.getOrigin().getReality().equals("Earth");
            }
        };
    }

}
