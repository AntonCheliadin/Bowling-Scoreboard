package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */

@TestFor(FrameService)
@Mock([Frame, Game, LastFrame, LastFrameService])
class FrameServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test next frame"() {
        when:
        def game = new Game().save()
        Frame secondFrame = new Frame(game: game, frameNumber: 2, firstRoll: 0).save()
        Frame thirdFrame = new Frame(game: game, frameNumber: 3, firstRoll: 5).save()

        then: "frame with the next frame number is returned"
        service.nextFrame(secondFrame) == thirdFrame

        then: "return null if frame with the next frame number is absent"
        service.nextFrame(thirdFrame) == null
    }

    void "test previous frame"() {
        when:
        def game = new Game().save()
        Frame firstFrame = new Frame(game: game, frameNumber: 1, firstRoll: 0).save()
        Frame secondFrame = new Frame(game: game, frameNumber: 2, firstRoll: 5).save()

        then: "frame with the previous frame number is returned"
        service.previousFrame(secondFrame) == firstFrame

        then: "return null if frame with the previous frame number is absent"
        service.previousFrame(firstFrame) == null
    }

    void "test spare"() {
        def frame = new Frame(frameNumber: 1, firstRoll: firstRoll, secondRoll: secondRoll)
        expect: "Spare - the remainder of the pins left standing after the first roll are knocked down on the second roll in a frame;"
        expected == service.isSpare(frame)

        where:
        firstRoll | secondRoll | expected
        10        | 0          | false
        4         | 6          | true
        0         | 10         | true
        3         | 5          | false
    }

    void "test strike"() {
        def frame = new Frame(firstRoll: firstRoll, secondRoll: secondRoll)
        expect: "Strike - all ten pins have been knocked down by the first roll of the ball in a frame."
        expected == service.isStrike(frame)

        where:
        firstRoll | secondRoll | expected
        10        | 0          | true
        4         | 6          | false
        3         | 5          | false
        0         | 10         | false
    }

    void "test sum of first and second rolls"() {
        when:
        def frame = new Frame(firstRoll: firstRoll, secondRoll: secondRoll)

        then:
        expected == service.sumRolls(frame)

        where:
        firstRoll | secondRoll | expected
        10        | null       | 10
        0         | 10         | 10
        4         | 3          | 7
        0         | 0          | 0
    }

    void "test is new frame needed"() {
        when:
        def frame = new Frame(frameNumber: frameNumber, firstRoll: firstRoll, secondRoll: secondRoll)

        then:
        expected == service.isNewFrameNeeded(frame)

        where:
        frameNumber | firstRoll | secondRoll | expected
        1           | 10        | null       | true
        1           | 0         | null       | false
        1           | 4         | 6          | true
    }

    void "test fill frame score"() {
        when:
        def game = new Game().save()
        def frame = new Frame(game: game, frameNumber: 1, firstRoll: firstRoll, secondRoll: secondRoll).save()
        if (thirdRoll != null) {
            new Frame(game: game, frameNumber: 2, firstRoll: thirdRoll, secondRoll: fourthRoll).save()
        }
        if (fifthRoll != null) {
            new Frame(game: game, frameNumber: 3, firstRoll: fifthRoll).save()
        }
        service.fillScore(frame)

        then:
        frame.score == expected

        where:
        firstRoll | secondRoll | thirdRoll | fourthRoll | fifthRoll | expected
        1         | null       | null      | null       | null      | null
        4         | 0          | null      | null       | null      | 4
        2         | 8          | null      | null       | null      | null
        2         | 8          | 4         | null       | null      | 14
        10        | null       | null      | null       | null      | null
        10        | null       | 5         | null       | null      | null
        10        | null       | 5         | 1          | null      | 16
        10        | null       | 10        | null       | null      | null
        10        | null       | 10        | null       | 7         | 27
    }

    void "test fill score for ninth frame"() {
        when: "strike in ninth frame and strike in tenth frame"
        def game = new Game().save()
        def eightFrame = new Frame(game: game, frameNumber: 8, firstRoll: 0, secondRoll: 0, score: 100).save()
        def ninthFrame = new Frame(game: game, frameNumber: 9, firstRoll: 10).save()
        def lastFrame = new LastFrame(game: game, frameNumber: 10, firstRoll: 10, secondRoll: 7, bonusRoll: 2).save()
        service.fillScore(ninthFrame)

        then: "score equals 10 plus next two rolls (both of them in last frame)"
        ninthFrame.score == 127
    }
}
