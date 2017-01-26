package task.bowling

import static task.bowling.Constants.*

class FrameService {

    def nextFrame = {frame -> Frame.findByGameAndFrameNumber(frame.game, frame.frameNumber + 1) }

    def previousFrame = {frame -> Frame.findByGameAndFrameNumber(frame.game, frame.frameNumber - 1) }

    def isSpare = {frame -> sumRolls(frame) == MAX_PIN && !isStrike(frame) }

    def isStrike = {frame -> frame.firstRoll == MAX_PIN }

    def sumRolls = {frame -> frame.firstRoll + (frame.secondRoll ?: MIN_PIN) }

    def isNewFrameNeeded = {frame -> frame.secondRoll != null || isStrike(frame) }

    void fillScore(Frame frame) {
        def previousFrame = fillPreviousFrameScore(frame)

        Frame nextFrame = nextFrame(frame)
        if (isFillScoreNeeded(frame, nextFrame)) {
            int totalScore = (previousFrame?.score ?: MIN_PIN) + sumRolls(frame)
            if (isSpare(frame)) {
                totalScore += nextFrame?.firstRoll ?: MIN_PIN
            } else if (isStrike(frame)) {
                totalScore += sumRolls(nextFrame)
                if (isStrike(nextFrame)) {
                    totalScore += this.nextFrame(nextFrame)?.firstRoll ?: MIN_PIN
                }
            }
            frame.score = totalScore
        }
    }

    protected def fillPreviousFrameScore(Frame frame){
        def previousFrame = previousFrame(frame)

        if (previousFrame && previousFrame.score == null) {
            previousFrame.service.fillScore(previousFrame)
        }
        previousFrame
    }

    protected def isFillScoreNeeded = {frame, nextFrame ->
        if (frame.score == null) {
            if (isStrike(frame)) {
                nextFrame && (nextFrame.secondRoll != null || isStrike(nextFrame) && this.nextFrame(nextFrame))
            } else if (isSpare(frame)) {
                nextFrame != null
            } else {
                frame.secondRoll != null
            }
        }
    }
}
