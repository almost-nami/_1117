<%--
  Created by IntelliJ IDEA.
  User: nami
  Date: 2022/09/20
  Time: 11:33 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@include file="../includes/header.jsp"%>

    <style>
        .uploadResult {
            width: 100%;
            background-color: #EBF3FB;
        }

        .uploadResult ul {
            display: flex;
            flex-flow: row;
            justify-content: center;
            align-items: center;
        }

        .uploadResult ul li {
            list-style: none;
            padding: 10px;
            align-content: center;
            text-align: center;
        }

        .uploadResult ul li img {
            width: 100px;
        }

        .uploadResult ul li span {
            color: gray;
        }

        .bigPictureWrapper {
            position: absolute;
            display: none;
            justify-content: center;
            align-items: center;
            top: 0%;
            width: 100%;
            height: 100%;
            background-color: gray;
            z-index: 100;
            background: rgba(255, 255, 255, 0.5);
        }

        .bigPicture {
            position: relative;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .bigPicture img {
            width: 600px;
        }
    </style>

    <div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">Board Register</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
    <!-- /.row -->

    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">Board Register</div>
                <!-- /.panel-heading -->
                <div class="panel-body">
                    <form role="form" action="/board/register" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="form-group">
                            <label>Title</label> <input class="form-control" name='title'>
                        </div>
                        <div class="form-group">
                            <label>Text area</label>
                            <textarea class="form-control" rows="3" name='content'></textarea>
                        </div>
                        <div class="form-group">
                            <label>Writer</label> <input class="form-control" name='writer'
                                value='<sec:authentication property="principal.username"/>' readonly="readonly">
                        </div>
                        <button type="submit" class="btn btn-default">Submit Button</button>
                        <button type="reset" class="btn btn-default">Reset Button</button>
                    </form>
                </div>
                <!-- end panel-body -->
            </div>
            <!-- end panel-body -->
        </div>
        <!-- end panel -->
    </div>
    <!-- /.row -->

    <!-- 첨부파일처리 -->
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">

                <div class="panel-heading">File Attach</div>
                <!-- /.panel-heading -->
                <div class="panel-body">
                    <div class="form-group uploadDiv">
                        <input type="file" name='uploadFile' multiple>
                    </div>

                    <div class='uploadResult'>
                        <ul>

                        </ul>
                    </div>

                </div>
                <!-- end panel-body -->

            </div>
            <!-- end panel-body -->
        </div>
        <!-- end panel -->
    </div>
    <!-- /.row -->

<script>
    $(document).ready(function (e){

        var formObj = $("form[role='form']");

        $("button[type='submit']").on("click", function (e){

            e.preventDefault();

            console.log("submit clicked");

            var str = "";

            $(".uploadResult ul li").each(function (i, obj){
                var jobj = $(obj);

                console.dir(jobj);

                str += "<input type='hidden' name='attachList["+ i +"].fileName' value='"+ jobj.data("filename") +"'>";
                str += "<input type='hidden' name='attachList["+ i +"].uuid' value='"+ jobj.data("uuid") +"'>";
                str += "<input type='hidden' name='attachList["+ i +"].uploadPath' value='"+ jobj.data("path") +"'>";
                str += "<input type='hidden' name='attachList["+ i +"].fileType' value='"+ jobj.data("type") +"'>";

            });

            formObj.append(str).submit();

        });

        var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");

        var maxSize = 5242880;  // 5MB

        function checkExtension(fileName, fileSize){
            if(fileSize >= maxSize) {
                alert("파일 사이즈 초과");

                return false;
            }

            if(regex.test(fileName)) {
                alert("해당 종류의 파일은 업로드할 수 없습니다.");

                return false;
            }

            return  true;
        }

        // 업로드된 결과를 화면에 섬네일 등을 만들어서 처리하는 부분
        function showUploadResult(uploadResultArr) {
            if (!uploadResultArr || uploadResultArr.length == 0) {
                return;
            }

            var uploadUL = $(".uploadResult ul");

            var str = "";

            $(uploadResultArr).each(function (i, obj) {

                // image type
                if (obj.image) {
                    var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);

                    str += "<li data-path='" + obj.uploadPath + "'";
                    str += " data-uuid='" + obj.uuid + "' data-filename='" + obj.fileName + "' data-type='" + obj.image + "'";
                    str += "><div>";
                    str += "<span> " + obj.fileName + "</span>";
                    str += "<button type='button' data-file=\'" + fileCallPath + "\' data-type='image' class='btn btn-warning btn-circle'><i class='fa fa-times'></i></button><br>";
                    str += "<img src='/display?fileName=" + fileCallPath + "'>";
                    str += "</div>";
                    str += "</li>";
                } else {
                    var fileCallPath = encodeURIComponent(obj.uploadPath + "/" + obj.uuid + "_" + obj.fileName);

                    var fileLink = fileCallPath.replace(new RegExp(/\\/g), "/");

                    str += "<li data-path='" + obj.uploadPath + "'";
                    str += " data-uuid='" + obj.uuid + "' data-filename='" + obj.fileName + "' data-type='" + obj.image + "'";
                    str += "><div>";
                    str += "<span> " + obj.fileName + "</span>";
                    str += "<button type='button' data-file=\'" + fileCallPath + "\' data-type='file' class='btn btn-warning btn-circle'><i class='fa fa-times'></i></button><br>";
                    str += "<img src='/resources/img/attach.png'>";
                    str += "</div>";
                    str += "</li>";
                }

            });

            uploadUL.append(str);
        }

        // X 아이콘 클릭시 서버에서 파일 삭제
        $(".uploadResult").on("click", "button", function (e){
            console.log("delete file");

            var targetFile = $(this).data("file");

            var type = $(this).data("type");

            // closest 가장 가까운 부모 호출
            var targetLi = $(this).closest("li");

            $.ajax({
                url: '/deleteFile',
                data: {fileName: targetFile, type: type},
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(csrfHeaderName, csrfTokenValue);
                },
                dataType: 'text',
                type: 'POST',
                    success: function (result){
                        alert(result);

                        targetLi.remove();
                    }
            });
        });

        var csrfHeaderName = "${_csrf.headerName}";
        var csrfTokenValue = "${_csrf.token}";

        // 파일 업로드는 <input type='file'>의 내용이 변경되는 것을 감지해서 처리
        $("input[type='file']").change(function (e){
            var formData = new FormData();

            var inputFile = $("input[name='uploadFile']");

            var files = inputFile[0].files;

            for(var i=0; i<files.length; i++) {
                if(!checkExtension(files[i].name, files[i].size)) {
                    return false;
                }
                formData.append("uploadFile", files[i]);
            }

            $.ajax({
                url: '/uploadAjaxAction',
                processData: false,
                contentType: false,
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(csrfHeaderName, csrfTokenValue);
                },
                data: formData,
                type: 'POST',
                dataType: 'json',
                    success: function (result){
                        console.log(result);

                        // 업로드 결과 처리 함수
                        showUploadResult(result);
                    }
            });
        });

    });
</script>

<%@include file="../includes/footer.jsp"%>
