package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */

@TestFor(FrameService)
@Mock([Frame, Game, LastFrame])
class FrameServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test previous frame"() {
        when:
        def game = new Game().save()
        Frame firstFrame = new Frame(game: game, frameNumber: 1, firstRoll: 0).save()
        Frame secondFrame = new Frame(game: game, frameNumber: 2, firstRoll: 5).save()

        then: "frame with the previous frame number is returned"
        secondFrame.previousFrame() == firstFrame

        then: "return null if frame with the previous frame number is absent"
        firstFrame.previousFrame() == null
    }

    void "test spare"() {
        def frame = new Frame(frameNumber: 1, firstRoll: firstRoll, secondRoll: secondRoll)
        expect: "Spare - the remainder  the pins left standing after the first roll are knocked down on the second roll in a frame;"
        expected == frame.isSpare()

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
        expected == frame.isStrike()

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
        expected == frame.sumRolls()

        where:
        firstRoll | secondRoll | expected
        10        | null       | 10
        0         | 10         | 10
        4         | 3          | 7
        0         | 0          | 0
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
        ninthFrame.score == 129
    }


    void "test sum of rolls"() {
        when:
        def frame = new LastFrame(firstRoll: firstRoll, secondRoll: secondRoll, bonusRoll: bonusRoll)

        then:
        expected == frame.sumRolls()

        where:
        firstRoll | secondRoll | bonusRoll | expected
        10        | null       | null      | 10
        0         | 10         | null      | 10
        4         | 3          | 7         | 14
        0         | 0          | 0         | 0
        10        | 10         | 10        | 30
    }

    void "test fill score for last frame"() {
        when: "frame with number 9 has score equals 0"
        def game = new Game().save()
        new Frame(game: game, frameNumber: 9, firstRoll: 0, secondRoll: 0, score: 0).save()
        def frame = new LastFrame(game: game, frameNumber: 10, firstRoll: firstRoll, secondRoll: secondRoll, bonusRoll: bonusRoll).save()

        service.fillScore(frame)

        then:
        frame.score == expected

        where:
        firstRoll | secondRoll | bonusRoll | expected
        10        | null       | null      | null
        10        | 10         | null      | null
        10        | 10         | 1         | 21
        10        | 0          | 7         | 17
        0         | 5          | null      | 5
        6         | 4          | null      | null
        6         | 4          | 10        | 20
    }
}
