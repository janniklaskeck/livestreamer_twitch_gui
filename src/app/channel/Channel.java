package app.channel;

public interface Channel {

    public void updateChannel();

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public boolean isOnline();

    public void setOnline(boolean isOnline);
}
