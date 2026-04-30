package utils;

public class RoleUtils {

    private RoleUtils() {
    }

    public static boolean canManageInternships(String role) {
        return "admin".equalsIgnoreCase(role)
                || "business".equalsIgnoreCase(role)
                || "company".equalsIgnoreCase(role);
    }

    public static String displayRole(String role) {
        if (canManageInternships(role)) {
            return "Business";
        }

        return "Intern";
    }
}