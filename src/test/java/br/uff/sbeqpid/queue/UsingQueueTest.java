package br.uff.sbeqpid.queue;

import br.uff.sbeqpid.EmbeddedQpidBroker;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest()
public class UsingQueueTest {

    public static final String QUEUE_NAME = "queue_for_test";

    public static final String EXCHANGE_NAME = "exchange_for_test";

    public static final String ROUTE_KEY_NAME = "test.route.#";

    /**
     * @see {https://spring.io/guides/gs/messaging-rabbitmq/}
     */
    @TestConfiguration
    @ComponentScan(basePackageClasses = QueueListener.class)
    public static class QueueConfiguration{
        @Bean
        Queue queue() {
            return new Queue(QUEUE_NAME, false);
        }

        @Bean
        TopicExchange exchange() {
            return new TopicExchange(EXCHANGE_NAME);
        }

        @Bean
        Binding binding(Queue queue, TopicExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with(ROUTE_KEY_NAME);
        }

    }


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private QueueListener queueListener;

    @Autowired
    private EmbeddedQpidBroker embeddedQpidBroker;

    @After
    public void after(){
        queueListener.clearMessage();
    }

    @Test
    public void test_send_data_to_queue(){

        MyMessage myMessage = embeddedQpidBroker.syncExcecution(()->{
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTE_KEY_NAME, new MyMessage("XUXU", 1L));

            Awaitility.await()
                    .until(queueListener::hasMessage);
            return queueListener.getMessage();
        });

        assertThat(myMessage.getName()).isEqualTo("XUXU");
        assertThat(myMessage.getId()).isEqualTo(1L);
    }
}
