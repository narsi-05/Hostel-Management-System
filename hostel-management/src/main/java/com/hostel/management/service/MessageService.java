package com.hostel.management.service;

import com.hostel.management.model.*;
import com.hostel.management.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(User sender, User receiver, String content, Hostel hostel) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setHostel(hostel);
        return messageRepository.save(message);
    }

    public List<Message> getConversation(User user1, User user2) {
        List<Message> messages = new ArrayList<>();
        messages.addAll(messageRepository.findBySenderAndReceiverOrderBySentAtAsc(user1, user2));
        messages.addAll(messageRepository.findBySenderAndReceiverOrderBySentAtAsc(user2, user1));
        messages.sort(Comparator.comparing(Message::getSentAt));
        return messages;
    }

    public List<Message> getAllMessagesForUser(User user) {
        return messageRepository.findBySenderOrReceiverOrderBySentAtDesc(user, user);
    }

    public long countUnreadMessages(User user) {
        return messageRepository.countByReceiverAndIsRead(user, false);
    }

    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(msg -> {
            msg.setIsRead(true);
            messageRepository.save(msg);
        });
    }

    /**
     * Marks all messages received by this user as read.
     * Called when the user opens their Messages page, so the unread
     * notification count on the dashboard clears after being seen.
     */
    public void markAllAsReadForUser(User user) {
        List<Message> unread = messageRepository.findByReceiverAndIsRead(user, false);
        for (Message msg : unread) {
            msg.setIsRead(true);
        }
        messageRepository.saveAll(unread);
    }
}
