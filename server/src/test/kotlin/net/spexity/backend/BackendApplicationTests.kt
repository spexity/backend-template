package net.spexity.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer

@SpringBootTest
class BackendApplicationTests {

	@Test
	fun contextLoads() {
	}

	@TestConfiguration(proxyBeanMethods = false)
	class TestcontainersConfiguration {

		@Bean
		@ServiceConnection
		fun postgresContainer(): PostgreSQLContainer {
			return PostgreSQLContainer("postgres:18")
		}
	}

}
