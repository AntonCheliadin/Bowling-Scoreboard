package task.bowling

import static task.bowling.Constants.MIN_PIN

class FrameService {

    void fillScore(Frame frame) {
        Frame previousFrame = fillPreviousFrameScore(frame)

        Frame nextFrame = frame.nextFrame()
        if (isFillScoreNeeded(frame, nextFrame)) {
            int totalScore = (previousFrame?.score ?: MIN_PIN) + frame.sumRolls()
            if (frame.isSpare()) {
                totalScore += nextFrame?.firstRoll ?: MIN_PIN
            } else if (frame.isStrike()) {
                totalScore += nextFrame.sumRolls()
                if (nextFrame.isStrike()) {
                    totalScore += nextFrame.nextFrame()?.firstRoll ?: MIN_PIN
                }
            }
            frame.score = totalScore
        }
    }

    void fillScore(LastFrame frame) {
        def previousFrame = fillPreviousFrameScore(frame)

        if (isFillScoreNeeded(frame)) {
            frame.score = previousFrame.score + frame.sumRolls()
        }
    }

    private fillPreviousFrameScore(Frame frame) {
        def previousFrame = frame.previousFrame()

        if (previousFrame && previousFrame.score == null) {
            fillScore(previousFrame)
        }
        previousFrame
    }

    private isFillScoreNeeded(Frame frame, Frame nextFrame) {
        if (frame.score == null) {
            if (frame.isStrike()) {
                nextFrame && (nextFrame.secondRoll != null || nextFrame.isStrike() && nextFrame.nextFrame())
            } else if (frame.isSpare()) {
                nextFrame != null
            } else {
                frame.secondRoll != null
            }
        }
    }

    private isFillScoreNeeded(LastFrame frame) {
        frame.score == null &&
                (frame.bonusRoll != null || (!frame.isStrike() && !frame.isSpare() && frame.secondRoll != null))
    }
}
