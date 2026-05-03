package com.bervan.projectmgmtapp.api;

import java.util.UUID;

public record TaskSearchResultDto(UUID id, String number, String name, String status, String type) {
}
