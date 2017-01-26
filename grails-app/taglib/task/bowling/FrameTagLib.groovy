package task.bowling

import static task.bowling.Constants.*

class FrameTagLib {
    static defaultEncodeAs = [taglib: 'html']
    static encodeAsForTags = [hitPins: [taglib: 'none']]

    static namespace = "frameTag"

    def hitPins = { attrs, body ->
        Frame frame = attrs['frame']
        if (frame)
            calculateView(frame)
    }

    private def calculateView(Frame frame) {
        wrapDiv(HIT_PINS, calculateFirstRoll(frame.firstRoll))
        wrapDiv(HIT_PINS, calculateSecondRoll(frame.firstRoll, frame.secondRoll))
    }

    private def calculateView(LastFrame frame) {
        String cssClass = frame.bonusRoll ? BONUS_PINS : HIT_PINS

        wrapDiv(cssClass, calculateFirstRoll(frame.firstRoll))

        if (frame.getService().isStrike(frame)) {
            wrapDiv(cssClass, calculateFirstRoll(frame.secondRoll))
            wrapDiv(cssClass, calculateBonusRoll(frame))
        } else {
            wrapDiv(cssClass, calculateSecondRoll(frame.firstRoll, frame.secondRoll))
            wrapDiv(cssClass, calculateFirstRoll(frame.bonusRoll))
        }
    }

    private def wrapDiv = { cssClass, value ->
        out << "<div class=\"" + cssClass + "\">" + value + "</div>"
    }

    private def calculateCommonRoll = { roll -> roll == MIN_PIN ? "-" : roll ?: "" }

    private def calculateFirstRoll = { firstRoll -> firstRoll == MAX_PIN ? "X" : calculateCommonRoll(firstRoll)}

    private def calculateSecondRoll = { firstRoll, secondRoll -> secondRoll && firstRoll + secondRoll == MAX_PIN ? "/" : calculateCommonRoll(secondRoll)}

    private def calculateBonusRoll = { frame -> frame.secondRoll == MAX_PIN && frame.bonusRoll == MAX_PIN ? "X" :
            calculateSecondRoll(frame.secondRoll, frame.bonusRoll)}
}