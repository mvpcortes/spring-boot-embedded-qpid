package br.uff.sbeqpid.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class QueueListener {

    private MyMessage lastMessage;

    @RabbitListener(queues = {UsingQueueTest.QUEUE_NAME})
    public synchronized void accept(@Payload MyMessage mm){
        lastMessage = mm;
    }

    public synchronized void clearMessage(){
        lastMessage = null;
    }

    public synchronized MyMessage getMessage() {
        return lastMessage;
    }

    public synchronized boolean hasMessage() {
        return lastMessage != null;
    }
}
