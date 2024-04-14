package com.github.lipeacelino.fileconvertapi.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class RegexUtil {

    private static final Pattern GET_USER_ID_FROM_LINE_PATTERN = Pattern.compile("\\s.*$");

    private Integer getUserIdFromLine(String line) {
        return Integer.valueOf(line.replaceFirst("\\s.*$", "")); //remove tudo após o primeiro espaço
    }

    private String getUsernameFromLine(String line) {
        return line.replaceAll("\\d+.", "").trim(); //remove pontos e tudo que não for letra
//                .replaceAll("^\\s+?(?=[a-zA-Z])", "") //remove espaços do início
//                .replaceAll("(?<=[a-zA-Z])\\s+$", ""); //remove espaços do final
    }

    private Integer getOrderIdFromLine(String line) {
        return Integer.valueOf(line.replaceAll(".*(?<=[(a-zA-Z)+.])(\\d+)(?=\\s).*", "$1") //mantém apenas o orderId e productId
                .replaceAll("^(.{10}).*", "$1")); //mantém apenas o orderId
    }

    private Integer getProductIdFromLine(String line) {
        return Integer.valueOf(line.replaceAll(".*(?<=[(a-zA-Z)+.])(\\d+)(?=\\s).*", "$1") //mantém apenas o orderId e productId
                .replaceAll("^.{10}(.{10}).*", "$1")); //mantém apenas o productId
    }

    private BigDecimal getValueFromLine(String line) {
        return new BigDecimal(line.replaceFirst("^.*\\s", "") //mantém apenas value e date
                .replaceAll("(\\.\\d{2}).*$", "$1")); //mantém apenas value
    }

    private String getDateFromLine(String line) {
        return line.replaceFirst("^.*\\s", "") //mantém apenas value e date
                .replaceAll("^.*(?=(.{8})$)", ""); //mantém apenas o date
    }

}
