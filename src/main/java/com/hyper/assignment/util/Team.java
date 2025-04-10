package com.hyper.assignment.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Team {

    DEVELOPMENT("Development"),
    QA("QA");

    private final String name;

}
