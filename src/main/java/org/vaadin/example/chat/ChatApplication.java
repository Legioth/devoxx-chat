package org.vaadin.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

/**
 * This is the main Spring Boot application. Vaadin views are automatically
 * bound using the <code>@Route</code> annotation.
 *
 * @see org.vaadin.example.chat.ui.ChatView
 *
 */
@SpringBootApplication
public class ChatApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChatApplication.class, args);
  }

  /**
   * 
   * @param messageDistributor
   *          the messageDistributor
   * @return a Flux where clients can subscribe to receive ChatMessages
   */
  @Bean
  Flux<ChatMessage> chatMessages(UnicastProcessor<ChatMessage> messageDistributor) {
    return messageDistributor.replay(20).autoConnect();
  }

  /**
   * @return UnicastProcessor to be used for distributing new chat messages to
   *         all participants.
   */
  @Bean
  UnicastProcessor<ChatMessage> messageDistributor() {
    return UnicastProcessor.create();
  }

}
