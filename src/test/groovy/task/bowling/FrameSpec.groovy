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

    void "test get service"(){
        when:"frame is instance of Frame class"
        def game = new Game()
        def frame = new Frame(game: game, frameNumber: 1, firstRoll: 10)
        def service = frame.getService()

        then:"service is instance of FrameService class"
        service && service instanceof FrameService
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
}
