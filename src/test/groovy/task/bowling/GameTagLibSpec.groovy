package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(GameTagLib)
@Mock([Frame, LastFrame, Game, FrameService])
class GameTagLibSpec extends Specification {

    def setup() {
        tagLib.gameService = new GameService()
    }

    def cleanup() {
    }

    void "test playing form"() {
        given:
            def game = new Game().save()

        when:"game in progress"
            def output = tagLib.playingForm(game: game).toString()

        then:"form is displayed"
            output.contains("class=\"playing_form\"")

        when:"game over"
            fillGame(game, 1, 9)
            new LastFrame(game: game, frameNumber: 10, firstRoll: 2, secondRoll: 1).save()
            output = tagLib.playingForm(game: game).toString()

        then:"form isn't displayed"
            !output.contains("class=\"playing_form\"")
        and:"message exist"
            output.contains("class=\"message\"")
    }

    private def fillGame(Game game, int from, int to) {
        for (i in from..to) {
            new Frame(game: game, frameNumber: i, firstRoll: 10).save()
        }
    }
}
