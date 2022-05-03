package hu.mobilalk.turafoglalas.model;

public class Tour
{
    private String id;
    private String name;
    private String location;
    private int length;
    private boolean available;

    private int imageResource;

    public Tour(String name, String location, int length, int imageResource, boolean available)
    {
        this.name = name;
        this.location = location;
        this.length = length;
        this.imageResource = imageResource;
        this.available = available;
    }

    public Tour() { }

    public String _getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }
    public String getLocation()
    {
        return location;
    }
    public int getLength()
    {
        return length;
    }

    public boolean isAvailable() { return available; }

    public int getImageResource()
    {
        return imageResource;
    }
}
