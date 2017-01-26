package task.bowling

class GameTagLib {
    static defaultEncodeAs = [taglib:'html']
    static encodeAsForTags = [playingForm: [taglib: 'none']]

    static namespace = "gameTag"

    def gameService

    def playingForm = { attrs, body ->
        def game = attrs.game
        if (gameService.isGameOver(game)){
            out << "<div class=\"message\" role=\"status\">${message(code: 'task.bowling.game.over.message')}</div>"
        } else {
            out << render(template: "/game/playingForm", model: [game: game])
        }
    }
}
