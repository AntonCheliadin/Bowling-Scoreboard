package task.bowling

import static task.bowling.Constants.FIRST_FRAME_NUMBER
import static task.bowling.Constants.LAST_FRAME_NUMBER

class GameService {

    def frameService

    def executeRoll(Game game, Integer knockedDownPins) {
        if (isGameOver(game) || knockedDownPins == null) {
            return [error: 'task.bowling.game.over.message']
        }

        Frame frame = createOrUpdateBiggestFrame(game, knockedDownPins)

        if (!frame) {
            return [error: 'task.bowling.game.invalid.pins.message']
        }

        frameService.fillScore(frame)
    }

    def isGameOver(Game game) {
        Frame biggestFrame = getBiggestFrame(game)
        biggestFrame?.frameNumber == LAST_FRAME_NUMBER &&
                (biggestFrame.bonusRoll != null ||
                        !biggestFrame.isSpare() &&
                        !biggestFrame.isStrike() &&
                        biggestFrame.secondRoll != null)
    }

    def getBiggestFrame(Game game) {
        game.frames?.max { a, b -> a.frameNumber <=> b.frameNumber }
    }

    def createOrUpdateBiggestFrame(Game game, Integer knockedDownPins) {
        Frame biggestFrame = getBiggestFrame(game)
        if (!biggestFrame || biggestFrame.isNewFrameNeeded()) {

            def params = [game: game, frameNumber: biggestFrame ? biggestFrame.frameNumber + 1 : FIRST_FRAME_NUMBER, firstRoll: knockedDownPins]

            if (params['frameNumber'] == LAST_FRAME_NUMBER) {
                biggestFrame = new LastFrame(params)
            } else {
                biggestFrame = new Frame(params)
            }
        } else {
            if (biggestFrame.secondRoll == null) {
                biggestFrame.secondRoll = knockedDownPins
            } else {
                biggestFrame.bonusRoll = knockedDownPins
            }
        }
        biggestFrame.save()
    }
}
