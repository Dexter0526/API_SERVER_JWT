package com.api.dex.controller;

import com.api.dex.domain.File;
import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.FileDto;
import com.api.dex.service.FileService;
import com.api.dex.utils.S3;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FileService fileService;

    @Autowired
    private S3 s3;

    @GetMapping("/{id}")
    public void getFile(HttpServletResponse response, @PathVariable(value = "id") Long id) throws IOException {
        File file = fileService.getFileById(id);

        if(file != null){
            String extension = FilenameUtils.getExtension(file.getOriginalName());

            byte[] files = org.apache.commons.io.FileUtils.readFileToByteArray(new java.io.File(file.getPath() + file.getServerName()));

            if(extension.equals("pdf")) response.setContentType("application/pdf");
            else response.setContentType("image/png");

            response.setContentLength(files.length);
            response.setHeader("Content-Disposition", "inline; fileName=\"" + URLEncoder.encode(file.getOriginalName(),"UTF-8")+"\";");
            response.setHeader("Content-Transfer-Encoding", "binary");

            response.getOutputStream().write(files);
            response.getOutputStream().flush();
            response.getOutputStream().close();

        }


//        logger.info("file path:::" + file.getPath() + file.getServerName());
//        InputStream in = InputStream.class.getResourceAsStream("E://temp/files/210812120659marine_logo.png");
//        return IOUtils.toByteArray(in);
//        return org.apache.commons.io.FileUtils.readFileToByteArray(new java.io.File(file.getPath() + file.getServerName()));
    }

    @DeleteMapping("/{id}")
    public void deleteFile(@PathVariable(value = "id") Long id, Authentication authentication){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        fileService.deleteFile(id, securityUser.getMember().getAccount());
    }

    @PostMapping("/boards")
    public ResponseEntity insertBoardFile(@RequestParam(value = "boardId") Long boardId, @RequestParam(value = "files") List<MultipartFile> multipartFiles) throws IOException {
        logger.info("FileController boards:::" + boardId);
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();
        List<FileDto> fileDtoList = fileService.insertFileList(multipartFiles, boardId);

        data.add("files", gson.toJsonTree(fileDtoList));
        data.addProperty("Total_elements", fileDtoList.size());

        items.add("items", data);
        items.addProperty("message", "success!");

        return new ResponseEntity<>(gson.toJson(items), HttpStatus.CREATED);
    }

    @PostMapping("/members")
    public ResponseEntity insertMemberFile(@RequestParam(value = "account") String account, @RequestParam(value = "file") MultipartFile multipartFile, Authentication authentication) throws IOException {
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        if(securityUser.getMember().getAccount().contains(account)){
            FileDto fileDto = fileService.insertFile(multipartFile, securityUser.getMember().getAccount());

            data.add("file", gson.toJsonTree(fileDto));
            items.add("items", data);
            items.addProperty("message", "success!");

            return new ResponseEntity<>(gson.toJson(items), HttpStatus.CREATED);
        }else{
            items.addProperty("message", "FORBIDDEN!");
            return new ResponseEntity<>(gson.toJson(items), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity getMemberFile(@PathVariable(value = "memberId") Integer memberId){
        FileDto fileDto = fileService.getFileByMember(memberId, null, 0);
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();

        data.add("file", gson.toJsonTree(fileDto));
//        data.addProperty("src", PathManagement.src + fileDto.getId());
        data.addProperty("src", s3.getSrc(fileDto.getPath(), fileDto.getServerName()));
        items.add("items", data);
        items.addProperty("message", "success!");

        return new ResponseEntity<>(gson.toJson(items), HttpStatus.OK);
    }

}
