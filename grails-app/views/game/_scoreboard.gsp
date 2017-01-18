<%@ page import="task.bowling.Frame; static task.bowling.GameController.BONUS_FRAME_NUMBER; static task.bowling.GameController.FIRST_FRAME_NUMBER; static task.bowling.GameController.LAST_FRAME_NUMBER;" %>

<g:set var="frames" value="${this.game.frames?.sort { a, b -> a.frameNumber <=> b.frameNumber }}"/>
<g:set var="bonusFrame" value="${frames?.getAt(BONUS_FRAME_NUMBER - 1)}"/>

<g:link action="show" id="${this.game.id}">${this.game}</g:link>
<div class="scoreboard">
    <g:each in="${(FIRST_FRAME_NUMBER..LAST_FRAME_NUMBER)}" var="number">
        <div class="scoreboard_frame">
            <g:set var="frame" value="${frames.getAt(number - 1) as Frame}"/>
            <div>${number}</div>

            <div class="knocked_down_pins">
                <frameTag:hitPins firstRoll="${frame?.firstRoll}"
                                  secondRoll="${frame?.secondRoll}"
                                  bonusRoll="${bonusFrame?.firstRoll}"
                                  isLastFrame="${number == LAST_FRAME_NUMBER}"/>
            </div>

            <div>${frame?.score}</div>
        </div>
    </g:each>
</div>