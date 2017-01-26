package task.bowling

import static task.bowling.Constants.*

class GameService {

    def executeRoll(Game game, Integer knockedDownPins) {
        if (isGameOver(game) || knockedDownPins == null) {
            return [error: 'task.bowling.game.over.message']
        }

        Frame frame = createOrUpdateBiggestFrame(game, knockedDownPins)

        if (!frame) {
            return [error: 'task.bowling.game.invalid.pins.message']
        }

        frame.service.fillScore(frame)
    }


    def isGameOver(Game game){
        Frame biggestFrame = getBiggestFrame(game)
        game.frames?.size() == LAST_FRAME_NUMBER &&
                (biggestFrame instanceof LastFrame &&
                      (biggestFrame.bonusRoll != null ||
                            !biggestFrame.service.isSpare(biggestFrame) &&
                            !biggestFrame.service.isStrike(biggestFrame) &&
                            biggestFrame.secondRoll != null))
    }

    def getBiggestFrame = { game ->
        game.frames?.max { a, b -> a.frameNumber <=> b.frameNumber }
    }

    def createOrUpdateBiggestFrame(Game game, Integer knockedDownPins) {
        Frame biggestFrame = getBiggestFrame(game)
        if (!biggestFrame || biggestFrame.service.isNewFrameNeeded(biggestFrame)) {

            def params = [game: game, frameNumber: biggestFrame ? biggestFrame.frameNumber + 1 : FIRST_FRAME_NUMBER, firstRoll: knockedDownPins]

            if ( params['frameNumber'] == LAST_FRAME_NUMBER){
                biggestFrame = new LastFrame(params)
            } else {
                biggestFrame = new Frame(params)
            }
        } else {
            if ( biggestFrame.secondRoll == null){
                biggestFrame.secondRoll = knockedDownPins
            } else {
                biggestFrame.bonusRoll = knockedDownPins
            }
        }
        biggestFrame.save()
    }
}
