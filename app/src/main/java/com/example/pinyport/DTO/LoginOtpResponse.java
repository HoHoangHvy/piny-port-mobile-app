package com.example.pinyport.DTO;

import java.util.List;
public class LoginOtpResponse {
    private boolean success;
    private Data data;
    private String message;

    // Getter and Setter for success
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Getter and Setter for data
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Inner Data Class
    public static class Data {
        private String token;
        private User user;

        // Getter and Setter for token
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        // Getter and Setter for user
        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        // Inner User Class
        public static class User {
            private String id;
            private String name;
            private String email;
            private String phoneNumber;
            private String emailVerifiedAt;
            private String apiToken;
            private String twoFactorSecret;
            private String twoFactorRecoveryCodes;
            private int isAdmin;
            private String userType;
            private String teamId;
            private String createdAt;
            private String updatedAt;
            private String deletedAt;
            private String createdBy;
            private List<Role> roles;

            // Getter and Setter for id
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            // Getter and Setter for name
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            // Getter and Setter for email
            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            // Getter and Setter for phoneNumber
            public String getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
            }

            // Getter and Setter for emailVerifiedAt
            public String getEmailVerifiedAt() {
                return emailVerifiedAt;
            }

            public void setEmailVerifiedAt(String emailVerifiedAt) {
                this.emailVerifiedAt = emailVerifiedAt;
            }

            // Getter and Setter for apiToken
            public String getApiToken() {
                return apiToken;
            }

            public void setApiToken(String apiToken) {
                this.apiToken = apiToken;
            }

            // Getter and Setter for twoFactorSecret
            public String getTwoFactorSecret() {
                return twoFactorSecret;
            }

            public void setTwoFactorSecret(String twoFactorSecret) {
                this.twoFactorSecret = twoFactorSecret;
            }

            // Getter and Setter for twoFactorRecoveryCodes
            public String getTwoFactorRecoveryCodes() {
                return twoFactorRecoveryCodes;
            }

            public void setTwoFactorRecoveryCodes(String twoFactorRecoveryCodes) {
                this.twoFactorRecoveryCodes = twoFactorRecoveryCodes;
            }

            // Getter and Setter for isAdmin
            public int getIsAdmin() {
                return isAdmin;
            }

            public void setIsAdmin(int isAdmin) {
                this.isAdmin = isAdmin;
            }

            // Getter and Setter for userType
            public String getUserType() {
                return userType;
            }

            public void setUserType(String userType) {
                this.userType = userType;
            }

            // Getter and Setter for teamId
            public String getTeamId() {
                return teamId;
            }

            public void setTeamId(String teamId) {
                this.teamId = teamId;
            }

            // Getter and Setter for createdAt
            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            // Getter and Setter for updatedAt
            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            // Getter and Setter for deletedAt
            public String getDeletedAt() {
                return deletedAt;
            }

            public void setDeletedAt(String deletedAt) {
                this.deletedAt = deletedAt;
            }

            // Getter and Setter for createdBy
            public String getCreatedBy() {
                return createdBy;
            }

            public void setCreatedBy(String createdBy) {
                this.createdBy = createdBy;
            }

            // Getter and Setter for roles
            public List<Role> getRoles() {
                return roles;
            }

            public void setRoles(List<Role> roles) {
                this.roles = roles;
            }

            // Inner Role Class
            public static class Role {
                private String id;
                private String name;
                private String description;
                private String guardName;
                private String createdAt;
                private String updatedAt;
                private String createdBy;
                private boolean isAdmin;
                private boolean applyTeamVisibility;
                private Pivot pivot;

                // Getter and Setter for id
                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                // Getter and Setter for name
                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                // Getter and Setter for description
                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                // Getter and Setter for guardName
                public String getGuardName() {
                    return guardName;
                }

                public void setGuardName(String guardName) {
                    this.guardName = guardName;
                }

                // Getter and Setter for createdAt
                public String getCreatedAt() {
                    return createdAt;
                }

                public void setCreatedAt(String createdAt) {
                    this.createdAt = createdAt;
                }

                // Getter and Setter for updatedAt
                public String getUpdatedAt() {
                    return updatedAt;
                }

                public void setUpdatedAt(String updatedAt) {
                    this.updatedAt = updatedAt;
                }

                // Getter and Setter for createdBy
                public String getCreatedBy() {
                    return createdBy;
                }

                public void setCreatedBy(String createdBy) {
                    this.createdBy = createdBy;
                }

                // Getter and Setter for isAdmin
                public boolean isAdmin() {
                    return isAdmin;
                }

                public void setAdmin(boolean admin) {
                    isAdmin = admin;
                }

                // Getter and Setter for applyTeamVisibility
                public boolean isApplyTeamVisibility() {
                    return applyTeamVisibility;
                }

                public void setApplyTeamVisibility(boolean applyTeamVisibility) {
                    this.applyTeamVisibility = applyTeamVisibility;
                }

                // Inner Pivot Class
                public static class Pivot {
                    private String modelType;
                    private String modelId;
                    private String roleId;

                    // Getter and Setter for modelType
                    public String getModelType() {
                        return modelType;
                    }

                    public void setModelType(String modelType) {
                        this.modelType = modelType;
                    }

                    // Getter and Setter for modelId
                    public String getModelId() {
                        return modelId;
                    }

                    public void setModelId(String modelId) {
                        this.modelId = modelId;
                    }

                    // Getter and Setter for roleId
                    public String getRoleId() {
                        return roleId;
                    }

                    public void setRoleId(String roleId) {
                        this.roleId = roleId;
                    }
                }
            }
        }
    }
}
