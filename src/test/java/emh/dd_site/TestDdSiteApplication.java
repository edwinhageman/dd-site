package emh.dd_site;

import org.springframework.boot.SpringApplication;

public class TestDdSiteApplication {

    public static void main(String[] args) {
        SpringApplication.from(DdSiteApplication::main).with(TestcontainersConfig.class).run(args);
    }
}
