package org.da477.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.da477.model.RequestJson;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

@Service
@Slf4j
public class SftpService {
    private final String LOCALDIR = System.getProperty("java.io.tmpdir");

    public Map<String, String> downloadFiles(RequestJson requestJson) throws JSchException, SftpException {
        log.info("IN SftpService.index(json) {}", requestJson.toString());
        Map<String, String> mapFiles = getFilesToMap(requestJson);
        log.info("return without null mapFiles.size():{}", mapFiles.size());
        return mapFiles;
    }

    private Map<String, String> getFilesToMap(RequestJson json) {
        Map<String, String> mapFiles = new HashMap<>();
        Session session = null;
        ChannelSftp channel = null;

        try {
            session = openSession(json);
            channel = openChannel(json, session);

            Vector remoteFileList = channel.ls(json.getDirectory());
            for (int i = 0; i < remoteFileList.size(); i++) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) remoteFileList.get(i);

                final String remoteFileName = entry.getFilename();
//                    if (remoteFileName.equals(".") || remoteFileName.equals("..")) {
//                        continue;
                if (remoteFileName.contains(json.getFilesExtension())) {
                    String localFileName = LOCALDIR + remoteFileName;
                    channel.get(remoteFileName, localFileName);
                    log.info("Save to {}", localFileName);
                    File localFile = new File(localFileName);
                    mapFiles.put(remoteFileName, encodeFileToBase64(localFile));
                    localFile.delete();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) session.disconnect();
            if (channel != null) channel.disconnect();
        }
        return mapFiles;
    }

    private ChannelSftp openChannel(RequestJson json, Session session) throws JSchException, SftpException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        channel.cd(json.getDirectory());
        return channel;
    }

    private Session openSession(RequestJson json) throws JSchException {

        JSch jsch = new JSch();
        Session session = jsch.getSession(json.getUser(), json.getServer(), json.getPort());
        session.setPassword(json.getPassword());

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
        log.info("Connection to server established.");

        return session;
    }

    private static String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    public int deleteFiles(RequestJson json) {
        int countSuccess = 0;
        Set<String> filesName = json.getFileNames().keySet();
        Session session = null;
        ChannelSftp channel = null;

        try {
            session = openSession(json);
            channel = openChannel(json, session);

            for (String remoteFileName : filesName) {
                channel.rm(remoteFileName);
                Boolean success = true;
                if (success) {
                    log.info("File has been DELETED successfully: {}", remoteFileName);
                    countSuccess++;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) session.disconnect();
            if (channel != null) channel.disconnect();
        }
        return countSuccess;
    }

    public int uploadFiles(RequestJson json) {
        int countSuccess = 0;
        Map<String, String> files = json.getFileNames();
        String remoteDir = json.getDirectory() + "/"; // "remote_sftp_test/"

        Session session = null;
        ChannelSftp channel = null;

        try {
            session = openSession(json);
            channel = openChannel(json, session);

            for (String remoteFileName : files.keySet()) {
                String base64String = files.get(remoteFileName);

                byte[] decodedBytes = Base64.getDecoder().decode(base64String);
                InputStream is = new ByteArrayInputStream(decodedBytes);

                channel.put(is, remoteDir + remoteFileName);

                Boolean success = true;
                if (success) {
                    log.info("File has been UPLOADED successfully: {}", remoteFileName);
                    countSuccess++;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) session.disconnect();
            if (channel != null) channel.disconnect();
        }
        return countSuccess;
    }

}
