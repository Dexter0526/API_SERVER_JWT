package com.api.dex.controller;

import com.api.dex.domain.File;
import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.FileDto;
import com.api.dex.service.FileService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;
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

    @Autowired
    private FileService fileService;

    @GetMapping("/{id}")
    public void getFile(HttpServletResponse response, @PathVariable(value = "id") Integer id) throws IOException {
        File file = fileService.getFileById(id);

        if(file.getOriginalName() != null){
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

    }

    @PostMapping("/boards")
    public ResponseEntity insertBoardFile(@RequestParam(value = "id") long boardId, @RequestParam(value = "files") MultipartFile[] multipartFiles, Authentication authentication) throws IOException {
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


}
