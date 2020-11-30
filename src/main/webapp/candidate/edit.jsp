<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="ru.job4j.dream.store.PsqlStore" %>
<%@ page import="ru.job4j.dream.model.Candidate" %>
<%@ page import="ru.job4j.dream.model.Photo" %>
<%@ page import="ru.job4j.dream.store.Store" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
            integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

    <title>Работа мечты</title>
</head>
<body>
<%
        final Store store =  PsqlStore.instOf();
        String id = request.getParameter("id");
        Candidate candidate = new Candidate(0, "");
        if (id != null) {
            candidate = store.findCandidateById(Integer.valueOf(id));
        }

        Photo photo = candidate.getPhoto();
        if(photo != null) {
            photo = store.findPhotoById(photo.getId());
        }
%>
<div class="container pt-3">
    <div class="row">
        <ul class="nav">
            <li class="nav-item">
                <a class="nav-link" href='<c:url value="/posts.do"/>'>Вакансии</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href='<c:url value="/candidates.do"/>'>Кандидаты</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href='<c:url value="/post/edit.jsp"/>'>Добавить вакансию</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href='<c:url value="/candidate/edit.jsp"/>'>Добавить кандидата</a>
             </li>
        <li class="nav-item">
            <a class="nav-link" href="<%=request.getContextPath()%>/login.jsp"> <c:out value="${user.name}"/> | Выйти</a>
        </li>
        </ul>
    </div>

    <div class="row">
        <div class="card" style="width: 100%">
            <div class="card-header">
                   <div class="row">
                            <% if (id == null) { %>
                                Новый кандидат.
                            <% } else { %>
                                Редактирование кандидата.
                            <% } %>
                   </div>
            </div>

            <div class="card-body">
                <form action="<%=request.getContextPath()%>/candidates.do?id=<%=candidate.getId()%>" method="post" enctype="multipart/form-data">
                    <div>
                    <div class="row">
                        <div class="col-lg-4 col-md-5 xs-margin-30px-bottom">
                            <% if(photo == null) { %>
                                <img width="200" height="200" src="${pageContext.request.contextPath}/static/user-logo.png" alt="img-fluid">
                            <% } else { %>
                                <img width="200" height="200" src="${pageContext.request.contextPath}/download?name=<%=photo.getName()%>" alt="img-fluid">
                            <% } %>
                            <input type="file"  id="photo" name="photo" accept="image/*" onchange="onChangeInput()">
                        </div>
                    </div>

                    <div class="col-lg-8 col-md-7">
                        <div class="form-group">
                            <label>Имя</label>
                            <input type="text" class="form-control" name="name" value="<%=candidate.getName()%>">
                        </div>
                        <button id="savebtn" type="submit" class="btn btn-primary">Сохранить</button>
                     </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    const onChangeInput = () => {
        const image = document.querySelector('img');
        const currentSrc = image.getAttribute("src");
        const reader = new FileReader();
        const file    = document.querySelector('input[type=file]').files[0];

        reader.onloadend = function () {
            image.src = reader.result;
        };

        if(file) {
            reader.readAsDataURL(file);
        } else {
            image.src = currentSrc;
        }
    };
</script>
</body>
</html>