<div>
    <g:if test="${!this.game.isGameOver()}">
        <g:form resource="${this.game}" action="roll" method="post">
            <input type="number" name="knockedDownPins" value="10"/>
            <g:submitButton name="roll" value="roll"/>
        </g:form>
    </g:if>
    <g:else>
        <div class="message" role="status">${message(code: 'task.bowling.game_over.message')}</div>
    </g:else>
</div>