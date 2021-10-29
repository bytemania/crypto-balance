package com.github.bytemania.cryptobalance;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
class CryptoBalanceApplicationTests {

	@AfterAll
	static void afterAll() throws IOException {
		Files.deleteIfExists(Path.of("portfolio.db"));
	}

	@Test
	void contextLoads() {
	}

}
