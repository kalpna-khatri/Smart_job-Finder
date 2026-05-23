package model;

public class User {
    private String username;
    private String email;
    private String password;
    private String skills; // comma separated

    public User(String username, String email, String password, String skills) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.skills = skills;
    }

    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getSkills()   { return skills; }

    public String toCSV() {
        return escape(username) + "," + escape(email) + "," +
               escape(password) + "," + escape(skills);
    }

    private String escape(String s) {
        return s.replace(",", ";");
    }

    public static User fromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 4) return null;
        return new User(parts[0], parts[1], parts[2], parts[3]);
    }
}