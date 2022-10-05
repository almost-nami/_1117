<%--
  Created by IntelliJ IDEA.
  User: nami
  Date: 2022/09/26
  Time: 2:15 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload Ajax</title>
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
            color: white;
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
</head>
  <script src="https://code.jquery.com/jquery-3.3.1.min.js"
          integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
          crossorigin="anonymous"></script>
<body>
<h1>Upload with Ajax</h1>

<div class="uploadDiv">
  <input type="file" name="uploadFile" multiple>
</div>

<div class="bigPictureWrapper">
    <div class="bigPicture">

    </div>
</div>

<div class="uploadResult">
    <ul>

    </ul>
</div>
<button id="uploadBtn">Upload</button>

<script>
    // 원본 이미지 보여주기
    function showImage(fileCallPath) {
        // alert(fileCallPath);

        $(".bigPictureWrapper").css("display", "flex").show();

        // animate() 지정된 시간동안 화면에서 열리는 효과
        $(".bigPicture").html("<img src='/display?fileName=" + encodeURI(fileCallPath) + "'>")
            .animate({width: '100%', height: '100%'}, 1000);

        /*
            setTimeout()에 적용된 '=>(ES6의 화살표 함수)'는 chrome에서는 정상작동하지만
            IE11에서는 제대로 동작하지 않으므로 변경이 필요함
                setTimeout(function(){
                    $('.bigPictureWrapper').hide();
                }, 1000);
         */
        $(".bigPictureWrapper").on("click", function (e){
            $(".bigPicture").animate({width: '0%', height: '0%'}, 1000);
            setTimeout(() => {
                $(this).hide();
            }, 1000);
        });

    }

    $(document).ready(function (){
        // 첨부파일의 확장자가 exe, sh, zip, alz인 파일의 업로드 제한
        var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");
        var maxSize = 5242880;  // 5MB

        function checkExtension(fileName, fileSize) {
            if(fileSize >= maxSize) {
                alert("파일 사이즈 초과");
                return false;
            }

            // test() : 문자열에 일치하는 부분이 있는지 확인
            if(regex.test(fileName)) {
                alert("해당 종류의 파일은 업로드할 수 없습니다.");
                return false;
            }

            return true;
        }
        // <input type='file'>은 readonly여서 내용을 수정할 수 없음
        // 내용이 없는 <input type='file'>를 복사한 뒤에 첩부파일을 업로드 후에 복사된 객체를 다시 추가
        var cloneObj = $(".uploadDiv").clone();

        $("#uploadBtn").on("click", function (e){
            // formData는 가상의 <form>태그
            var formData = new FormData();

            var inputFile = $("input[name='uploadFile']");

            var files = inputFile[0].files;

            console.log(files);

            // add File Data to formData
            for(var i=0; i<files.length; i++) {

                // 확장자와 파일크기 검사
                if(!checkExtension(files[i].name, files[i].size)) {
                    return false;
                }

                // fileData를 formData에 추가
                formData.append("uploadFile", files[i]);
            }

            $.ajax({
                url: '/uploadAjaxAction',
                processData: false,
                contentType: false,
                data: formData,
                type: 'POST',
                dataType: 'json',
                // 정상적으로 응답을 받은 경우 success 콜백이 호출됨
                // success 콜백 함수의 파라미터 응답바디, 응답코드, XHR헤드
                success: function (result) {

                  console.log(result);

                  // 첨부파일 목록을 보여주는 부분
                  showUploadedFile(result);

                  $(".uploadDiv").html(cloneObj.html());
                }
            });

        });

        var uploadResult = $(".uploadResult ul");

        // 첨부 파일 삭제
        $(".uploadResult").on("click", "span", function (e){
            var targetFile = $(this).data("file");
            var type = $(this).data("type");

            console.log(targetFile);

            $.ajax({
                url: '/deleteFile',
                data: {fileName: targetFile, type:type},
                dataType: 'text',
                type: 'POST',
                    success: function (result){
                        alert(result);
                    }
            });
        });

        // 첨부파일 목록을 보여주는 부분
        function showUploadedFile(uploadResultArr){

            var str = "";

            $(uploadResultArr).each(function (i, obj){
                if(!obj.image) {

                    // encodeURIComponent() : 파일 이름에 포함된 공백 문자나 한글 이름 처리
                    var fileCallPath = encodeURIComponent(obj.uploadPath + "/" + obj.uuid + "_" + obj.fileName);

                    var fileLink = fileCallPath.replace(new RegExp(/\\/g), "/");

                    str += "<li><div><a href='/download?fileName="+fileCallPath+"'><img src='/resources/img/attach.png'>"
                        + obj.fileName + "</a><span data-file=\'"+fileCallPath+"\' data-type='file'> x </span></div></li>";
                } else {
                    // str += "<li>" + obj.fileName + "</li>";

                    var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);

                    var originPath = obj.uploadPath + "\\" + obj.uuid + "_" + obj.fileName;

                    // /\\/g 전체에서 \를 찾음
                    originPath = originPath.replace(new RegExp(/\\/g), "/");

                    // 섬네일 클릭시 showImage() 호출 -> javascript:showImage()
                    str += "<li><a href=\"javascript:showImage(\'"+originPath+"\')\"><img src='/display?fileName="
                        + fileCallPath + "'></a><span data-file=\'"+fileCallPath+"\' data-type='image'> x </span></li>";
                }
            });

            uploadResult.append(str);
        }
    });
  </script>
</body>
</html>
