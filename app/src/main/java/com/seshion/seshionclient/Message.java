package com.seshion.seshionclient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message {
    private UUID messageID;
    private String creator;
    private String recipient;
    private LocalDate dateCreated;
    private LocalTime timeCreated;
    private String messageContent;
    private boolean isPrivateMessage;
    private boolean isGroupMessage;

    Message(String creator, String recipient, LocalDate dateCreated, LocalTime timeCreated, 
    String messageContent, boolean isPrivateMessage, boolean isGroupMessage)
    {
        messageID = UUID.randomUUID();
        this.creator = creator;
        this.recipient = recipient;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.messageContent = messageContent;
        this.isPrivateMessage = isPrivateMessage;
        this.isGroupMessage = isGroupMessage;
    }

    public void setID(UUID id)
    {
        messageID = id;
    }

    public UUID getID()
    {
        return messageID;
    }

    public String getMessage()
    {
        return messageContent;
    }

    public boolean isPrivateMessage()
    {
        return isPrivateMessage;
    }

    public String getCreatorUsername()
    {
        return creator;
    }

    public String getRecipientUserName()
    {
        return recipient;
    }

    public LocalDate getCreationDate()
    {
        return dateCreated;
    }

    public LocalTime getCreationTime()
    {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timeCreatedText = timeFormat.format(timeCreated);
        return LocalTime.parse(timeCreatedText, timeFormat);
    }

    public boolean isGroupMessage()
    {
        return isGroupMessage;
    }
}