package task.bowling

import static task.bowling.Constants.*

class LastFrame extends Frame {
    Integer bonusRoll

    static constraints = {
        frameNumber(validator: { val -> val == LAST_FRAME_NUMBER })
        secondRoll(validator: { val, obj ->
            if (obj.firstRoll != MAX_PIN && val != null) {
                obj.firstRoll + val <= MAX_PIN
            } else {
                true
            }
        })
        bonusRoll(range: 0..10, nullable: true, validator: { val, obj ->
            if (val && obj.firstRoll == MAX_PIN && obj.secondRoll != MAX_PIN) {
                obj.secondRoll + val <= MAX_PIN
            } else if (obj.firstRoll + (obj.secondRoll ?: MIN_PIN) < MAX_PIN) {
                val == null
            }
        })
    }

    def sumRolls() {
        firstRoll + (secondRoll ?: MIN_PIN) + (bonusRoll ?: MIN_PIN)
    }

    def isNewFrameNeeded() {
        false
    }

    @Override
    public String toString() {
        return "LastFrame{" +
                "id=" + id +
                ", game=" + game +
                ", frameNumber=" + frameNumber +
                ", firstRoll=" + firstRoll +
                ", secondRoll=" + secondRoll +
                ", bonusRoll=" + bonusRoll +
                ", score=" + score +
                '}';
    }
}
