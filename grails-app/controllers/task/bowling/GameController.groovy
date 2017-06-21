package task.bowling

import static org.springframework.http.HttpStatus.*

class GameController {
    def gameService

    static allowedMethods = [save: "POST", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Game.list(params), model: [gameCount: Game.count()]
    }

    def show(Game game) {
        respond game
    }

    def save(Game game) {
        if (game == null) {
            notFound()
            return
        }

        if (game.hasErrors()) {
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

    def delete(Game game) {
        if (game == null) {
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

    def roll(Game game) {
        if (game == null) {
            notFound()
            return
        }
        Integer knockedDownPins = params.int('knockedDownPins')
        def result = gameService.executeRoll(game, knockedDownPins)
        if (result?.error) {
            flash.message = message(code: result.error)
        }
        redirect(action: "show", id: game.getId())
    }
}
