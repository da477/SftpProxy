package org.da477.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class RequestJson {
    private String server;
    private int port;
    private String user;
    private String password;
    private String directory;
    private String filesExtension;
    private Map<String, String> fileNames;

    @JsonIgnore
    public boolean isEmpty() {
        return this.getServer() == null || this.getServer().equals("")
                || this.getUser() == null || this.getUser().equals("")
                || this.getPassword() == null || this.getPassword().equals("")
                ;
    }
}
