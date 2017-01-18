package task.bowling

import grails.test.mixin.TestFor
import spock.lang.Specification
import static task.bowling.FrameTagLib.BONUS_PINS
import static task.bowling.FrameTagLib.HIT_PINS

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(FrameTagLib)
class FrameTagLibSpec extends Specification {

    def frameTagLib

    def setup() {
        frameTagLib = new FrameTagLib()
    }

    def cleanup() {
    }

    void "test knocked down pins view"() {
        when:
        def tagValue = frameTagLib.hitPins(firstRoll: firstRoll, secondRoll: secondRoll, bonusRoll: bonusRoll, isLastFrame: isLastFrame).toString()

        then:
        tagValue == expected
        where:
        firstRoll | secondRoll | bonusRoll | isLastFrame | expected
        10        | 10         | 10        | true        | wrapDiv(BONUS_PINS, ["X", "X", "X"])
        10        | 0          | 10        | true        | wrapDiv(BONUS_PINS, ["X", "-", "/"])
        0         | 10         | 10        | true        | wrapDiv(BONUS_PINS, ["-", "/", "X"])
        10        | 10         | null      | true        | wrapDiv(HIT_PINS, ["X", "X", ""])
        10        | null       | null      | false       | wrapDiv(HIT_PINS, ["X", ""])
        0         | 10         | null      | false       | wrapDiv(HIT_PINS, ["-", "/"])
        4         | 3          | null      | false       | wrapDiv(HIT_PINS, ["4", "3"])
    }

    String wrapDiv(String cssClass, ArrayList list) {
        String result = ""
        list.each {
            result += "<div class=\"" + cssClass + "\">" + it + "</div>"
        }
        result
    }


}
