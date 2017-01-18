package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Game)
class GameSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    private def fillGame(Game game, int from, int to) {
        for (i in from..to) {
            new Frame(game: game, frameNumber: i, firstRoll: 10).save()
        }
    }

    void "test find biggest frame"() {
        given:
        def game = new Game().save()
        when: "add 5 frames"
        fillGame(game, 1, 6)
        then: "frame with frame number = 5 is the biggest"
        game.getBiggestFrame().frameNumber == 6
    }

    void "test is game over"() {
        given:
        def game = new Game().save()

        when: "game without any frames"
        then: "isn't finished"
        !game.isGameOver()

        when: "there are 5 frames"
        fillGame(game, 1, 5)
        then: "isn't finished"
        !game.isGameOver()

        when: "there are 10 frames, last frame without second roll"
        fillGame(game, 6, 9)
        def lastFrame = new Frame(game: game, frameNumber: 10, firstRoll: 3).save()
        then: "isn't finished"
        !game.isGameOver()

        when: "there are 10 frames, last frame without strike and spare"
        lastFrame.firstRoll = 3
        lastFrame.secondRoll = 6
        lastFrame.save()
        then: "game over"
        game.isGameOver()


        when: "there are 10 frames, last frame with double strike"
        lastFrame.firstRoll = 10
        lastFrame.secondRoll = 10
        lastFrame.save()
        then: "isn't finished"
        !game.isGameOver()

        when: "there are 10 frames, last frame with spare"
        lastFrame.firstRoll = 3
        lastFrame.secondRoll = 7
        lastFrame.save()
        then: "isn't finished"
        !game.isGameOver()

        when: "there are 11 frames"
        new Frame(game: game, frameNumber: 11, firstRoll: 3).save()
        then: "game over"
        game.isGameOver()
    }

    void "test create Or Update Last Frame"() {
        given:
        def game = new Game().save()

        when: "first roll in game"
        def frame = game.createOrUpdateLastFrame(10)
        then: "frame is created with frame number = 1"
        frame.frameNumber == 1

        when: "frames on the game isn't empty"
        frame = game.createOrUpdateLastFrame(5)
        then: "new frame create with next frame number"
        frame.frameNumber == 2
        frame.firstRoll == 5

        when: "knocked down pins valid"
        frame = game.createOrUpdateLastFrame(3)
        then: "frame is updated"
        frame.firstRoll == 5
        frame.secondRoll == 3
    }
}
