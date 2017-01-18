package task.bowling

import static task.bowling.GameController.*

class Frame {
    Integer frameNumber
    Integer firstRoll
    Integer secondRoll
    Integer score
    static belongsTo = [game: Game]

    static constraints = {
        frameNumber(range: 1..11)
        firstRoll(range: 0..10)
        secondRoll(range: 0..10, nullable: true)
        score(nullable: true)
        game(nullable: false)
    }

    static mapping = {
        game indexColumn: [name: "the_game", type: Game]
    }

    @Override
    boolean validate() {
        if (frameNumber == BONUS_FRAME_NUMBER && previousFrame()?.isStrike() && previousFrame()?.secondRoll != MAX_PIN) {
            //bonus frame
            firstRoll + (previousFrame().secondRoll) <= MAX_PIN
        } else if (frameNumber == LAST_FRAME_NUMBER && isStrike()) { // strike in last frame
            firstRoll + (secondRoll ?: MIN_PIN) <= MAX_PIN * 2
        } else if (isStrike()) {
            secondRoll == null
        } else {
            firstRoll + (secondRoll ?: MIN_PIN) <= MAX_PIN
        }
    }

    def nextFrame = { Frame.findByGameAndFrameNumber(this.game, this.frameNumber + 1) }

    def previousFrame = { Frame.findByGameAndFrameNumber(this.game, this.frameNumber - 1) }

    def isSpare = { sumRolls() == MAX_PIN && !isStrike() }

    def isStrike = { firstRoll == MAX_PIN }

    def sumRolls = { firstRoll + (secondRoll ?: MIN_PIN) }

    def isNewFrameNeeded = { secondRoll != null || isStrike() && frameNumber != LAST_FRAME_NUMBER }

    def fillScore() {
        def previousFrame = previousFrame()

        if (previousFrame && previousFrame.score == null) {
            previousFrame.fillScore()
        }

        Frame nextFrame = nextFrame()

        if (isFillScoreNeeded(nextFrame)) {
            int totalScore = (previousFrame?.score ?: MIN_PIN) + sumRolls()
            if (isSpare() || frameNumber == LAST_FRAME_NUMBER) {
                totalScore += nextFrame?.firstRoll ?: MIN_PIN
            } else if (isStrike()) {
                if (nextFrame.isStrike() && nextFrame.frameNumber != LAST_FRAME_NUMBER) {
                    totalScore += nextFrame.firstRoll + nextFrame.nextFrame().firstRoll
                } else {
                    totalScore += nextFrame.sumRolls()
                }
            }
            this.score = totalScore
        }
    }

    private def isFillScoreNeeded = { nextFrame ->
        if (isStrike()) {
            if (frameNumber == LAST_FRAME_NUMBER) {
                nextFrame != null
            } else {
                nextFrame && (nextFrame.secondRoll != null || nextFrame.isStrike() && nextFrame.nextFrame())
            }
        } else if (isSpare()) {
            nextFrame != null
        } else {
            secondRoll != null
        }
    }

    @Override
    public String toString() {
        return "Frame{" +
                "game=" + game +
                ", frameNumber=" + frameNumber +
                ", firstRoll=" + firstRoll +
                ", secondRoll=" + secondRoll +
                ", score=" + score +
                '}';
    }
}

