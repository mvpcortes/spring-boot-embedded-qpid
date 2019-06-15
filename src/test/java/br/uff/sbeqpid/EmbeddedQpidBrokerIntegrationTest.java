package br.uff.sbeqpid;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class EmbeddedQpidBrokerIntegrationTest {

    @Autowired
    private EmbeddedQpidBroker embeddedQpidBroker;

    @Test
    public void verify_if_context_is_started() {
        //verify
    }


}