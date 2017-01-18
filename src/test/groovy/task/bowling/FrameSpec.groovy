package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Frame)
@Mock([Frame, Game])
class FrameSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test validation"() {
        given:
        def game = new Game().save()

        when: "sum of rolls less than 10"
        def frame = new Frame(game: game, frameNumber: 1, firstRoll: 1, secondRoll: 2)
        then: "frame is valid"
        frame.validate()

        when: "spare frame"
        frame = new Frame(game: game, frameNumber: 2, firstRoll: 4, secondRoll: 6)
        then: "frame is valid"
        frame.validate()

        when: "sum of rolls grater than 10"
        frame = new Frame(game: game, frameNumber: 3, firstRoll: 4, secondRoll: 7)
        then: "frame is invalid"
        !frame.validate()

        when: "strike in not last frame and second roll is not null"
        frame = new Frame(game: game, frameNumber: 4, firstRoll: 10, secondRoll: 0)
        then: "frame is invalid"
        !frame.validate()

        when: "double strike in last frame"
        frame = new Frame(game: game, frameNumber: 10, firstRoll: 10, secondRoll: 10)
        then: "frame is valid"
        frame.validate()
    }

    void "test validation of bonus frame"() {
        given:
        Game game = new Game().save()

        def lastFrame = new Frame(game: game, frameNumber: 10)
        def bonusFrame = new Frame(game: game, frameNumber: 11)

        when:
        lastFrame.firstRoll = firstRoll
        lastFrame.secondRoll = secondRoll
        bonusFrame.firstRoll = bonusRoll
        lastFrame.save(validate: false)
        bonusFrame.save(validate: false)
        then:
        bonusFrame.validate() == expected

        where:
        firstRoll | secondRoll | bonusRoll | expected
        10        | 10         | 10        | true
        10        | 9          | 1         | true
        10        | 9          | 2         | false
        7         | 3          | 10        | true
    }

    void "test next frame"() {
        when:
        Game game = new Game().save()
        Frame secondFrame = new Frame(game: game, frameNumber: 2, firstRoll: 0).save()
        Frame thirdFrame = new Frame(game: game, frameNumber: 3, firstRoll: 5).save()

        then: "frame with the next frame number is returned"
        secondFrame.nextFrame() == thirdFrame

        then: "return null if frame with the next frame number is absent"
        thirdFrame.nextFrame() == null
    }

    void "test previous frame"() {
        when:
        Game game = new Game().save()
        Frame firstFrame = new Frame(game: game, frameNumber: 1, firstRoll: 0).save()
        Frame secondFrame = new Frame(game: game, frameNumber: 2, firstRoll: 5).save()

        then: "frame with the previous frame number is returned"
        secondFrame.previousFrame() == firstFrame

        then: "return null if frame with the previous frame number is absent"
        firstFrame.previousFrame() == null
    }

    void "test spare"() {
        Frame frame = new Frame(frameNumber: 1, firstRoll: firstRoll, secondRoll: secondRoll)
        expect: "Spare - the remainder of the pins left standing after the first roll are knocked down on the second roll in a frame;"
        expected == frame.isSpare()

        where:
        firstRoll | secondRoll | expected
        10        | 0          | false
        4         | 6          | true
        0         | 10         | true
        3         | 5          | false
    }

    void "test strike"() {
        Frame frame = new Frame(firstRoll: firstRoll, secondRoll: secondRoll)
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
        Frame frame = new Frame(firstRoll: firstRoll, secondRoll: secondRoll)

        then:
        expected == frame.sumRolls()

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
        expected == frame.isNewFrameNeeded()

        where:
        frameNumber | firstRoll | secondRoll | expected
        1           | 10        | null       | true
        1           | 0         | null       | false
        1           | 4         | 6          | true
        10          | 10        | null       | false
        10          | 10        | 10         | true
        10          | 3         | 7          | true
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
        frame.fillScore()

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
        def lastFrame = new Frame(game: game, frameNumber: 10, firstRoll: 10, secondRoll: 7).save()
        def bonusFrame = new Frame(game: game, frameNumber: 11, firstRoll: 2).save()
        ninthFrame.fillScore()

        then: "score equals 10 plus next two rolls (both of them in last frame)"
        ninthFrame.score == 127
    }

    void "test fill score for last frame"() {
        when: "frame with number 9 has score equals 0"
        def game = new Game().save()
        new Frame(game: game, frameNumber: 9, firstRoll: 0, secondRoll: 0, score: 0).save()
        def frame = new Frame(game: game, frameNumber: 10, firstRoll: firstRoll, secondRoll: secondRoll).save()
        if (bonusRoll != null) {
            new Frame(game: game, frameNumber: 11, firstRoll: bonusRoll).save()
        }
        frame.fillScore()

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
