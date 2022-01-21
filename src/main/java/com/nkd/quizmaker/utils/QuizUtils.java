package com.nkd.quizmaker.utils;

import net.bytebuddy.utility.RandomString;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class QuizUtils {

    public static String createRandomCode() {
        StringBuilder rs = new StringBuilder();
        Random random = new Random();
        String s = RandomString.make();
        while (rs.length() < 6) {
            int i = random.nextInt(s.length() - 3);
            rs.append(s, i, i + 3);
        }
        return rs.toString();

    }
}
