package task.bowling

import task.bowling.data.RollCommand

import static task.bowling.Constants.FIRST_FRAME_NUMBER
import static task.bowling.Constants.LAST_FRAME_NUMBER

class GameService {

    def frameService

    def executeRoll(Game game, RollCommand cmd) {
        if (isGameOver(game)) {
            return [error: 'task.bowling.game.over.message']
        }

        def frame = createOrUpdateBiggestFrame(game, cmd.knockedDownPins)

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
        game.frames?.max { it.frameNumber }
    }

    private Frame createOrUpdateBiggestFrame(Game game, Integer knockedDownPins) {
        Frame biggestFrame = getBiggestFrame(game)
        if (!biggestFrame || biggestFrame.isNewFrameNeeded()) {
            createFrame(game, biggestFrame, knockedDownPins)
        } else {
            updateFrame(biggestFrame, knockedDownPins)
        }
    }

    private Frame createFrame(Game game, Frame frame, Integer knockedDownPins) {
        def params = [game: game, frameNumber: frame ? frame.frameNumber + 1 : FIRST_FRAME_NUMBER, firstRoll: knockedDownPins]

        if (params['frameNumber'] == LAST_FRAME_NUMBER) {
            new LastFrame(params).save()
        } else {
            new Frame(params).save()
        }
    }

    private Frame updateFrame(Frame frame, Integer knockedDownPins) {
        if (frame.secondRoll == null) {
            frame.secondRoll = knockedDownPins
        } else {
            frame.bonusRoll = knockedDownPins
        }
        frame.save()
    }
}
