package com.guedes.herlon.game;

import com.guedes.herlon.game.controller.GameController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TenPinBowlingScoreGameApplicationTests {

	@Autowired
	private GameController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
