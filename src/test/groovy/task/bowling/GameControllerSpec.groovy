package task.bowling

import grails.test.mixin.*
import spock.lang.*

@TestFor(GameController)
@Mock([Game, Frame])
class GameControllerSpec extends Specification {

    void "Test the index action returns the correct model"() {

        when: "The index action is executed"
        controller.index()

        then: "The model is correct"
        !model.gameList
        model.gameCount == 0
    }

    void "Test the save action correctly persists an instance"() {
        when: "The save action is executed"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def game = new Game()
        game.validate()
        controller.save(game)

        then: "A redirect is issued to the show action"
        response.redirectedUrl == '/game/show/1'
        controller.flash.message != null
        Game.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when: "The show action is executed with a null domain"
        controller.show(null)

        then: "A 404 error is returned"
        response.status == 404

        when: "A domain instance is passed to the show action"
        def game = new Game()
        controller.show(game)

        then: "A model is populated containing the domain instance"
        model.game == game
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when: "The delete action is called for a null instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(null)

        then: "A 404 is returned"
        response.redirectedUrl == '/game/index'
        flash.message != null

        when: "A domain instance is created"
        response.reset()
        def game = new Game().save(flush: true)

        then: "It exists"
        Game.count() == 1

        when: "The domain instance is passed to the delete action"
        controller.delete(game)

        then: "The instance is deleted"
        Game.count() == 0
        response.redirectedUrl == '/game/index'
        flash.message != null
    }

    void "test that the roll action redirect correct url"() {
        when: "The roll action is executed with a null domain"
        controller.roll(null)

        then: "A 404 error is returned"
        response.status == 404

        when: "The roll action is executed"
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        request.setParameter('knockedDownPins', "1")
        def game = new Game().save()
        controller.roll(game)

        then: "A redirect is issued to the show action"
        response.redirectedUrl == '/game/show/1'
        controller.flash.message == null

        when: "The roll action is executed without knocked down pins parameter"
        response.reset()
        game = new Game().save()
        controller.roll(game)

        then: "A redirect is issued to the show action and flash.message is exist"
        response.redirectedUrl == '/game/show/2'
    }


    void "test that the roll action processes knocked down pins correctly"() {
        when: "knocked down pins in first roll less than 10"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def game = new Game().save()
        request.addParameter('knockedDownPins', "5")
        controller.roll(game)
        def biggestFrame = game.getBiggestFrame()

        then: "frame is created with frame number = 1 and score is empty"
        biggestFrame.frameNumber == 1
        biggestFrame.firstRoll == 5
        biggestFrame.secondRoll == null
        biggestFrame.score == null
    }

}
