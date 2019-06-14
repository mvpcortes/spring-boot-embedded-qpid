package br.uff.sbeqpid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            if(password == null || password.trim().isEmpty()){
                   password = generateRandomPassword();
            }
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String generateRandomPassword() {
            Random random = new SecureRandom();
            IntStream specialChars = random.ints(124, 33, 45);
            return specialChars.mapToObj(data -> String.valueOf((char) data)).collect(Collectors.joining(",", "[", "]"));
        }
    }

    private Auth auth = new Auth();
    /**
     * Port used by qpid.
     */
    private int port = 5672;

    private @JsonIgnore JavaPropsMapper propMapper = new JavaPropsMapper();

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
            throw new IllegalStateException("Cannot serialize json", e);
        }
    }
}
