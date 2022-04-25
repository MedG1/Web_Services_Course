package tn.example.samplews.entities;


public interface Auditable {
    AuditFields getAuditFields();
    void setAuditFields(AuditFields auditFields);
}
