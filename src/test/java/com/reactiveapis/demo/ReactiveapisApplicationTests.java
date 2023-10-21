package com.reactiveapis.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		properties = {
				"spring.main.allow-bean-definition-overriding=true",
				"spring.r2dbc.username=test",
				"spring.r2dbc.password=test",
				"spring.r2dbc.url=r2dbc:postgresql://127.0.0.1:5439/test",

})
class ReactiveapisApplicationTests {

	@Test
	void contextLoads() {
	}

}
