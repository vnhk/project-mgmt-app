package com.bervan.projectmgmtapp.api;

import java.util.UUID;

public record AddRelationRequest(UUID parentTaskId, UUID childTaskId, String type) {
}
