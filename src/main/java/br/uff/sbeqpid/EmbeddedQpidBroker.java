package br.uff.sbeqpid;

import com.google.common.io.Files;
import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.configuration.IllegalConfigurationException;
import org.apache.qpid.server.model.SystemConfig;
import org.apache.qpid.server.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class EmbeddedQpidBroker {

    public static final String STR_FILE_CONFIG_NAME = "qpid-config.json";

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedQpidBroker.class);

    private final EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties;

    private final SystemLauncher systemLauncher;

    private File tempDirectory = null;

    //https://stackoverflow.com/a/39472514/8313595

    private final Resource qpidConfigResource;



    public EmbeddedQpidBroker(EmbeddedQpidBrokerProperties properties, Resource qpidConfigResource){
        this(properties, qpidConfigResource, new SystemLauncher());
    }

    public EmbeddedQpidBroker(EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties, Resource qpidConfigResource, SystemLauncher systemLauncher) {
        this.qpidConfigResource = qpidConfigResource;
        this.embeddedQpidBrokerProperties = embeddedQpidBrokerProperties;
        this.systemLauncher = systemLauncher;
    }

    @PostConstruct
    public void start() {
        if(!isStarted()) {
            logger.warn("Broker already started. It will destroy and create again.");
            shutdown();
        }

        tempDirectory = Files.createTempDir();

        tempDirectory.deleteOnExit();
        try {
            systemLauncher.startup(createSystemConfig());
            logger.info("Broker iniciado com sucesso");
        }catch(Exception e){
            logger.error("Falha ao iniciar broker", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if(isStarted()) {
            this.systemLauncher.shutdown();
            destroyTempDir();
        }else{
            logger.warn("Broker already stoped. It will do nothing.");
        }
    }

    private Map<String, Object> createSystemConfig() throws IllegalConfigurationException {
        final Map<String, Object> attributes = new HashMap<>();

        final File fileConfig = createFileConfig();

        attributes.put(SystemConfig.TYPE, "Memory");
        //@see https://gitbox.apache.org/repos/asf?p=qpid-broker-j.git;a=blob;f=broker/src/main/java/org/apache/qpid/server/Main.java;h=407eea2eaff06513169bbdc71e202c1d35835e71;hb=HEAD#l259
        //isto vai pegar as propriedades e colocar para o broker usar
        attributes.put(SystemConfig.CONTEXT, embeddedQpidBrokerProperties.toMap());
        attributes.put(SystemConfig.INITIAL_CONFIGURATION_LOCATION, fileConfig.getAbsolutePath());
        attributes.put(SystemConfig.STARTUP_LOGGED_TO_SYSTEM_OUT, false);
        return attributes;
    }

    private File createFileConfig() {
        try {
            File fileConfig = new File(tempDirectory.getAbsolutePath(), STR_FILE_CONFIG_NAME);
            fileConfig.createNewFile();
            fileConfig.deleteOnExit();
            FileUtils.copy(qpidConfigResource.getFile(), fileConfig);
            return fileConfig;
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível copiar o arquivo de config", e);
        }
    }


    private void destroyTempDir() {
        if(tempDirectory != null) {
            try {
                FileUtils.delete(this.tempDirectory, true);
            } catch (Exception e) {
                logger.warn("Não foi possível destruir o diretório temporário", e);
            }
            tempDirectory = null;
        }
    }

    private boolean isStarted(){
        return tempDirectory != null;
    }
}
