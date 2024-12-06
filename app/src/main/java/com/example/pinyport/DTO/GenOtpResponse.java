package com.example.pinyport.DTO;

import java.util.List;

public class GenOtpResponse {

    private boolean success;
    private String message;
    private GenOtpResponse.Data data;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public GenOtpResponse.Data getData() { return data; }
    public void setData(GenOtpResponse.Data data) { this.data = data; }

    // Inner Data Class
    public static class Data {
        private String user_id;
        private int otp;
        private String expire_at;
        private String id;
        private String updated_at;
        private String created_at;
        private GenOtpResponse.SendStatus send_status;

        // Getters and Setters
        public String getUser_id() { return user_id; }
        public void setUser_id(String user_id) { this.user_id = user_id; }

        public int getOtp() { return otp; }
        public void setOtp(int otp) { this.otp = otp; }

        public String getExpire_at() { return expire_at; }
        public void setExpire_at(String expire_at) { this.expire_at = expire_at; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getUpdated_at() { return updated_at; }
        public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

        public String getCreated_at() { return created_at; }
        public void setCreated_at(String created_at) { this.created_at = created_at; }

        public GenOtpResponse.SendStatus getSend_status() { return send_status; }
        public void setSend_status(GenOtpResponse.SendStatus send_status) { this.send_status = send_status; }
    }

    // Inner SendStatus Class
    public static class SendStatus {
        private GenOtpResponse.Headers headers;
        private GenOtpResponse.Original original;
        private Object exception; // Assuming it could be null

        // Getters and Setters
        public GenOtpResponse.Headers getHeaders() { return headers; }
        public void setHeaders(GenOtpResponse.Headers headers) { this.headers = headers; }

        public GenOtpResponse.Original getOriginal() { return original; }
        public void setOriginal(GenOtpResponse.Original original) { this.original = original; }

        public Object getException() { return exception; }
        public void setException(Object exception) { this.exception = exception; }
    }

    // Inner Headers Class (Assuming empty for now)
    public static class Headers {
        // Add fields if needed later
    }

    // Inner Original Class
    public static class Original {
        private boolean success;
        private GenOtpResponse.OriginalData data;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public GenOtpResponse.OriginalData getData() { return data; }
        public void setData(GenOtpResponse.OriginalData data) { this.data = data; }
    }

    // Inner OriginalData Class
    public static class OriginalData {
        private List<GenOtpResponse.Message> messages;

        // Getters and Setters
        public List<GenOtpResponse.Message> getMessages() { return messages; }
        public void setMessages(List<GenOtpResponse.Message> messages) { this.messages = messages; }
    }

    // Inner Message Class
    public static class Message {
        private String messageId;
        private GenOtpResponse.Status status;
        private String to;

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }

        public GenOtpResponse.Status getStatus() { return status; }
        public void setStatus(GenOtpResponse.Status status) { this.status = status; }

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
    }

    // Inner Status Class
    public static class Status {
        private String description;
        private int groupId;
        private String groupName;
        private int id;
        private String name;

        // Getters and Setters
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getGroupId() { return groupId; }
        public void setGroupId(int groupId) { this.groupId = groupId; }

        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
