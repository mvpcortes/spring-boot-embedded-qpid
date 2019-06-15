package br.uff.sbeqpid;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties="embedded-qpid.enabled=false")
public class EmbeddedQpidBrokerIntegrationWithBrokerDisabledTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void verify_if_with_broker_disabled_the_bean_will_not_loaded() {

        Assertions.assertThatThrownBy(()-> {
            applicationContext.getBean(EmbeddedQpidBroker.class);
        })
        .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
    }


}