<%@ page import="task.bowling.Frame; static task.bowling.Constants.*;" %>

<g:set var="frames" value="${this.game.frames?.sort{a, b -> a.frameNumber <=> b.frameNumber}}"/>
<g:link action="show" id="${this.game.id}">${this.game}</g:link>
<div class="scoreboard">
    <g:each in="${(FIRST_FRAME_NUMBER..LAST_FRAME_NUMBER)}" var="number">
        <div class="scoreboard_frame">
            <g:set var="frame" value="${frames.getAt(number - 1) as Frame}"/>
            <div>${number}</div>

            <div class="knocked_down_pins">
                <frameTag:hitPins frame="${frame}"/>
            </div>

            <div>${frame?.score}</div>
        </div>
    </g:each>
</div>