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

    public static String[] getPictures() {
        return PICTURES;
    }
}
