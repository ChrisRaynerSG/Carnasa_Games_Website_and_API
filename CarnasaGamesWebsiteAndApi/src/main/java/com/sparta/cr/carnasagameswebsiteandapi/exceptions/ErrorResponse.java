package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

public record ErrorResponse(Object errorDetails, String errorCode, String url) {
}
