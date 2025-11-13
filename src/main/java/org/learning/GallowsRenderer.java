package org.learning;

public class GallowsRenderer {
    private static final String[] STAGES = {
            """
      ----
    |/  |
    |   *
    |  /||
    |   |
    |   /\\
    """,
            """
     ----
    |/  |
    |   *
    |  /||
    |   |
    |
    """,
            """
     ----
    |/  |
    |   *
    |  /||
    |
    |
    """,
            """
     ----
    |/  |
    |   *
    |
    |
    |
    """,
            """
     ----
    |/  |
    |
    |
    |
    |
    """,
            """
    |
    |
    |
    |
    |
    """,
    ""
    };

    public static void render(int remainingAttempts) {
        if (remainingAttempts >= STAGES.length || remainingAttempts < 0) {
            System.out.println("No stage available for " + remainingAttempts);
            return;
        }
        System.out.println(STAGES[remainingAttempts]);
    }
}
