package com.resqnepal.model;

import com.resqnepal.model.enums.OperationStatus;
import java.time.LocalDateTime;

public class RescueOperation {
    private String opId;
    private String requestId;
    private String teamId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private OperationStatus status;

    public RescueOperation() {}

    public RescueOperation(String opId, String requestId, String teamId, LocalDateTime startTime, LocalDateTime endTime, OperationStatus status) {
        this.opId = opId;
        this.requestId = requestId;
        this.teamId = teamId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getOpId() { return opId; }
    public void setOpId(String opId) { this.opId = opId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public OperationStatus getStatus() { return status; }
    public void setStatus(OperationStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "RescueOperation{" +
                "opId='" + opId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", teamId='" + teamId + '\'' +
                ", status=" + status +
                '}';
    }
}
