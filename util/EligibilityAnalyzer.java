package util;

import model.Job;

import java.util.*;

public class EligibilityAnalyzer {

    public static class Result {
        public int score;
        public List<String> matchedSkills       = new ArrayList<>();
        public List<String> missingSkills        = new ArrayList<>();
        public List<String> recommendedSkills    = new ArrayList<>();
        public List<String> qualificationGaps    = new ArrayList<>();
        public List<String> strengths            = new ArrayList<>();
        public List<String> suggestions          = new ArrayList<>();
        public List<String> aiRecommendations    = new ArrayList<>();
        public String verdict;
        public String experienceCompatibility = "";
        public String qualificationStatus    = "";
        public int skillMatchPercent         = 0;
        public int experienceScore           = 0;
        public int educationScore            = 0;
        public int extrasScore               = 0;
    }

    public static Result analyze(Job job, Map<String, String> formData) {
        Result r = new Result();

        String userSkills      = orEmpty(formData.get("skills"));
        String softSkills      = orEmpty(formData.get("softSkills"));
        String tools           = orEmpty(formData.get("tools"));
        String experience      = orEmpty(formData.get("experience"));
        String totalExp        = orEmpty(formData.get("totalExperience"));
        String education       = orEmpty(formData.get("education"));
        String degree          = orEmpty(formData.get("degreeTitle"));
        String qualification   = orEmpty(formData.get("highestQualification"));
        String certifications  = orEmpty(formData.get("certifications"));
        String languages       = orEmpty(formData.get("languages"));
        String achievements    = orEmpty(formData.get("achievements"));
        String portfolio       = orEmpty(formData.get("portfolio"));
        String linkedin        = orEmpty(formData.get("linkedin"));
        String gpa             = orEmpty(formData.get("gpa"));

        // ---- SKILLS ANALYSIS (50% weight) ----
        List<String> required = tokenize(job.getRequiredSkills());
        List<String> userList = tokenize(userSkills + "," + softSkills + "," + tools);

        for (String req : required) {
            boolean found = false;
            for (String us : userList) {
                if (req.equalsIgnoreCase(us) || req.contains(us) || us.contains(req)) {
                    found = true; break;
                }
            }
            if (found) r.matchedSkills.add(capitalize(req));
            else       r.missingSkills.add(capitalize(req));
        }

        double skillScore = required.isEmpty() ? 0
                : (r.matchedSkills.size() * 100.0 / required.size());
        r.skillMatchPercent = (int) Math.round(skillScore);

        // ---- EXPERIENCE ANALYSIS (20% weight) ----
        double expScore = 0;
        String allExp = experience + " " + totalExp;
        if (!allExp.trim().isEmpty()) {
            int wordCount = allExp.trim().split("\\s+").length;
            // Check for years keywords
            boolean hasYears = allExp.toLowerCase().matches(".*\\b[0-9]+\\s*(year|yr).*");
            if (wordCount > 80 || hasYears)  expScore = 100;
            else if (wordCount > 40) expScore = 80;
            else if (wordCount > 20) expScore = 60;
            else if (wordCount > 5)  expScore = 40;
            else                     expScore = 20;
        } else {
            r.qualificationGaps.add("Work experience section not provided");
        }
        r.experienceScore = (int) Math.round(expScore);

        // ---- EDUCATION ANALYSIS (15% weight) ----
        double eduScore = 0;
        String eduAll = (education + " " + qualification + " " + degree).toLowerCase();
        if      (eduAll.contains("phd") || eduAll.contains("doctorate"))   { eduScore = 100; r.qualificationStatus = "PhD / Doctorate"; }
        else if (eduAll.contains("master") || eduAll.contains("msc") || eduAll.contains("mba")) { eduScore = 90; r.qualificationStatus = "Master's Degree"; }
        else if (eduAll.contains("bachelor") || eduAll.contains("bsc") || eduAll.contains("b.tech") || eduAll.contains("be") || eduAll.contains("beng")) { eduScore = 80; r.qualificationStatus = "Bachelor's Degree"; }
        else if (eduAll.contains("diploma") || eduAll.contains("associate")) { eduScore = 60; r.qualificationStatus = "Diploma / Associate"; }
        else if (!eduAll.trim().isEmpty()) { eduScore = 45; r.qualificationStatus = "Other Qualification"; }
        else {
            eduScore = 0;
            r.qualificationStatus = "Not Provided";
            r.qualificationGaps.add("Educational qualification not provided");
        }
        r.educationScore = (int) Math.round(eduScore);

        // Check GPA
        if (!gpa.isEmpty()) {
            try {
                double gpaVal = Double.parseDouble(gpa.replaceAll("[^0-9.]", ""));
                if (gpaVal >= 3.5 || gpaVal >= 8.5) {
                    r.strengths.add("Excellent academic record (GPA: " + gpa + ")");
                    eduScore = Math.min(100, eduScore + 5);
                }
            } catch (NumberFormatException ignored) {}
        }

        // ---- CERTIFICATIONS & EXTRAS (15% weight) ----
        double extraScore = 0;
        if (!certifications.isEmpty()) {
            extraScore += 40;
            r.strengths.add("Professional certifications add strong credibility");
        } else {
            r.qualificationGaps.add("No professional certifications listed");
        }
        if (!languages.isEmpty())     extraScore += 15;
        if (!portfolio.isEmpty())     extraScore += 25;
        else r.suggestions.add("Add a portfolio URL or GitHub link to showcase your work");
        if (!linkedin.isEmpty())      extraScore += 15;
        else r.suggestions.add("A complete LinkedIn profile significantly increases recruiter visibility");
        if (!achievements.isEmpty())  extraScore += 10;
        else r.suggestions.add("Add notable achievements or awards to stand out from other candidates");

        if (extraScore > 100) extraScore = 100;
        r.extrasScore = (int) Math.round(extraScore);

        // ---- FINAL SCORE ----
        double finalScore = (skillScore * 0.50) + (expScore * 0.20)
                          + (eduScore * 0.15) + (extraScore * 0.15);
        r.score = (int) Math.round(finalScore);

        // ---- EXPERIENCE COMPATIBILITY ----
        if (expScore >= 80)      r.experienceCompatibility = "Excellent — detailed and relevant experience";
        else if (expScore >= 60) r.experienceCompatibility = "Good — solid experience background";
        else if (expScore >= 40) r.experienceCompatibility = "Moderate — some experience provided";
        else if (expScore > 0)   r.experienceCompatibility = "Limited — elaborate your experience more";
        else                     r.experienceCompatibility = "Not provided — this significantly impacts eligibility";

        // ---- STRENGTHS ----
        if (!r.matchedSkills.isEmpty())
            r.strengths.add("You match " + r.matchedSkills.size() + " of " + required.size() + " required skills");
        if (expScore >= 75) r.strengths.add("Strong, detailed work experience description");
        if (eduScore >= 80) r.strengths.add("Excellent educational background");
        if (!portfolio.isEmpty()) r.strengths.add("Portfolio or resume link provided");
        if (!languages.isEmpty()) r.strengths.add("Multilingual — valuable in global work environments");
        if (r.matchedSkills.size() == required.size() && !required.isEmpty())
            r.strengths.add("Perfect skill match — you meet ALL required technical skills!");

        // ---- RECOMMENDED SKILLS ----
        for (String missing : r.missingSkills) {
            r.recommendedSkills.add("Learn " + missing + " — check Coursera, Udemy, or official docs");
        }

        // ---- SUGGESTIONS ----
        if (skillScore < 50)
            r.suggestions.add("Focus on core technical skills listed in the job requirements");
        if (expScore < 50)
            r.suggestions.add("Elaborate on your roles, projects, and measurable impact in experience section");
        if (eduScore < 60)
            r.suggestions.add("Consider pursuing relevant higher education or specialized online certifications");
        if (r.missingSkills.size() > 3)
            r.suggestions.add("Take structured courses to cover the " + r.missingSkills.size() + " missing skills");

        // ---- AI RECOMMENDATIONS ----
        r.aiRecommendations.add("Tailor your resume keywords to match: " + job.getRequiredSkills());
        if (!r.missingSkills.isEmpty())
            r.aiRecommendations.add("Priority upskilling: start with " + r.missingSkills.get(0)
                    + (r.missingSkills.size() > 1 ? " then " + r.missingSkills.get(1) : ""));
        r.aiRecommendations.add("Research " + job.getCompany() + " culture and values before any interview");
        if (expScore < 70)
            r.aiRecommendations.add("Build 1-2 personal projects that demonstrate the required skills");
        r.aiRecommendations.add("Network with professionals at " + job.getCompany() + " on LinkedIn");

        // ---- VERDICT ----
        if      (r.score >= 85) r.verdict = "Exceptional Match! You're a top candidate for this role.";
        else if (r.score >= 70) r.verdict = "Strong Match — apply with confidence, you're well-qualified.";
        else if (r.score >= 55) r.verdict = "Good Match — minor gaps that can be bridged quickly.";
        else if (r.score >= 40) r.verdict = "Moderate Match — meaningful upskilling will boost your chances.";
        else                    r.verdict = "Low Match — focus on the improvement areas below to qualify.";

        return r;
    }

    private static List<String> tokenize(String s) {
        List<String> list = new ArrayList<>();
        if (s == null) return list;
        for (String t : s.toLowerCase().split("[,;|/]")) {
            String trimmed = t.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }

    private static String orEmpty(String s) { return s == null ? "" : s.trim(); }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
