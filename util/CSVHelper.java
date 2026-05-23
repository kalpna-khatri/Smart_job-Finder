package util;

import model.User;
import model.Job;

import java.io.*;
import java.util.*;

public class CSVHelper {
    private static final String USERS_FILE = "data/users.csv";
    private static final String SAVED_FILE = "data/saved_jobs.csv";
    private static final String APPLICATIONS_FILE = "data/applications.csv";

    static {
        new File("data").mkdirs();
        ensureFile(USERS_FILE);
        ensureFile(SAVED_FILE);
        ensureFile(APPLICATIONS_FILE);
    }

    private static void ensureFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            try { f.createNewFile(); } catch (IOException ignored) {}
        }
    }

    // ----- USERS -----
    public static void saveUser(User u) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(u.toCSV());
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                User u = User.fromCSV(line);
                if (u != null) list.add(u);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public static User authenticate(String username, String password) {
        for (User u : loadUsers()) {
            if (u.getUsername().equalsIgnoreCase(username) &&
                u.getPassword().equals(password)) return u;
        }
        return null;
    }

    public static boolean userExists(String username) {
        for (User u : loadUsers())
            if (u.getUsername().equalsIgnoreCase(username)) return true;
        return false;
    }

    // ----- SAVED JOBS -----
    public static void saveJob(String username, Job job) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SAVED_FILE, true))) {
            bw.write(username + "::" + job.toCSV());
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<Job> loadSavedJobs(String username) {
        List<Job> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SAVED_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("::", 2);
                if (parts.length == 2 && parts[0].equalsIgnoreCase(username)) {
                    Job j = Job.fromCSV(parts[1]);
                    if (j != null) list.add(j);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean isJobSaved(String username, Job job) {
        for (Job j : loadSavedJobs(username))
            if (j.getTitle().equals(job.getTitle()) &&
                j.getCompany().equals(job.getCompany())) return true;
        return false;
    }

    // ----- APPLICATIONS -----

    private static String applicationKey(String username, Job job) {
        return username.toLowerCase() + "~~" + job.getTitle().toLowerCase() + "~~" + job.getCompany().toLowerCase();
    }

    public static boolean hasApplied(String username, Job job) {
        return loadApplication(username, job) != null;
    }

    public static void saveApplication(String username, Job job, Map<String, String> data) {
        String key = applicationKey(username, job);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPLICATIONS_FILE, true))) {
            StringBuilder sb = new StringBuilder(key);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                sb.append("||").append(escapeField(entry.getKey()))
                  .append("=").append(escapeField(entry.getValue()));
            }
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static Map<String, String> loadApplication(String username, Job job) {
        String key = applicationKey(username, job);
        try (BufferedReader br = new BufferedReader(new FileReader(APPLICATIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|\\|");
                if (parts.length >= 1 && parts[0].equals(key)) {
                    Map<String, String> data = new LinkedHashMap<>();
                    for (int i = 1; i < parts.length; i++) {
                        int eq = parts[i].indexOf('=');
                        if (eq >= 0) {
                            String k = unescapeField(parts[i].substring(0, eq));
                            String v = unescapeField(parts[i].substring(eq + 1));
                            data.put(k, v);
                        }
                    }
                    return data;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static void updateApplication(String username, Job job, Map<String, String> data) {
        String key = applicationKey(username, job);
        File file = new File(APPLICATIONS_FILE);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|\\|");
                if (parts.length >= 1 && parts[0].equals(key)) {
                    StringBuilder sb = new StringBuilder(key);
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        sb.append("||").append(escapeField(entry.getKey()))
                          .append("=").append(escapeField(entry.getValue()));
                    }
                    lines.add(sb.toString());
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) { e.printStackTrace(); return; }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String l : lines) { bw.write(l); bw.newLine(); }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static String escapeField(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("||", "\\|\\|").replace("=", "\\=").replace("\n", "\\n").replace("\r", "");
    }

    private static String unescapeField(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\=", "=").replace("\\|\\|", "||").replace("\\\\", "\\");
    }
}
