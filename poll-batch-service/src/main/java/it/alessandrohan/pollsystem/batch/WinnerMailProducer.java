package it.alessandrohan.pollsystem.batch;

import it.alessandrohan.pollsystem.messaging.WinnerMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WinnerMailProducer {

    private final String queueName;
    private final RabbitTemplate rabbitTemplate;

    public WinnerMailProducer(
            @Value("${app.queue.poll-winner-mail}") String queueName,
            RabbitTemplate rabbitTemplate
    ) {
        this.queueName = queueName;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(WinnerMessage msg) {
        rabbitTemplate.convertAndSend(queueName, msg);
    }
}
