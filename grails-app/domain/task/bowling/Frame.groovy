package task.bowling

import static task.bowling.Constants.MAX_PIN
import static task.bowling.Constants.MIN_PIN

class Frame {

    Integer frameNumber
    Integer firstRoll
    Integer secondRoll
    Integer score
    static belongsTo = [game: Game]

    static constraints = {
        frameNumber(range: 1..10)
        firstRoll(range: 0..10)
        secondRoll(range: 0..10, nullable: true, validator: { val, obj ->
            if (obj.firstRoll == MAX_PIN) {
                val == null
            } else {
                obj.firstRoll + (val ?: MIN_PIN) <= MAX_PIN
            }
        })
        score(nullable: true, min: 0, max: 300)
        game(nullable: false)
    }

    static mapping = {
        game indexColumn: [name: "the_game", type: Game]
    }

    def nextFrame() {
        Frame.findByGameAndFrameNumber(game, frameNumber + 1)
    }

    def previousFrame() {
        Frame.findByGameAndFrameNumber(game, frameNumber - 1)
    }

    def isSpare() {
        sumRolls() == MAX_PIN && !isStrike()
    }

    def isStrike() {
        firstRoll == MAX_PIN
    }

    def sumRolls() {
        firstRoll + (secondRoll ?: MIN_PIN)
    }

    def isNewFrameNeeded() {
        secondRoll != null || isStrike()
    }

    @Override
    public String toString() {
        return "Frame{" +
                "id=" + id +
                ", game=" + game +
                ", frameNumber=" + frameNumber +
                ", firstRoll=" + firstRoll +
                ", secondRoll=" + secondRoll +
                ", score=" + score +
                '}';
    }
}