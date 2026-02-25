package it.alessandrohan.pollsystem.batch;

import it.alessandrohan.pollsystem.messaging.WinnerMessage;
import org.springframework.stereotype.Component;

@Component
public class WinnerMailProducer {

    public void send(WinnerMessage msg) {
        // TODO implement RabbitMQ publish
    }
}
