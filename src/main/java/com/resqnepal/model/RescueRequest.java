package com.resqnepal.model;

import com.resqnepal.model.enums.RequestStatus;
import com.resqnepal.model.enums.Severity;

public class RescueRequest implements Comparable<RescueRequest> {
    private String requestId;
    private int victimId;
    private String type;
    private Severity severity;
    private int peopleAffected;
    private String requiredSpecialization;
    private RequestStatus status;
    private String description;

    public RescueRequest() {}

    public RescueRequest(String requestId, int victimId, String type, Severity severity, int peopleAffected, String requiredSpecialization, RequestStatus status, String description) {
        this.requestId = requestId;
        this.victimId = victimId;
        this.type = type;
        this.severity = severity;
        this.peopleAffected = peopleAffected;
        this.requiredSpecialization = requiredSpecialization;
        this.status = status;
        this.description = description;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public int getVictimId() { return victimId; }
    public void setVictimId(int victimId) { this.victimId = victimId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public int getPeopleAffected() { return peopleAffected; }
    public void setPeopleAffected(int peopleAffected) { this.peopleAffected = peopleAffected; }

    public String getRequiredSpecialization() { return requiredSpecialization; }
    public void setRequiredSpecialization(String requiredSpecialization) { this.requiredSpecialization = requiredSpecialization; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public int compareTo(RescueRequest other) {
        return Integer.compare(other.getSeverity().getLevel(), this.getSeverity().getLevel());
    }

    @Override
    public String toString() {
        return "RescueRequest{" +
                "requestId='" + requestId + '\'' +
                ", victimId=" + victimId +
                ", type='" + type + '\'' +
                ", severity=" + severity +
                ", status=" + status +
                '}';
    }
}
