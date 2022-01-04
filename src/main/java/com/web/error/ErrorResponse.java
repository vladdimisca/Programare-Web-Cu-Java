package com.web.error;

import java.util.List;

public record ErrorResponse(List<ErrorEntity> errorMessages) {
}
