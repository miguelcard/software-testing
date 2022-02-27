package com.amigoscode.testing.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

// "Off course you would use a library for this but this is just an example"
@Component
public class PhoneNumberValidator implements Predicate<String> {
    @Override
    public boolean test(String phoneNumber) {
        String withoutSpaces = phoneNumber.replaceAll(" ","");
        if(withoutSpaces.startsWith("+")) {
            String withoutSpacesAndSymbol = withoutSpaces.replace("+", "");
            if(withoutSpacesAndSymbol.matches("[0-9]+") && withoutSpacesAndSymbol.length() == 12) {
                return true;
            }
        }
        return false;
    }
}
