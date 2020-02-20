package com.onepointltd.converter.pdfpng;

import org.apache.pdfbox.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ConversionUtilityTest {

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    @Test
    void generateSinglePng() throws IOException {
        convertFromPath("Test1Page");
    }

    @Test
    void generateMultiplePngs() throws IOException {
        convertFromPath("DEV3_Section1_Activities_V7.1");
    }

    private void convertFromPath(String fileName) throws IOException {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName + ".pdf")) {

            assertNotNull(in, "PDF file not found");

            byte[] bytes = toByteArray(in);
            String encoded = new String(Base64.getEncoder().encode(bytes));
            String[] pngEncodeds = ConversionUtility.generatePng(encoded, null);
            IntStream.range(0, pngEncodeds.length).forEach(i -> {
                String pngEncoded = pngEncodeds[i];
                byte[] decoded = Base64.getDecoder().decode(pngEncoded);
                try {
                    Files.write(Paths.get(String.format("%s%d.png", fileName, i)), decoded);
                } catch (IOException e) {
                    fail("Failed at page " + i, e);
                }
            });
        }
    }
}