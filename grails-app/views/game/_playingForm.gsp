<div class="playing_form">
    <g:form resource="${this.game}" action="roll" method="post">
        <input type="number" name="knockedDownPins" value="10"/>
        <g:submitButton name="roll" value="roll"/>
    </g:form>
</div>