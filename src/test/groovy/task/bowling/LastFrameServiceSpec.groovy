package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */

@TestFor(LastFrameService)
@Mock([Frame, Game, LastFrame])
class LastFrameServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test sum of rolls"() {
        when:
        def frame = new LastFrame(firstRoll: firstRoll, secondRoll: secondRoll, bonusRoll: bonusRoll)

        then:
        expected == service.sumRolls(frame)

        where:
        firstRoll | secondRoll | bonusRoll | expected
        10        | null       | null      | 10
        0         | 10         | null      | 10
        4         | 3          | 7         | 14
        0         | 0          | 0         | 0
        10        | 10         | 10        | 30
    }

    void "test is new frame needed"() {
        when:
        def frame = new LastFrame(frameNumber: 10, firstRoll: firstRoll, secondRoll: secondRoll, bonusRoll: bonusRoll)

        then:
        expected == service.isNewFrameNeeded(frame)

        where:
        firstRoll | secondRoll | bonusRoll | expected
        10        | null       | null       | false
        10        | 10         | null       | false
        3         | 3          | 7          | false
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
