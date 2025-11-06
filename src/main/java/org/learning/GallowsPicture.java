package org.learning;

public class GallowsPicture {
    private static final String[] PICTURES = {
            """
      ----
    |/  |
    |   *
    |  /||
    |   |
    |  /\\
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
    """
    };

    public String getPictures(int remainingAttempts) {
        return PICTURES[remainingAttempts];
    }
}
