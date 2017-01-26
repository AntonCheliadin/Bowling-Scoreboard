package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */

@TestFor(GameService)
@Mock([Frame, LastFrame, Game, FrameService, LastFrameService])
class GameServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    private def fillGame(Game game, int from, int to) {
        for (i in from..to) {
            new Frame(game: game, frameNumber: i, firstRoll: 10).save()
        }
    }

    void "test execute roll"(){
        given:
        def game = new Game().save()

        when:"value knocked down pins is invalid"
        def result = service.executeRoll(game, 11)

        then:"error is returned"
        result.error != null

        when:"value knocked down pins is valid"
        result = service.executeRoll(game, 10)

        then:"error isn't returned"
        result?.error == null

        when:"game over"
        fillGame(game, 2, 9)
        new LastFrame(game: game, frameNumber: 10, firstRoll: 4, secondRoll: 5).save()
        result = service.executeRoll(game, 5)

        then:"error is returned"
        result.error != null
    }

    void "test find biggest frame"() {
        given:
        def game = new Game().save()
        when: "add 6 frames"
        fillGame(game, 1, 6)
        then: "frame with frame number = 6 is the biggest"
        service.getBiggestFrame(game).frameNumber == 6
    }

    void "test is game over"() {
        given:
        def game = new Game().save()

        when: "game without any frames"
        then: "isn't finished"
        !service.isGameOver(game)

        when: "there are 5 frames"
        fillGame(game, 1, 5)
        then: "isn't finished"
        !service.isGameOver(game)

        when: "there are 10 frames, last frame without second roll"
        fillGame(game, 6, 9)
        def lastFrame = new LastFrame(game: game, frameNumber: 10, firstRoll: 3).save()
        then: "isn't finished"
        !service.isGameOver(game)

        when: "there are 10 frames, last frame without strike and spare"
        lastFrame.firstRoll = 3
        lastFrame.secondRoll = 6
        lastFrame.save()
        then: "game over"
        service.isGameOver(game)


        when: "there are 10 frames, last frame with double strike"
        lastFrame.firstRoll = 10
        lastFrame.secondRoll = 10
        lastFrame.save()
        then: "isn't finished"
        !service.isGameOver(game)

        when: "there are 10 frames, last frame with spare"
        lastFrame.firstRoll = 3
        lastFrame.secondRoll = 7
        lastFrame.save()
        then: "isn't finished"
        !service.isGameOver(game)

        when: "there are 11 frames"
        lastFrame.bonusRoll = 3
        lastFrame.save()
        then: "game over"
        service.isGameOver(game)
    }

    void "test create Or Update Last Frame"() {
        given:
        def game = new Game().save()

        when: "first roll in game"
        Frame frame = service.createOrUpdateBiggestFrame(game, 10)
        then: "frame is created with frame number = 1"
        frame.frameNumber == 1

        when: "frames on the game isn't empty"
        frame = service.createOrUpdateBiggestFrame(game, 5)
        then: "new frame create with next frame number"
        frame.frameNumber == 2
        frame.firstRoll == 5

        when: "knocked down pins valid"
        frame = service.createOrUpdateBiggestFrame(game, 3)
        then: "frame is updated"
        frame.firstRoll == 5
        frame.secondRoll == 3

        when: "knocked down pins invalid"
        frame = service.createOrUpdateBiggestFrame(game, 11)
        then: "null is returned"
        frame == null
    }
}
