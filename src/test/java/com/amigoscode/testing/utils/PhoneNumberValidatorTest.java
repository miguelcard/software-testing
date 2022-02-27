package com.amigoscode.testing.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator validatorUnderTest;

    @BeforeEach
    private void setUp() {
        validatorUnderTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "+43 677 6336944,true",  // you can also add a custom message for each one in the object
            "+43 677 6336944000,false",
            "43 677 6336944,false"
    })
    void CustomerPhoneNumberShouldBeValid(String phoneNumber, boolean expectedValue) {
//        // Given
//         String phoneNumber = "+43 677 6336944"; can be commented out

        // When
        boolean isValid = validatorUnderTest.test(phoneNumber);

        // Then
        assertThat(isValid).isEqualTo(expectedValue);
    }
    // this above test replaces all of the ones below


    // Next 2 tests not Needed, already tested above!
    @Test
    @DisplayName("Phone number should not have more digits than 12")
    void CustomerPhoneNumberShouldNotBeValid() {
        // Given
        String phoneNumber = "+43 677 6336944000";

        // When
        boolean isValid = validatorUnderTest.test(phoneNumber);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Phone number should start with +")
    void CustomerPhoneNumberShouldStartWithPlus() {
        // Given
        String phoneNumber = "43 677 6336944";

        // When
        boolean isValid = validatorUnderTest.test(phoneNumber);

        // Then
        assertThat(isValid).isFalse();
    }

}
