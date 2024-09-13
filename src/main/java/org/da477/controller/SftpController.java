package org.da477.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.da477.model.RequestJson;
import org.da477.service.SftpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SftpController {

    @Value("{spring.application.name}")
    private String appName;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final SftpService service;

    @Autowired
    public SftpController(SftpService service) {
        this.service = service;
    }

    @PostMapping(value = "/files")
    public ResponseEntity<?> downloadFiles(@RequestBody RequestJson json) throws JSchException, SftpException {
        if (json == null || json.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Map<String, String> mapFiles = service.downloadFiles(json);
        return new ResponseEntity<>(mapFiles, HttpStatus.OK);
    }

    @DeleteMapping(value = "/files")
    public ResponseEntity<?> deleteFiles(@RequestBody RequestJson json) {
        if (json == null || json.isEmpty())
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        int countSuccess = service.deleteFiles(json);
        return new ResponseEntity<>(countSuccess, HttpStatus.OK);
    }

    @PostMapping(value = "/uploadFiles")
    public ResponseEntity<?> uploadFiles(@RequestBody RequestJson json) {
        if (json == null || json.isEmpty())
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        int countSuccess = service.uploadFiles(json);
        return new ResponseEntity<>(countSuccess, HttpStatus.OK);
    }

    @GetMapping("/")
    public String index() {
        return String.format(
                "Hello, I am '%s' and working... %s",
                appName,
                LocalDateTime.now().format(formatter)
        );
    }

}
