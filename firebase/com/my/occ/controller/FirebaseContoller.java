package com.my.occ.controllers;

import com.google.firebase.FirebaseApp;
import com.my.core.firebase.FirebasePushNotificationService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
@RequestMapping(value = "/{baseSiteId}/pushNotification")
@Tag(name = "pushNotification")
public class FirebaseContoller {

    @Autowired
    UserService userService;

    @Autowired
    ModelService modelService;

    @Autowired
    FirebasePushNotificationService firebasePushNotificationService;

    @RequestMapping(value = "/saveToken", method = RequestMethod.POST)
    @Operation(operationId = "To save device token in the user", summary = "To save device token in the user")
    @ResponseBody
    @ApiBaseSiteIdParam
    public ResponseEntity<String> saveDeviceToken(@RequestParam(name = "deviceToken", required = true) String deviceToken, @RequestParam(name = "userid", required = true) String userid) throws Exception
    {
        if(StringUtils.isEmpty(deviceToken)){
            throw new NoSuchElementException("token should not be empty or null");
        }
        if(StringUtils.isEmpty(userid)){
            throw new NoSuchElementException("userid should not be empty or null");
        }

        B2BCustomerModel user = (B2BCustomerModel) userService.getUserForUID(userid);
        if (Objects.nonNull(user)) {
            user.setDeviceToken(deviceToken);
            modelService.save(user);
            modelService.refresh(user);
            return new ResponseEntity<>(String.format("Device token :: %s successfully saved for the user :: %s ", deviceToken, userid), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(String.format("Device token :: %s failed to save for the user :: %s", deviceToken, userid) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/sendNotification", method = RequestMethod.POST)
    @Operation(operationId = "Send notification to the user", summary = "Send notification to the user")
    @ResponseBody
    @ApiBaseSiteIdParam
    public ResponseEntity<String> sendNotification(@RequestParam(name = "notificationTitle", required = true) String notificationTitle,@RequestParam(name = "notificationBody", required = true) String notificationBody, @RequestParam(name = "userid", required = true) String userid) throws Exception
    {
        String response = StringUtils.EMPTY;
        if(StringUtils.isEmpty(userid)){
            throw new NoSuchElementException("userid should not be empty or null");
        }
        B2BCustomerModel user = (B2BCustomerModel) userService.getUserForUID(userid);

        FirebaseApp firebaseApp = firebasePushNotificationService.initializeFirebaseApp("firebaseApp", "SecurityCredential.json");
        if (Objects.nonNull(user) && Objects.nonNull(firebaseApp)) {
            firebasePushNotificationService.sendNotification(user.getDeviceToken(), notificationBody, notificationBody, null, user, firebaseApp);
        }
        if(StringUtils.isNotEmpty(response)){
            return new ResponseEntity<>(String.format("Push notification sent successfully to the user ", userid), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(String.format("Push notification request failed for the user :: %s", userid) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
