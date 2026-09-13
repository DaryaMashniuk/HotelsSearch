package by.mashnyuk.hotels.model;

import by.mashnyuk.hotels.exceptions.IllegalArgumentCustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HistogramParamTest {

    @ParameterizedTest
    @ValueSource(strings = {"brand", "BRAND", "Brand", "bRaNd"})
    @DisplayName("Should parse BRAND enum regardless of case")
    void shouldParseBrandCaseInsensitive(String param) {
        HistogramParam result = HistogramParam.fromString(param);

        assertThat(result).isEqualTo(HistogramParam.BRAND);
    }

    @ParameterizedTest
    @ValueSource(strings = {"city", "country", "amenities"})
    @DisplayName("Should successfully parse all supported histogram parameters")
    void shouldParseAllSupportedParams(String param) {
        HistogramParam result = HistogramParam.fromString(param);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualToIgnoringCase(param);
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "invalid", "123", "", " "})
    @DisplayName("Should throw IllegalArgumentCustomException for unsupported parameter values")
    void shouldThrowExceptionForInvalidParams(String param) {
        assertThatThrownBy(() -> HistogramParam.fromString(param))
                .isInstanceOf(IllegalArgumentCustomException.class)
                .hasMessageContaining("Unsupported histogram parameter: " + param);
    }

    @Test
    @DisplayName("Should throw exception when param is null")
    void shouldThrowExceptionWhenParamIsNull() {
        assertThatThrownBy(() -> HistogramParam.fromString(null))
                .isInstanceOf(IllegalArgumentCustomException.class);
    }
}