package io.marketplace.services.transaction.processing.service;

import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.commons.utils.MembershipUtils;
import io.marketplace.services.notification.client.dto.NotificationMessage;
import io.marketplace.services.notification.client.service.NotificationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

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

@Component
public class NotificationService {

    @Value("${notification.entity-id:101D}")
    private String notificationEntityId;

    @Value("${notification.app-id:RetailMobileApp}")
    private String notificationAppId;

    @Value("${notification.enabled:true}")
    private boolean notificationEnabled;

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    public void sendNotification(Map<String, Object> dataMap, String templateName) {
        sendNotification(dataMap, MembershipUtils.getUserId(), templateName);
    }

    public void sendNotification(Map<String, Object> dataMap, String userId, String templateName) {

        if (notificationEnabled) {
            EventMessage<NotificationMessage> event =
                    EventMessage.<NotificationMessage>builder()
                            .appId(notificationAppId)
                            .entityId(notificationEntityId)
                            .eventUser(userId)
                            .businessData(
                                    NotificationMessage.builder()
                                            .templateName(templateName)
                                            .additionalData(dataMap)
                                            .build())
                            .build();
            notificationServiceClient.sendNotificationAsync(event);
        }
    }
}
