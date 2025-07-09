package emh.dd_site;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfig.class)
class DdSiteApplicationIT {

    @Test
    void contextLoads() {
    }
}
