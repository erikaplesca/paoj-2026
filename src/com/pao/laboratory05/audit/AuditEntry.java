package com.pao.laboratory05.audit;

public record AuditEntry(String action, String target, String timestamp) { }

