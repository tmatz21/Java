package model;

public class Application {
    private int id;
    private int userId;
    private int internshipId;
    private String status;
    private String cvPath;

    public Application(int userId, int internshipId, String cvPath) {
        this.userId = userId;
        this.internshipId = internshipId;
        this.cvPath = cvPath;
        this.status = "Pending";
    }

    public int getUserId() { return userId; }
    public int getInternshipId() { return internshipId; }
    public String getCvPath() { return cvPath; }
}