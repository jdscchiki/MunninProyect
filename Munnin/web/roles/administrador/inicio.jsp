<%-- esto se usa en el top para dar propiedades segun la pagina--%>
<%
    request.setAttribute("title", "Munnin coordinador");
%>
<jsp:include page="/contenido/top1.jsp"></jsp:include>
<jsp:include page="/roles/administrador/contenido/navbarCont.jsp"></jsp:include><%-- cambia dependiendo de la pagina--%>
<jsp:include page="/contenido/top2.jsp"></jsp:include>
<%-- todo el contnido aqui--%>

<jsp:include page="/contenido/bot.jsp"></jsp:include>