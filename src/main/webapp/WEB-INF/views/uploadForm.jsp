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

    <!-- name 속성을 변수로 지정해서 처리함 -->
    <input type="file" name="uploadFile" muliple>
    <button>Submit</button>

</form>

</body>
</html>
