package io.marketplace.services.transaction.processing.utils;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/*******************************************************************************
 *  Copyright © 2018-2021 101 Digital PTE LTD
 *
 *  All rights reserved. No part of this code may be reproduced, distributed,
 *  or transmitted in any form or by any means, including photocopying, recording,
 *  or other electronic or mechanical methods, without the prior written permission of 101 Digital PTE LTD.
 *
 *  Users agree to fully indemnify and hold harmless 101 Digital PTE LTD from
 *  and against any and all claims, demands, suits, losses, damages, costs
 *  and expenses arising out of the User's use of the Software, including,
 *  without limitation, arising out of the User's modification of the Software.
 *
 *  For permission requests, write to the address below.
 *  101 Digital PTE LTD
 *  61A Tras Street
 *  Singapore 079000
 *  Republic of Singapore
 *  www.101Digital.io
 *******************************************************************************/
public class FormatterUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormatterUtil.class);

    private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_TO = DateTimeFormatter.ofPattern("dd MMM yyyy',' hh:mm:ss a");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_FROM = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private FormatterUtil(){}

    public static String formatToTwoDecimals(BigDecimal value){
        if (value == null){
            return null;
        }
        return TWO_DECIMAL_FORMAT.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    public static String formatToTwoDecimals(String value){
        if (value == null){
            return null;
        }
        return formatToTwoDecimals(new BigDecimal(value));
    }

    public static String formatTimestamp(LocalDateTime dateTime, String timezone){
        if (dateTime == null){
            return null;
        }
        return dateTime.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of(timezone))
                .format(DATE_TIME_FORMATTER_TO);
    }

    public static String formatTimestamp(String dateTime, String timezone){
        if (dateTime == null){
            return null;
        }
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER_FROM);
            return formatTimestamp(parsedDateTime, timezone);
        }
        catch (DateTimeParseException ex){
            LOGGER.warn("Couldn't parse datetime", ex);
        }
        return dateTime;
    }
}

