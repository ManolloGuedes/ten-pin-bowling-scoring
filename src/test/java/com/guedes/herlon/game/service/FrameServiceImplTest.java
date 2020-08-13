package com.guedes.herlon.game.service;

import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.general.factory.PlayerThrowFactory;
import com.guedes.herlon.game.model.FrameImpl;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.FrameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FrameServiceImplTest {
	@Autowired
	FrameService frameService;

	@Test
	void strikeFrameScoreCalculationTest() {
		List<Frame> frames = new ArrayList<>();
		LongStream.range(0, 10).forEach(i -> {
			List<PlayerThrow> throwList = new ArrayList<>();
			throwList.add(PlayerThrowFactory.createCommonInstance(10, true, false));
			if(i == Constants.MAX_NUMBER_OF_FRAMES - 1) {
				for(int num = 1; num < Constants.LAST_FRAME_MAX_NUMBER_THROWS; num++) {
					throwList.add(PlayerThrowFactory.createCommonInstance(10, true, false));
				}
			}
			frames.add(new FrameImpl(throwList, i));
		});

		assertAll(
				() -> assertEquals(30, frameService.calculateFrameScore(frames, 0)),
				() -> assertEquals(300, frameService.calculateFrameScore(frames, frames.size() - 1))
		);
	}

	@Test
	void spareFrameScoreCalculationTest() {
		List<Frame> frames = new ArrayList<>();
		LongStream.range(0, 10).forEach(i -> {
			List<PlayerThrow> throwList = new ArrayList<>();
			throwList.add(PlayerThrowFactory.createCommonInstance(5, false, true));
			throwList.add(PlayerThrowFactory.createCommonInstance(5, false, true));
			frames.add(new FrameImpl(throwList, i));
		});

		assertEquals(15, frameService.calculateFrameScore(frames, 0));
	}
}
