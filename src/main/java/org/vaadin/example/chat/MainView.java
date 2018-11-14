package org.vaadin.example.chat;

import org.github.legioth.accessor.Accessor;
import org.vaadin.marcus.shortcut.Shortcut;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Push
@Route("")
public class MainView extends VerticalLayout {
  VerticalLayout messages = new VerticalLayout();
  private String name;

  public MainView(UnicastProcessor<ChatMessage> messageDistributor,
      Flux<ChatMessage> chatMessages) {
    TextField nameField = new TextField("Name");
    Button joinButton = new Button("Join");

    Dialog dialog = new Dialog(new VerticalLayout(nameField, joinButton));
    dialog.open();

    TextField input = new TextField();
    Button send = new Button("Send");

    add(new H1("Websocket chat"), messages, new HorizontalLayout(input, send));
    
    joinButton.addClickListener(click -> {
      dialog.close();
      
      name = nameField.getValue();
      
      input.focus();
    });
    
    Shortcut.add(nameField, Key.ENTER, joinButton::click);

    send.addClickListener(click -> {
      ChatMessage message = new ChatMessage(name, input.getValue());

      input.setValue("");
      input.focus();

      messageDistributor.onNext(message);
    });
    
    Shortcut.add(input, Key.ENTER, send::click);

    Accessor.ofConsumer(this::addMessage).withSubscriberAndUnsubscriber(
        chatMessages::subscribe, Disposable::dispose).bind(this);
  }

  private void addMessage(ChatMessage message) {
    messages
        .add(new Paragraph(message.getFrom() + ": " + message.getMessage()));
  }
}
