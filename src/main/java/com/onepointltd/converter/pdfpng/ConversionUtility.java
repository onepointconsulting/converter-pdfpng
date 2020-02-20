package com.onepointltd.converter.pdfpng;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Used to convert PDF's into images.
 */
public class ConversionUtility {

    private static final int DEFAULT_DPI = 300;

    public static String[] generatePng(String base64PDF, Integer dpi) throws IOException {
        if (dpi == null) {
            dpi = DEFAULT_DPI;
        }
        byte[] input = Base64.getDecoder().decode(base64PDF);
        try (final PDDocument document = PDDocument.load(input)) {
            if (document.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("Document has no pages.");
            }
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            final int allDpi = dpi;
            return IntStream.range(0, document.getNumberOfPages()).boxed()
                    .map(page -> {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, allDpi, ImageType.RGB);
                            ImageIO.write(bim, "png", baos);
                            byte[] imageInByte = baos.toByteArray();
                            return Base64.getEncoder().encodeToString(imageInByte);
                        } catch (IOException e) {
                            throw new RuntimeException(String.format("Could not convert from page %s", page));
                        }
                    }).toArray(String[]::new);
        }
    }

    public static InputStream[] generatePngStream(String base64PDF, Integer dpi) throws IOException {
        String[] strings = generatePng(base64PDF, dpi);
        return Arrays.stream(strings).map(s -> new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)))
                .toArray(InputStream[]::new);
    }
}
