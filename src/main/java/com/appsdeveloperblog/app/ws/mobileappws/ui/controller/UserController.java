package com.appsdeveloperblog.app.ws.mobileappws.ui.controller;

import com.appsdeveloperblog.app.ws.mobileappws.dto.UserDto;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.mobileappws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public String getUser()
    {
        return "get user was called";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)
    {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping
    public String changeUser()
    {
        return "change user was called";
    }

    @DeleteMapping
    public String deleteUser()
    {
        return "delete user was called";
    }
}
