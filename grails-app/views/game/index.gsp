<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'game.label', default: 'Game')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:stylesheet src="bowling/scoreboard.css"/>
</head>

<body>
<a href="#list-game" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                           default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<g:form action="create">
    <fieldset class="buttons">
        <g:submitButton name="create" class="save"
                        value="${message(code: 'task.bowling.button.create.label', args: [entityName])}"/>
    </fieldset>
</g:form>
<div id="list-game" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <g:render template="scoreboard" collection="${gameList}" var="game"/>

    <div class="pagination">
        <g:paginate total="${gameCount ?: 0}"/>
    </div>
</div>
</body>
</html>