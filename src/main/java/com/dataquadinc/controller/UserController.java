package com.dataquadinc.controller;

import com.dataquadinc.dto.EmployeeWithRole;
import com.dataquadinc.dto.UserDto;
import com.dataquadinc.dto.ResponseBean;
import com.dataquadinc.dto.UserResponse;
import com.dataquadinc.model.Roles;
import com.dataquadinc.model.UserDetails;
import com.dataquadinc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")

public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseBean<UserResponse>> registerUser(@Valid  @RequestBody UserDto userDto) throws RoleNotFoundException {

         return   userService.registerUser(userDto);

    }
    @GetMapping("/roles/{userId}")
    public ResponseEntity<Set<Roles>> getRolesByUserId(@PathVariable String userId ) {
        return userService.getRolesByUserId(userId);
    }

    @GetMapping("/employee")
    public ResponseEntity<List<EmployeeWithRole>> getTest() {
        List<EmployeeWithRole> employeeRoles = userService.getRolesId(3);

        if (employeeRoles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(employeeRoles, HttpStatus.OK);
    }



}
