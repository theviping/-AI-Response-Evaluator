import java.util.*;

class Response {
    int id;
    String text;
    int score;
    List<String> feedback;

    Response(int id, String text) {
        this.id = id;
        this.text = text == null ? "" : text.trim();
        this.feedback = new ArrayList<>();
    }
}

public class AIResponseEvaluator {

    private static final String[] KEYWORDS = {"ai", "data", "model", "learning"};

    // Score constants (clean code practice)
    private static final int LENGTH_SCORE = 2;
    private static final int CLARITY_SCORE = 2;
    private static final int CAPITAL_SCORE = 1;

    public static int evaluate(Response r) {
        int score = 0;
        r.feedback.clear();

        String text = r.text;
        String lowerText = text.toLowerCase();

        // 0. Empty Check
        if (text.isEmpty()) {
            r.feedback.add("Empty response");
            r.score = 0;
            return 0;
        }

        // 1. Length Check
        if (text.length() > 50) {
            score += LENGTH_SCORE;
        } else {
            r.feedback.add("Too short");
        }

        // 2. Sentence Structure Check
        if (text.matches(".*[.!?].*")) {
            score += CLARITY_SCORE;
        } else {
            r.feedback.add("Lacks sentence structure");
        }

        // 3. Keyword Matching (optimized)
        String[] words = lowerText.split("\\s+");
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));

        int keywordCount = 0;
        for (String keyword : KEYWORDS) {
            if (wordSet.contains(keyword)) {
                keywordCount++;
            }
        }
        score += keywordCount;

        // 4. Grammar Check (safe)
        if (!text.isEmpty() && Character.isUpperCase(text.charAt(0))) {
            score += CAPITAL_SCORE;
        } else {
            r.feedback.add("Does not start with capital letter");
        }

        // 5. Repetition Check (improved)
        Set<String> uniqueWords = new HashSet<>(wordSet);

        if (words.length > 6 && uniqueWords.size() < words.length * 0.6) {
            r.feedback.add("Repetitive / low quality content");
            score -= 2;
        }

        // Prevent negative score
        r.score = Math.max(score, 0);

        // Remove duplicate feedback (optional clean)
        Set<String> feedbackSet = new LinkedHashSet<>(r.feedback);
        r.feedback = new ArrayList<>(feedbackSet);

        return r.score;
    }

    // Input validation
    private static int readPositiveInteger(Scanner sc) {
        while (true) {
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine();

                if (value > 0) return value;
            } else {
                sc.nextLine();
            }

            System.out.print("Please enter a positive number: ");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your question: ");
        String question = sc.nextLine();
        System.out.println("Question: " + question);

        System.out.print("How many responses? ");
        int n = readPositiveInteger(sc);

        List<Response> responses = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            System.out.println("\nEnter Response " + i + ":");
            String text = sc.nextLine();
            responses.add(new Response(i, text));
        }

        // Evaluate responses
        for (Response r : responses) {
            evaluate(r);
        }

        // Sort (best practice comparator)
        responses.sort(Comparator.comparingInt((Response r) -> r.score).reversed());

        // Display ranking
        System.out.println("\n--- Ranking ---");
        int rank = 1;
        for (Response r : responses) {
            System.out.println("\nRank " + rank + ": Response " + r.id);
            System.out.println("Score: " + r.score);
            System.out.println("Feedback: " +
                    (r.feedback.isEmpty() ? "Good" : String.join(", ", r.feedback)));
            rank++;
        }

        sc.close();
    }
}