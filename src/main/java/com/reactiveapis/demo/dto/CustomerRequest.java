package com.reactiveapis.demo.dto;

public record CustomerRequest(
        String name,
        String phone,
        String email,
        String logo
) {
}
