package task.bowling

import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
class GameController {

    static allowedMethods = [save: "POST", delete: "DELETE"]

    public static final Integer MAX_PIN = 10
    public static final Integer MIN_PIN = 0
    public static final Integer FIRST_FRAME_NUMBER = 1
    public static final Integer LAST_FRAME_NUMBER = 10
    public static final Integer BONUS_FRAME_NUMBER = 11

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Game.list(params), model: [gameCount: Game.count()]
    }

    def show(Game game) {
        respond game
    }

    @Transactional
    def save(Game game) {
        if (game == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (game.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond game.errors, view: 'create'
            return
        }

        game.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'game.label', default: 'Game'), game.id])
                redirect game
            }
            '*' { respond game, [status: CREATED] }
        }
    }

    @Transactional
    def delete(Game game) {

        if (game == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        game.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'game.label', default: 'Game'), game.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    @Transactional
    def roll(Game game) {
        if (game == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        Integer knockedDownPins = params.int('knockedDownPins')
        if (game.isGameOver() || knockedDownPins == null) {
            flash.message = message(code: 'task.bowling.game.reject.roll.message')
        } else {
            Frame frame = game.createOrUpdateLastFrame(knockedDownPins)

            if (!frame || !frame.validate()) {
                flash.message = message(code: 'task.bowling.game.invalid.pins.message')
                transactionStatus.setRollbackOnly()
            } else {
                frame.fillScore()
            }
        }
        redirect(action: "show", id: game.getId())
    }
}
