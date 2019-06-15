package br.uff.sbeqpid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class EmbeddedQpidBrokerProperties {


    public static class Auth{

        /**
         * username used by authentication.
         */
        private String username="guest";

        /**
         * password used by authentication. empty will generate random password
         */
        private String password="";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private Auth auth = new Auth();
    /**
     * Port used by qpid.
     */
    private int port = 5672;

    private @JsonIgnore JavaPropsMapper propMapper = new JavaPropsMapper();

    public void setPropMapper(JavaPropsMapper propMapper) {
        this.propMapper = propMapper;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Map<String, String> toMap() {

        try {
            Properties properties= propMapper.writeValueAsProperties(this);
            return properties.stringPropertyNames()
                    .stream()
                    .collect(Collectors.toMap(name -> "qpid."+name, properties::getProperty, (a, b) -> b));

        }catch (Exception e){
            throw new IllegalStateException("Cannot serialize " + getClass().getSimpleName(), e);
        }
    }
}
