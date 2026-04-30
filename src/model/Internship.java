package model;

public class Internship {
    private int id;
    private String title, company, location, description;

    public Internship(int id, String title, String company, String location, String description) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
    }

    public Internship(String title, String company, String location, String description) {
        this(0, title, company, location, description);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }
}
