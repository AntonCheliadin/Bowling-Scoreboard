package task.bowling.data

import grails.validation.Validateable

class RollCommand implements Validateable {

    int knockedDownPins

    static constraints = {
        knockedDownPins min: 0, max: 10
    }
}
