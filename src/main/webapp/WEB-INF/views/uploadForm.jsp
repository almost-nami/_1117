<%--
  Created by IntelliJ IDEA.
  User: nami
  Date: 2022/09/26
  Time: 1:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload Form</title>
</head>
<body>

<form action="uploadFormAction" method="post" enctype="multipart/form-data">

    <!--
        multiple 속성을 이용하면 하나읜 <input>태그에서 여러 개의 파일을 업로드할 수 있음
        name 속성을 변수로 지정해서 처리함
    -->
    <input type="file" name="uploadFile" muliple>
    <button>Submit</button>

</form>

</body>
</html>
