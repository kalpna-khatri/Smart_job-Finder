package model;


import java.util.Arrays;
import java.util.List;

public class Job {
    private String title;
    private String company;
    private String location;
    private String salary;
    private String requiredSkills; // comma separated

    public Job(String title, String company, String location,
               String salary, String requiredSkills) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.requiredSkills = requiredSkills;
    }

    public String getTitle()          { return title; }
    public String getCompany()        { return company; }
    public String getLocation()       { return location; }
    public String getSalary()         { return salary; }
    public String getRequiredSkills() { return requiredSkills; }

    /** Simple skill-match percentage logic */
    public int matchPercentage(String userSkills) {
        if (userSkills == null || userSkills.isEmpty()) return 0;

        List<String> reqList = Arrays.asList(requiredSkills.toLowerCase().split("\\s*,\\s*"));
        List<String> userList = Arrays.asList(userSkills.toLowerCase().split("\\s*,\\s*"));

        if (reqList.isEmpty()) return 0;

        int matches = 0;
        for (String req : reqList) {
            for (String us : userList) {
                if (req.equalsIgnoreCase(us) || req.contains(us) || us.contains(req)) {
                    matches++;
                    break;
                }
            }
        }
        return (int) ((matches * 100.0) / reqList.size());
    }

    public String toCSV() {
        return title + "|" + company + "|" + location + "|" + salary + "|" + requiredSkills;
    }

    public static Job fromCSV(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return null;
        return new Job(p[0], p[1], p[2], p[3], p[4]);
    }
}