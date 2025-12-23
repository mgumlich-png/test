package com.example.gks.dto;

public class DocumentUploadResponse {
    private Long id;
    private int version;
    private String fileName;
    private String storageKey;

    public DocumentUploadResponse(Long id, int version, String fileName, String storageKey) {
        this.id = id;
        this.version = version;
        this.fileName = fileName;
        this.storageKey = storageKey;
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStorageKey() {
        return storageKey;
    }
}
