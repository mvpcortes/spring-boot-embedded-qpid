package br.uff.sbeqpid;

import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.model.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class EmbeddedQpidBroker {

    public static final String STR_FILE_CONFIG_NAME = "qpid-config.json";

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedQpidBroker.class);

    private final EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties;

    //https://stackoverflow.com/a/39472514/8313595
    private final Resource qpidConfigResource;

    private final SystemLauncher systemLauncher;

    private boolean isStarted = false;


    public EmbeddedQpidBroker(EmbeddedQpidBrokerProperties properties, Resource qpidConfigResource){
        this(properties, qpidConfigResource, new SystemLauncher());

    }
    public EmbeddedQpidBroker(EmbeddedQpidBrokerProperties properties, Resource qpidConfigResource, SystemLauncher systemLauncher){
        this.qpidConfigResource = qpidConfigResource;
        this.embeddedQpidBrokerProperties = properties;
        this.systemLauncher = systemLauncher;
    }

    @PostConstruct
    public synchronized void start() {
        if(isStarted()) {
            logger.warn("Broker already started. It will destroy and create again.");
            shutdown();
        }

        try {
            systemLauncher.startup(createSystemConfig());
            logger.info("Broker successfully started");
            isStarted = true;
        }catch(Exception e){
            throw new IllegalStateException("Cannot start systemLauncher", e);
        }
    }

    @PreDestroy
    public synchronized void shutdown() {
        if(isStarted()) {
            this.systemLauncher.shutdown();
            this.isStarted = false;
        }else{
            logger.warn("Broker already stoped. It will do nothing.");
        }
    }

    private Map<String, Object> createSystemConfig() {
        final Map<String, Object> attributes = new HashMap<>();

        attributes.put(SystemConfig.TYPE, "Memory");
        //@see https://gitbox.apache.org/repos/asf?p=qpid-broker-j.git;a=blob;f=broker/src/main/java/org/apache/qpid/server/Main.java;h=407eea2eaff06513169bbdc71e202c1d35835e71;hb=HEAD#l259
        //isto vai pegar as propriedades e colocar para o broker usar
        attributes.put(SystemConfig.CONTEXT, embeddedQpidBrokerProperties.toMap());
        attributes.put(SystemConfig.INITIAL_CONFIGURATION_LOCATION, getFileConfig());
        attributes.put(SystemConfig.STARTUP_LOGGED_TO_SYSTEM_OUT, false);
        return attributes;
    }

    private String getFileConfig() {
        try {
            return this.qpidConfigResource.getURL().toExternalForm();
        }catch(IOException uoe){
            throw new IllegalStateException("Fail to get configuration", uoe);
        }
    }

    private boolean isStarted(){
        return isStarted;
    }

    /**
     * the sync method should be used to avoid concurrency problems using the queue;
     * @param func the function in sync
     * @param <T> type returned
     * @return returned value generated by func, in a sync context.
     */
    public <T>  T syncExcecution(Supplier<T> func){
        synchronized (this){
            return func.get();
        }
    }
}
