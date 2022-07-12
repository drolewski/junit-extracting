import model.Avenger;
import model.Origin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockTest {

    @Mock
    private Origin origin;
    @InjectMocks
    private Avenger avenger;

    @Before
    public void setup() {
        when(origin.getCity()).thenReturn("Los Angeles");
    }

    @Test
    public void test() {
        assertThat(avenger.getOrigin()).extracting("city").contains("Los Angeles");
    }

}
