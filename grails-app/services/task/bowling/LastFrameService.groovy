package task.bowling

import static task.bowling.Constants.MIN_PIN

class LastFrameService extends FrameService {

    def sumRolls = {frame -> frame.firstRoll + (frame.secondRoll ?: MIN_PIN) + (frame.bonusRoll ?: MIN_PIN) }

    def isNewFrameNeeded = {frame -> false }

    void fillScore(Frame frame) {
        def previousFrame = fillPreviousFrameScore(frame)

        if (isFillScoreNeeded(frame)) {
            frame.score = previousFrame.score + sumRolls(frame)
        }
    }

    protected def isFillScoreNeeded = {frame ->
        frame.score == null &&
                (frame.bonusRoll != null || (!isStrike(frame) && !isSpare(frame) && frame.secondRoll != null))
    }
}
