package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(LastFrame)
@Mock([Frame, Game, LastFrameService])
class LastFrameSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test rolls validators"() {
        given:
        Game game = new Game().save()
        def lastFrame = new LastFrame(game: game, frameNumber: 10)

        when:"first or second roll isn't strike"
        lastFrame.firstRoll = firstRoll
        lastFrame.secondRoll = secondRoll
        lastFrame.bonusRoll = bonusRoll

        then:"sum of 1st and 2nd or 2nd and 3rd rolls accordingly must be less than 10"
        lastFrame.validate() == expected

        where:
        firstRoll | secondRoll | bonusRoll | expected
        10        | 10         | 10        | true
        10        | 9          | 1         | true
        10        | 9          | 2         | false
        4         | 7          | null      | false
        7         | 3          | 10        | true
        5         | 3          | 0         | false
        2         | 1          | null      | true
    }

    void "test frame number validator"(){
        given:
        def game = new Game()

        when:"frame number is not 10"
        def frame = new LastFrame(game: game, frameNumber: 9, firstRoll: 10)

        then:"frame is invalid"
        !frame.validate()

        when:"frame number is 10"
        frame = new LastFrame(game: game, frameNumber: 10, firstRoll: 10)

        then:"frame is valid"
        frame.validate()
    }

    void "test get service"(){
        when:"frame is instance of LastFrame class"
        def game = new Game()
        def frame = new LastFrame(game: game, frameNumber: 10, firstRoll: 10)
        def service = frame.getService()

        then:"service is instance of LastFrameService class"
        service && service instanceof LastFrameService
    }
}
