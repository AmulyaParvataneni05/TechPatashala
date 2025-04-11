package com.example.techpatashala;

public class Upload {
    private String title;
    private String type;
    private String fileUrl;

    public Upload() {
        // Default constructor required for Firebase
    }

    public Upload(String title, String type, String fileUrl) {
        this.title = title;
        this.type = type;
        this.fileUrl = fileUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
