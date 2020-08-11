package com.guedes.herlon.game.service;

import com.guedes.herlon.game.general.enums.BonusRule;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.FrameService;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class FrameServiceImpl implements FrameService {
    @Override
    public long calculateFrameScore(List<Frame> frames, int frameNumber) {
        long frameScore = 0;
        if(frameNumber >= 0) {
            frameScore += calculateFrameScore(frames, frameNumber - 1);
            frameScore += frameNumber < frames.size() - 1 ? calculateFrameBonus(frames, frameNumber) : 0L;
            frameScore += frames.get(frameNumber).getTotalKnockedDownPins();
            frames.get(frameNumber).setScore(frameScore);
        }
        return frameScore;
    }

    private long calculateFrameBonus(List<Frame> frames, int frameNumber) {
        long bonus = 0L;
        if (frames.get(frameNumber).isStrike()) {
            bonus = calculateBonusUsing(BonusRule.STRIKE, frames, frameNumber);
        } else if (frames.get(frameNumber).isSpare()) {
            bonus = calculateBonusUsing(BonusRule.SPARE, frames, frameNumber);
        }
        return bonus;
    }

    private long calculateBonusUsing(BonusRule rule, List<Frame> frames, int frameNumber) {
        Long bonusValue = 0L;

        if(frameNumber < frames.size()-1) {
            List<PlayerThrow> playerThrows = getThrowsFrom(frames);

            @NonNull List<PlayerThrow> playerThrowOnFrame = frames.get(frameNumber).getPlayerThrowList();
            int indexOfLastThrow = playerThrows.indexOf(playerThrowOnFrame.get(playerThrowOnFrame.size()-1));

            int amountThrowsToUse = Math.min(playerThrows.size() - indexOfLastThrow, rule.getThrowsToUse());

            List<PlayerThrow> playerThrowsToUseAsBonuses = playerThrows.subList(indexOfLastThrow + 1, indexOfLastThrow + 1 + amountThrowsToUse);

            bonusValue += playerThrowsToUseAsBonuses.stream().mapToLong(PlayerThrow::getKnockedDownPins).sum();
        } else {
            bonusValue += frames.get(frameNumber).getTotalKnockedDownPins();
        }

        return bonusValue;
    }

    @Override
    public List<PlayerThrow> getThrowsFrom(List<Frame> frames) {
        return frames.stream().flatMap(frame -> frame.getPlayerThrowList().stream()).collect(Collectors.toList());
    }
}
