package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Game)
@Mock([Game, Frame])
class GameSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test constraint"(){
        given:
        def game = new Game().save()

        when:"number of frames in game less than 10"
        fillGame(game, 1, 9)

        then:"frames are created successfully"
        game.frames.size() == 9

        when:"number of frames in game equals 10"
        fillGame(game, 10, 13)

        then:"extra frames are not created"
        game.frames.size() == 10
    }

    private def fillGame(Game game, int from, int to) {
        for (i in from..to) {
            new Frame(game: game, frameNumber: i, firstRoll: 10).save()
        }
    }
}
