package me.gaegul.ch05;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trader {
    private final String name;
    private final String city;
}
