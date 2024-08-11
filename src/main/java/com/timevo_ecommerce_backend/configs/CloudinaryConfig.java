package com.timevo_ecommerce_backend.configs;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {
    private final String CLOUD_NAME = "dvzgkve6l";
    private final String API_KEY = "772873499257764";
    private final String API_SECRET = "S_6T7TFQ3x_OEUSaAHgqIc_bRkE";

    @Bean
    public Cloudinary cloudinary () {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        return new Cloudinary(config);
    }
}
