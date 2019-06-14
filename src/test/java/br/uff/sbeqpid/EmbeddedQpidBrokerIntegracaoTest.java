package br.uff.sbeqpid;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.function.Supplier;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class EmbeddedQpidBrokerIntegracaoTest {

    @Autowired
    private EmbeddedQpidBroker embeddedQpidBroker;

    @Test
    public void test() {

    }


}