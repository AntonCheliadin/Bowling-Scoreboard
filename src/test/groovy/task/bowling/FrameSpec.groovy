package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Frame)
@Mock([Frame, Game, FrameService])
class FrameSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test rolls constraints"() {
        given:
        Game game = new Game().save()
        def frame = new Frame(game: game, frameNumber: 1)

        when:"rolls aren't in range 0..10 or sum of rolls grater than 10"
        frame.firstRoll = firstRoll
        frame.secondRoll = secondRoll

        then:"frame is invalid"
        frame.validate() == expected

        where:
        firstRoll | secondRoll | expected
        1         | 2          | true
        4         | 6          | true
        0         | null       | true
        3         | 8          | false
        10        | 0          | false
        10        | null       | true
        11        | null       | false
        0         | 12         | false
        12        | -2         | false
        -5        | 7          | false
    }

    void "test score constraints"(){
        given:
        Game game = new Game()
        def frame = new Frame(game: game, frameNumber: 1, firstRoll: 10)

        when:"score less than 0"
        frame.score = score

        then:"frame is invalid"
        frame.validate() == expected

        where:
        score | expected
        -1    | false
         null | true
         0    | true
         300  | true
         301  | false
    }

    void "test next frame"() {
        when:
        def game = new Game().save()
        Frame secondFrame = new Frame(game: game, frameNumber: 2, firstRoll: 0).save()
        Frame thirdFrame = new Frame(game: game, frameNumber: 3, firstRoll: 5).save()

        then: "frame with the next frame number is returned"
        secondFrame.nextFrame() == thirdFrame

        then: "return null if frame with the next frame number is absent"
        thirdFrame.nextFrame() == null
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
    }
}
