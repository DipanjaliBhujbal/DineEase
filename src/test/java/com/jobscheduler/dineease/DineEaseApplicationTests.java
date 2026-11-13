package com.jobscheduler.dineease;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "dineease.admin.password=test-only-password")
class DineEaseApplicationTests {

    @Test
    void contextLoads() {
    }

}
