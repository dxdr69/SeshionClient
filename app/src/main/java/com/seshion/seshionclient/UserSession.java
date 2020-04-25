package com.seshion.seshionclient;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UserSession {
    private UUID sessionID;
    private String name;
    private String owner;
    private String description;
    private double latitudeTopLeft;
    private double longitudeTopLeft;
    private double latitudeTopRight;
    private double longitudeTopRight;
    private double latitudeBottomLeft;
    private double longitudeBottomLeft;
    private double latitudeBottomRight;
    private double longitudeBottomRight;
    private LocalDate startDate;
    private String startDateText;
    private LocalDate endDate;
    private String endDateText;
    private LocalTime startTime;
    private String startTimeText;
    private LocalTime endTime;
    private String endTimeText;
    private boolean isSessionPrivate;
    private boolean hasEnded;
    private List<String> invitedUsers;
    private List<String> showedUpUsers;
    private int img;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    UserSession(String name, String owner, String description,
                double latitudeTopLeft, double longitudeTopLeft,
                double latitudeTopRight, double longitudeTopRight,
                double latitudeBottomLeft, double longitudeBottomLeft,
                double latitudeBottomRight, double longitudeBottomRight,
                LocalDate startDate, LocalDate endDate, LocalTime startTime,
                LocalTime endTime, boolean isSessionPrivate,
                List<String> invitedUsers)
    {
        sessionID = UUID.randomUUID();
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.latitudeTopLeft = latitudeTopLeft;
        this.longitudeTopLeft = longitudeTopLeft;
        this.latitudeTopRight = latitudeTopRight;
        this.longitudeTopRight = longitudeTopRight;
        this.latitudeBottomLeft = latitudeBottomLeft;
        this.longitudeBottomLeft = longitudeBottomLeft;
        this.latitudeBottomRight = latitudeBottomRight;
        this.longitudeBottomRight = longitudeBottomRight;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSessionPrivate = isSessionPrivate;
        hasEnded = false;
        this.invitedUsers = invitedUsers;
        showedUpUsers = new ArrayList<String>();

        startDateText = startDate.toString();
        startTimeText = startTime.format(timeFormat);

        if (endDate == null)
        {
            endDateText = null;
        }
        else
        {
            endDateText = endDate.toString();
        }

        if (endTime == null)
        {
            endTimeText = null;
        }
        else
        {
            endTimeText = endTime.format(timeFormat);
        }
    }

    UserSession(String name, String owner, String description,
                double latitudeTopLeft, double longitudeTopLeft,
                double latitudeBottomRight, double longitudeBottomRight,
                LocalDate startDate, LocalDate endDate,
                LocalTime startTime, LocalTime endTime,
                boolean isSessionPrivate, List<String> invitedUsers)
    {
        sessionID = UUID.randomUUID();
        this.name = name;
        this.owner = owner;
        this.description = description;

        this.latitudeTopLeft = latitudeTopLeft;
        this.longitudeTopLeft = longitudeTopLeft;

        this.latitudeTopRight = latitudeTopLeft;
        this.longitudeTopRight = longitudeBottomRight;

        this.latitudeBottomLeft = latitudeBottomRight;
        this.longitudeBottomLeft = longitudeTopLeft;

        this.latitudeBottomRight = latitudeBottomRight;
        this.longitudeBottomRight = longitudeBottomRight;

        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSessionPrivate = isSessionPrivate;
        hasEnded = false;
        this.invitedUsers = invitedUsers;
        showedUpUsers = new ArrayList<String>();

        startDateText = startDate.toString();
        startTimeText = startTime.format(timeFormat);

        if (endDate == null)
        {
            endDateText = null;
        }
        else
        {
            endDateText = endDate.toString();
        }

        if (endTime == null)
        {
            endTimeText = null;
        }
        else
        {
            endTimeText = endTime.format(timeFormat);
        }
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setID(UUID id)
    {
        sessionID = id;
    }

    public UUID getID()
    {
        return sessionID;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public String getName()
    {
        return name;
    }

    public String getOwnerUsername()
    {
        return owner;
    }

    public void setDescription(String desc)
    {
        description = desc;
    }

    public String getDescription()
    {
        return description;
    }

    public void setLatitudeTopLeft(double lat)
    {
        latitudeTopLeft = lat;
    }

    public double getLatitudeTopLeft()
    {
        return latitudeTopLeft;
    }

    public void setLongitudeTopLeft(double lon)
    {
        longitudeTopLeft = lon;
    }

    public double getLongitudeTopLeft()
    {
        return longitudeTopLeft;
    }

    public void setLatitudeTopRight(double lat)
    {
        latitudeTopRight = lat;
    }

    public double getLatitudeTopRight()
    {
        return latitudeTopRight;
    }

    public void setLongitudeTopRight(double lon)
    {
        longitudeTopRight = lon;
    }

    public double getLongitudeTopRight()
    {
        return longitudeTopRight;
    }

    public void setLatitudeBottomLeft(double lat)
    {
        latitudeBottomLeft = lat;
    }

    public double getLatitudeBottomLeft()
    {
        return latitudeBottomLeft;
    }

    public void setLongitudeBottomLeft(double lon)
    {
        longitudeBottomLeft = lon;
    }

    public double getLongitudeBottomLeft()
    {
        return longitudeBottomLeft;
    }

    public void setLatitudeBottomRight(double lat)
    {
        latitudeBottomRight = lat;
    }

    public double getLatitudeBottomRight()
    {
        return latitudeBottomRight;
    }

    public void setLongitudeBottomRight(double lon)
    {
        longitudeBottomRight = lon;
    }

    public double getLongitudeBottomRight()
    {
        return longitudeBottomRight;
    }

    public void setStartDate(LocalDate date)
    {
        startDate = date;
    }

    public LocalDate getStartDate()
    {
        return LocalDate.parse(startDateText);
    }

    public void setEndDate(LocalDate date)
    {
        endDate = date;
    }

    public LocalDate getEndDate()
    {
        if (endDateText != null)
        {
            return LocalDate.parse(endDateText);
        }
        else
        {
            return null;
        }
    }

    public void setStartTime(LocalTime time)
    {
        startTime = time;
    }

    public LocalTime getStartTime()
    {
        return LocalTime.parse(startTimeText, timeFormat);
    }

    public void setEndTime(LocalTime time)
    {
        endTime = time;
    }

    public LocalTime getEndTime()
    {
        if (endTimeText != null)
        {
            return LocalTime.parse(endTimeText, timeFormat);
        }
        else
        {
            return null;
        }
    }

    public void changeSessionVisibility()
    {
        if (isSessionPrivate)
        {
            isSessionPrivate = false;
        }
        else
        {
            isSessionPrivate = true;
        }
    }

    public boolean isSessionPrivate()
    {
        return isSessionPrivate;
    }

    public boolean isSessionOpen()
    {
        return hasEnded;
    }

    public void endSession()
    {
        hasEnded = true;
    }

    public void inviteUser(String username)
    {
        invitedUsers.add(username);
    }

    public void setAllInvitedUsers(List<String> invited)
    {
        invitedUsers = invited;
    }

    public List<String> getInvitedUsers()
    {
        return invitedUsers;
    }

    public void joinUser(String username)
    {
        showedUpUsers.add(username);
    }

    public void setAllShowedUpUsers(List<String> showed)
    {
        showedUpUsers = showed;
    }

    public List<String> getShowedUpUsers()
    {
        return showedUpUsers;
    }

    public String getStartTimeText()
    {
        return startTimeText;
    }

    public String getEndTimeText()
    {
        return endTimeText;
    }

    public String getStartDateText()
    {
        return startDateText;
    }

    public String getEndDateText()
    {
        return endDateText;
    }


}
