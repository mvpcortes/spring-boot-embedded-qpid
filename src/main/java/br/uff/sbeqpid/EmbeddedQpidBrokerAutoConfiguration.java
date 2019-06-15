package br.uff.sbeqpid;

import org.apache.qpid.server.SystemLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import static br.uff.sbeqpid.EmbeddedQpidBroker.STR_FILE_CONFIG_NAME;

/**
 * This configuration create and start the qpid-broker.
 * The post <a href="https://novotnyr.github.io/scrolls/qpid-as-mocking-amqp-broker-for-integration-tests/">mocking-amqp-broker</a>
 * was used to develop it
 */
@Configuration
@ConditionalOnClass({SystemLauncher.class})
@ConditionalOnMissingBean({EmbeddedQpidBroker.class})
@AutoConfigureBefore(RabbitAutoConfiguration.class)
public class EmbeddedQpidBrokerAutoConfiguration {

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
