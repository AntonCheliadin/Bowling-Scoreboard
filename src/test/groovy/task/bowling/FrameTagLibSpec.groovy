package task.bowling

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

import static task.bowling.Constants.*

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(FrameTagLib)
@Mock([FrameService])
class FrameTagLibSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test knocked down pins view"() {
        when:
        def game = new Game()
        def frame = new Frame(game: game, frameNumber: 1, firstRoll: firstRoll, secondRoll: secondRoll)
        def tagValue = tagLib.hitPins(frame: frame).toString()

        then:
        tagValue == expected

        where:
        firstRoll | secondRoll | expected
        10        | null       | wrapDiv(HIT_PINS, ["X", ""])
        0         | 10         | wrapDiv(HIT_PINS, ["-", "/"])
        5         | 5          | wrapDiv(HIT_PINS, ["5", "/"])
        4         | 3          | wrapDiv(HIT_PINS, ["4", "3"])
    }

    void "test knocked down pins view for last frame"() {
        when:
        def game = new Game()
        def frame = new LastFrame(game: game, frameNumber: 1, firstRoll: firstRoll, secondRoll: secondRoll, bonusRoll: bonusRoll)
        def tagValue = tagLib.hitPins(frame: frame).toString()

        then:
        tagValue == expected

        where:
        firstRoll | secondRoll | bonusRoll | expected
        10        | 10         | 10        | wrapDiv(BONUS_PINS, ["X", "X", "X"])
        10        | 0          | 10        | wrapDiv(BONUS_PINS, ["X", "-", "/"])
        0         | 10         | 10        | wrapDiv(BONUS_PINS, ["-", "/", "X"])
        10        | 10         | null      | wrapDiv(HIT_PINS, ["X", "X", ""])
        10        | null       | null      | wrapDiv(HIT_PINS, ["X", "", ""])
        0         | 10         | null      | wrapDiv(HIT_PINS, ["-", "/", ""])
        4         | 3          | null      | wrapDiv(HIT_PINS, ["4", "3", ""])
    }

    def wrapDiv(String cssClass, ArrayList list) {
        String result = ""
        list.each {
            result += "<div class=\"" + cssClass + "\">" + it + "</div>"
        }
        result
    }


}
