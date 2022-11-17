package org.zerock.controller;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Log4j
public class UploadController {

    @GetMapping("/uploadForm")
    public void uploadForm() {

        log.info("upload form");
    }

    /*
        파일처리 : MultipartFile
            -> 첨부파일을 여러 개 선택할 수 있으므로 배열타입으로 설정
        MultipartFile의 메소드
            -> getName() : 파라미터의 이름, <input>태그의 이름
            -> getOriginalFileName() : 업로드되는 파일의 이름
            -> isEmpty() : 파일이 존재하지 않는 경우 true
            -> getSize() : 업로드되는 파일의 크기
            -> getBytes() : byte[]로 파일 데이터 변환
            -> getInputStream() : 파일데이터와 연결된 InputStream을 반환
            -> transferTo(File file) : 파일의 저장
     */
    @PostMapping("/uploadFormAction")
    public void uploadFormPost(MultipartFile[] uploadFile, Model model){

        String uploadFolder = "/Users/nami/Documents/spring/upload";

        for(MultipartFile multipartFile : uploadFile) {

            log.info("-------------------------");
            log.info("Upload File Name : " + multipartFile.getOriginalFilename());
            log.info("Upload File Size : " + multipartFile.getSize());

            File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());

            try {
                multipartFile.transferTo(saveFile);
            } catch(Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @GetMapping("/uploadAjax")
    public void uploadAjax() {

        log.info("upload ajax");
    }

    // 현재 시간을 이용해서 폴더경로 만들기
    private String getFolder() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();

        String str = sdf.format(date);

        return str.replace("-", File.separator);
    }

    // 파일이 이미지 파일인지 확인
    private boolean checkImageType(File file) {
        try {
            String contentType = Files.probeContentType(file.toPath());

            return contentType.startsWith("image");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
    @PostMapping("/uploadAjaxAction")
    public void uploadAjaxPost(MultipartFile[] uploadFile) {

        String uploadFolder = "/Users/nami/Documents/spring/upload";

        // make folder
        File uploadPath = new File(uploadFolder, getFolder());
        log.info("upload path : " + uploadPath);

        // getFolder()의 경로가 있는지 검사
        if(uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }
        // make yyyy/MM/dd folder

        for(MultipartFile multipartFile : uploadFile) {

            log.info("-------------------------");
            log.info("Upload File Name : " + multipartFile.getOriginalFilename());
            log.info("Upload File Size : " + multipartFile.getSize());

            String uploadFileName = multipartFile.getOriginalFilename();

            // IE has file path : IE의 경우 전체 파일 경로가 전송되므로 마지막 \를 기준으로 잘라낸 문자열이 실제 파일 이름
            uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);

            log.info("only file name : " + uploadFileName);

            UUID uuid = UUID.randomUUID();

            // 원래의 파일 이름을 구분할 수 있도록 중간에 '_'를 추가
            uploadFileName = uuid.toString() + "_" + uploadFileName;

            // File saveFile = new File(uploadFolder, uploadFileName);
            // File saveFile = new File(uploadPath, uploadFileName);

            try {
                File saveFile = new File(uploadPath, uploadFileName);
                multipartFile.transferTo(saveFile);

                // check image type file : 이미지 파일일 경우 섬네일 생성
                if(checkImageType(saveFile)) {
                    FileOutputStream thumbnail
                            = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));

                    // Thumbnailator는 InputStream과 java.io.File 객체를 이용해서 파일을 생성
                    Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);

                    thumbnail.close();
                }
            } catch(Exception e) {
                log.error(e.getMessage());
            }
        }
    }
     */

    // AttachFileDTO의 리스트를 반환하는 구조
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {

        List<AttachFileDTO> list = new ArrayList<>();
        String uploadFolder = "/Users/nami/Documents/spring/upload";

        String uploadFolderPath = getFolder();
        // make folder
        File uploadPath = new File(uploadFolder, uploadFolderPath);

        if(uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }
        // make yyyy/MM/dd folder

        for(MultipartFile multipartFile : uploadFile) {

            AttachFileDTO attachDTO = new AttachFileDTO();

            String uploadFileName = multipartFile.getOriginalFilename();

            // IE has file path
            uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);

            log.info("only file name : " + uploadFileName);

            attachDTO.setFileName(uploadFileName);

            UUID uuid = UUID.randomUUID();

            uploadFileName = uuid.toString() + "_" + uploadFileName;

            try {
                File saveFile = new File(uploadPath, uploadFileName);
                multipartFile.transferTo(saveFile);

                attachDTO.setUuid(uuid.toString());
                attachDTO.setUploadPath(uploadFolderPath);

                // check image type file
                if(checkImageType(saveFile)) {
                    attachDTO.setImage(true);

                    FileOutputStream thumbnail
                            = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));

                    Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);

                    thumbnail.close();
                }

                // add to List
                list.add(attachDTO);

            } catch(Exception e) {
                log.error(e.getMessage());
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/display")
    @ResponseBody
    public ResponseEntity<byte[]> getFile(String fileName) {
        log.info("fileName : " + fileName);

        File file = new File("/Users/nami/Documents/spring/upload/" + fileName);

        log.info("file : " + file);

        ResponseEntity<byte[]> result = null;

        try {
            HttpHeaders header = new HttpHeaders();
            /*
                브라우저에서 보내주는 MIME 타입이 파일종류에 따라 달라짐
                -> probeContentType()을 이용해서 적절한 MIME 타입 데이터를 Http 헤더에 포함
             */
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<>(
                     FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    // 다운로드가 가능한 MIME타입 application/octet-stream
    // HttpServletRequest에 포함된 헤더중 User-Agent를 이용하면 브라우저의 종류, 모바일/데스크톱, 프라우저 프로그램 종류 확인가능
    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFile(@RequestHeader("User-Agent") String userAgent, String fileName) {

        Resource resource = new FileSystemResource("/Users/nami/Documents/spring/upload/" + fileName);

        if(resource.exists() == false) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String resourceName = resource.getFilename();

        //Remove UUID
        String resourceOriginalName = resourceName.substring(resourceName.indexOf("_") + 1);

        // 다운로드 시 파일의 이름을 처리함
        HttpHeaders headers = new HttpHeaders();

        try {
            String downloadName = null;

            if(userAgent.contains("Trident")) {
                log.info("IE browser");

                downloadName = URLEncoder.encode(resourceOriginalName, "UTF-8").replaceAll("\\+", " ");

            } else if(userAgent.contains("Edge")) {
                log.info("Edge browser");

                downloadName = URLEncoder.encode(resourceOriginalName, "UTF-8");

                log.info("Edge namd : " + downloadName);
            } else {
                log.info("Chrome browser");

                downloadName = new String(resourceOriginalName.getBytes("UTF-8"), "ISO-8859-1");
            }

            // 다운로드 시 저장되는 이름 'Content-Disposition'을 이용해 지정
            headers.add("Content-Disposition",
                    "attachment; filename=" + downloadName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

    // 서버에서 첨부파일 삭제
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deleteFile")
    @ResponseBody
    public ResponseEntity<String> deleteFile(String fileName, String type) {
        log.info("deleteFile : " + fileName);

        File file;

        try {
            file = new File("/Users/nami/Documents/spring/upload/" + URLDecoder.decode(fileName, "UTF-8"));

            file.delete();

            // 이미지 파일인 경우 섬네일 파일도 삭제
            if(type.equals("image")) {
                String largeFileName = file.getAbsolutePath().replace("s_", "");

                log.info("largeFileName : " + largeFileName);

                file = new File(largeFileName);

                file.delete();
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }
}
