<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="template" tagdir="/WEB-INF/tags/template" %>
<template:basicTemplate actualPage="2"
                        actualRole="5"
                        funcionario="${sessionScope.usuario}"
                        title="Carga Objeto Instructor Munnin"
                        panelTitle="Cargar un nuevo objeto de aprendizaje">
    <div class="row">
        <div class="col-lg-12">
            <form class="form-horizontal"
                  enctype="multipart/form-data"
                  method="POST"
                  action="${pageContext.request.contextPath}/home/role/instructor/uploadobject/upload">
                <div class="form-group">
                    <label class="control-label col-sm-2" for="objectName">Nombre del archivo</label>
                    <div class="col-sm-10">
                        <input type="text" 
                               class="form-control" 
                               name="objectName" 
                               id="objectName" 
                               placeholder="Nombre del objeto de aprendizaje">
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2" for="objectDescription">Descripción</label>
                    <div class="col-sm-10">
                        <textarea class="form-control" 
                                  name="objectDescription" 
                                  id="objectDescription" 
                                  rows="3"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2" for="objectKeyword">Palabras clave</label>
                    <div class="col-sm-10">
                        <input type="text" 
                               class="form-control" 
                               name="objectKeyword" 
                               id="objectKeyword" 
                               placeholder="Palabras clave del objeto de aprendizaje">
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2" for="objectType">Tipo de objeto</label>
                    <div class="col-sm-3">
                        <select class="form-control" 
                                name="objectType"
                                id="objectType">
                            <c:forEach items="${objectTypes}" var="objectType">
                                <option value="${objectType.getId()}">${objectType.getNombre()}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2" for="objectFile">Archivo</label>
                    <div class="col-sm-10">
                        <input type="file" 
                               name="objectFile" 
                               id="objectFile" 
                               class="form-control" >
                    </div>
                </div>
                <c:if test="${message!= null}">
                    <div class="form-group">
                        <div class="col-lg-offset-2 col-sm-10">
                            <template:message message="${message}" type="${messageType}" />
                        </div>
                    </div>
                </c:if>
                <div class="form-group">
                    <div class="col-lg-offset-2 col-sm-4">
                        <button type="submit" class="btn btn-success">
                            Enviar
                        </button>
                    </div>
                </div>

            </form>
        </div>
    </div>
</template:basicTemplate>