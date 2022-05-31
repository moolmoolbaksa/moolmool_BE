package com.sparta.mulmul.service;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptConfigTest {
    @Test
    public void jasypt_test() {
        String plaintText = "Oo1aCKnxXrXzjZ9Qs0BN+p3levL7Hgj82NH6yrsv";
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("moolmool");
//        config.setPassword(System.getenv("JASYPT_PASSWORD"));
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        String encryptText = encryptor.encrypt(plaintText);
        System.out.println(encryptText);
        String decryptText = encryptor.decrypt(encryptText);
        System.out.println(decryptText);
        Assertions.assertThat(plaintText).isEqualTo(decryptText);

    }

}

