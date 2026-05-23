package util;

import model.Job;
import java.util.ArrayList;
import java.util.List;

public class JobDatabase {
    public static List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job("Junior Java Developer", "TechNova Inc.", "Remote",
                "$55,000 - $70,000", "Java, OOP, Swing, MySQL"));
        jobs.add(new Job("Frontend Engineer", "PixelForge",  "New York, USA",
                "$80,000 - $95,000", "HTML, CSS, JavaScript, React"));
        jobs.add(new Job("Backend Developer", "CloudCore", "Berlin, Germany",
                "$90,000 - $110,000", "Java, Spring, REST, PostgreSQL"));
        jobs.add(new Job("Data Analyst", "InsightWorks", "London, UK",
                "$70,000 - $85,000", "Python, SQL, Excel, PowerBI"));
        jobs.add(new Job("Mobile App Developer", "AppVerse", "Remote",
                "$75,000 - $100,000", "Java, Kotlin, Android, Firebase"));
        jobs.add(new Job("Full Stack Developer", "NextGen Labs", "Toronto, Canada",
                "$95,000 - $120,000", "Java, React, Node, MongoDB"));
        jobs.add(new Job("DevOps Engineer", "ScaleOps", "Remote",
                "$100,000 - $130,000", "Docker, Kubernetes, AWS, Linux"));
        jobs.add(new Job("UI/UX Designer", "DesignHive", "San Francisco, USA",
                "$70,000 - $90,000", "Figma, Photoshop, UI, UX"));
        jobs.add(new Job("Machine Learning Engineer", "AItech", "Remote",
                "$110,000 - $140,000", "Python, TensorFlow, ML, AI"));
        jobs.add(new Job("Software QA Tester", "BugHunters", "Austin, USA",
                "$60,000 - $75,000", "Selenium, Java, Testing, JUnit"));
        return jobs;
    }
}