package task.bowling

import static task.bowling.GameController.MAX_PIN
import static task.bowling.GameController.MIN_PIN

class FrameTagLib {
    static defaultEncodeAs = [taglib: 'html']
    static encodeAsForTags = [hitPins: [taglib: 'none']]

    static namespace = "frameTag"

    public static final String BONUS_PINS = "bonus_pins"
    public static final String HIT_PINS = "pins"

    def hitPins = { attrs, body ->
        Integer firstRoll = attrs['firstRoll']
        Integer secondRoll = attrs['secondRoll']
        Integer bonusRoll = attrs['bonusRoll']
        boolean isLastFrame = attrs['isLastFrame']

        if (!isLastFrame) {
            calculateUsuallyCase(firstRoll, secondRoll)
        } else {
            calculateLastFrame(firstRoll, secondRoll, bonusRoll)
        }
    }

    private def calculateUsuallyCase(Integer firstRoll, Integer secondRoll) {
        wrapDiv(HIT_PINS, calculateFirstRoll(firstRoll))
        wrapDiv(HIT_PINS, calculateSecondRoll(firstRoll, secondRoll))
    }

    private def calculateLastFrame(Integer firstRoll, Integer secondRoll, Integer bonusRoll) {
        String cssClass = bonusRoll ? BONUS_PINS : HIT_PINS

        wrapDiv(cssClass, calculateFirstRoll(firstRoll))

        if (firstRoll == MAX_PIN) {
            wrapDiv(cssClass, calculateFirstRoll(secondRoll))
            wrapDiv(cssClass, bonusRoll == MIN_PIN ? "-" : secondRoll == MAX_PIN && bonusRoll == MAX_PIN ? "X" :
                    isSpare(secondRoll, bonusRoll) ? "/" : bonusRoll ?: "")
        } else {
            wrapDiv(cssClass, calculateSecondRoll(firstRoll, secondRoll))
            wrapDiv(cssClass, calculateFirstRoll(bonusRoll))
        }
    }

    private def wrapDiv = { cssClass, value ->
        out << "<div class=\"" + cssClass + "\">" + value + "</div>"
    }

    private def isSpare = { firstRoll, secondRoll ->
        int fRoll = firstRoll ?: MIN_PIN
        int sRoll = secondRoll ?: MIN_PIN
        fRoll + sRoll == MAX_PIN && fRoll != MAX_PIN
    }

    private def calculateFirstRoll = { roll -> roll == MIN_PIN ? "-" : roll == MAX_PIN ? "X" : roll ?: "" }

    private def calculateSecondRoll = { firstRoll, secondRoll ->
        secondRoll == MIN_PIN ? "-" : isSpare(firstRoll, secondRoll) ? "/" : secondRoll ?: ""
    }
}