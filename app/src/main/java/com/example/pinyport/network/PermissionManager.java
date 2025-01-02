package com.example.pinyport.network;

import android.content.Context;

import com.google.gson.JsonObject;

public class PermissionManager {

    private final SharedPrefsManager sharedPrefsManager;

    // Constructor to initialize the SharedPrefsManager
    public PermissionManager(Context context) {
        this.sharedPrefsManager = new SharedPrefsManager(context);
    }

    /**
     * Checks if the user has the required permission.
     *
     * @param module     The module name (e.g., "orders", "users").
     * @param action     The action to check (e.g., "create", "edit", "delete").
     * @param createdBy  The ID of the user who created the resource.
     * @return True if the user has permission; otherwise, false.
     */
    public boolean hasPermission(String module, String action, String createdBy) {
        JsonObject user = sharedPrefsManager.getUser();

        if (user == null) {
            return false; // No user data available
        }

        String userId = sharedPrefsManager.getUserId();
        JsonObject permissions = user.getAsJsonObject("permission");

        if (permissions == null || !permissions.has(module)) {
            return false; // No permissions for the module
        }

        JsonObject modulePermissions = permissions.getAsJsonObject(module);
        if (!modulePermissions.has(action)) {
            return false; // No permissions for the action
        }

        String permission = modulePermissions.get(action).getAsString();

        // Permission logic
        switch (action) {
            case "create":
                return "allowed".equals(permission);
            case "delete":
            case "edit":
                return "all".equals(permission) || ("owner".equals(permission) && createdBy.equals(userId));
            default:
                return false; // Unknown action
        }
    }
}
