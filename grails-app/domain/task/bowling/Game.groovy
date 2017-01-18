package task.bowling

class Game {

    static hasMany = [frames: Frame]

    static constraints = {
        frames(maxSize: 11)
    }

    def getBiggestFrame = {
        frames?.max { a, b -> a.frameNumber <=> b.frameNumber }
    }

    def isGameOver = {
        Frame biggestFrame = getBiggestFrame()
        frames?.size() >= 10 &&
                (biggestFrame.frameNumber == GameController.BONUS_FRAME_NUMBER ||
                        biggestFrame.frameNumber == GameController.LAST_FRAME_NUMBER &&
                        !biggestFrame.isSpare() &&
                        !biggestFrame.isStrike() &&
                        biggestFrame.secondRoll != null)
    }

    def createOrUpdateLastFrame(int knockedDownPins) {
        Frame biggestFrame = getBiggestFrame()

        if (!biggestFrame || biggestFrame.isNewFrameNeeded()) {
            biggestFrame = new Frame(game: this, frameNumber: biggestFrame ? biggestFrame.frameNumber + 1 : GameController.FIRST_FRAME_NUMBER, firstRoll: knockedDownPins).save()
        } else {
            biggestFrame.secondRoll = knockedDownPins
        }
        biggestFrame
    }

    @Override
    public String toString() {
        return "Game #" + id
    }
}
