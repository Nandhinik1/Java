package com.my.core.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Utilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.my.core.constants.myCoreConstants.*;

public class FirebasePushNotificationService {

    private static final Logger LOG = Logger.getLogger(FirebasePushNotificationService.class);
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Initialize Firebase App
     *
     * @return
     */
    public FirebaseApp initializeFirebaseApp(String appName, String securityFileName){
        try {
            List<FirebaseApp> firebaseAppList = FirebaseApp.getApps().stream().filter(app -> app.getName().equalsIgnoreCase(appName)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(firebaseAppList)){
                LOG.info(String.format("Firebase already Initialized for the App name :: %s", appName));
                return firebaseAppList.get(0);
            }
            String securityFilePath = Utilities.getExtensionInfo(EXTENSIONNAME).getExtensionDirectory().toString().concat(SUFFIX).concat(RESOURCES).concat(SUFFIX).concat(securityFileName);
            FileInputStream serviceAccount = new FileInputStream(securityFilePath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(options, appName);
            LOG.info(String.format("Firebase Initialized successfully for the App name :: %s", app.getName()));
            return app;
        } catch (IOException e) {
            LOG.info(String.format("Exception occurred - %s, while initializing the firebase connection for the App name :: %s", e.getMessage(),appName));
            e.printStackTrace();
            throw new IllegalStateException("Failed to initialize Firebase", e);
        }
    }

    /**
     * Send push notification
     *
     * @param token
     * @param title
     * @param body
     * @param image
     * @param user
     * @param app
     *
     * @return
     */
    public String sendNotification(String token, String title, String body, String image, B2BCustomerModel user, FirebaseApp app) {
        Notification notification;
        if(Objects.nonNull(image)){
            notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setImage(image)
                    .build();
        }
        else{
            notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();
        }
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();
        try {
            if(Objects.nonNull(app)){
                LOG.info(String.format("Getting instance of Push Notification to the app name :: %s  for the user uid :: %s and message id :: %s with device token :: %s with title :: %s, body :: %s and image :: %s", app.getName(), user.getUid(), messageId, token, title, body, image));
                String response = FirebaseMessaging.getInstance(app).send(message);
                LOG.info(String.format("Successfully sent push notification for the user uid :: %s and message id :: %s with device token :: %s with title :: %s, body :: %s and image :: %s and the response is :: %s", user.getUid(), messageId, token, title, body, image, response));
                return response;
            }
        } catch (Exception e) {
            LOG.info(String.format("Error occurred while sending push notification :: %s for the device token :: %s with title :: %s, body :: %s and image :: %s", e.getMessage(), token, title, body, image));
        }
        return StringUtils.EMPTY;
    }

}
