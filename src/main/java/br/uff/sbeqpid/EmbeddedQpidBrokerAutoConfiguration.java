package br.uff.sbeqpid;

import com.google.common.io.Files;
import org.apache.qpid.server.SystemLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.function.Supplier;

import static br.uff.sbeqpid.EmbeddedQpidBroker.STR_FILE_CONFIG_NAME;

/**
 * This configuration create and start the qpid-broker.
 * The post <a href="https://novotnyr.github.io/scrolls/qpid-as-mocking-amqp-broker-for-integration-tests/">mocking-amqp-broker</a>
 * was used to develop it
 */
@Configuration
//@AutoConfigureBefore({JmsAutoConfiguration.class})
//@AutoConfigureAfter({JndiConnectionFactoryAutoConfiguration.class})
//@EnableConfigurationProperties({ActiveMQProperties.class, JmsProperties.class})
//@Import({ActiveMQXAConnectionFactoryConfiguration.class, ActiveMQConnectionFactoryConfiguration.class})
@ConditionalOnClass({SystemLauncher.class})
@ConditionalOnMissingBean({EmbeddedQpidBroker.class})
public class EmbeddedQpidBrokerAutoConfiguration {

    @Bean
    @Qualifier("fileConfigConfiguration")
    public Supplier<File> embbededQpidBrokerPropertiessupplierDirectoryConfiguration(){
        return ()->Files.createTempDir();
    }
    @Bean
    @ConfigurationProperties(prefix = "embbeded-qpid")
    @ConditionalOnMissingBean({EmbeddedQpidBrokerProperties .class})
    public EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties(){
        return new EmbeddedQpidBrokerProperties();
    }

    @Bean
    @ConditionalOnMissingBean({EmbeddedQpidBroker.class})
    public EmbeddedQpidBroker embeddedQpidBroker(EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties,
                                                 @Value(value = "classpath:/"+STR_FILE_CONFIG_NAME) Resource qpidConfigResource){
        return new EmbeddedQpidBroker(embeddedQpidBrokerProperties, qpidConfigResource);
    }
}
