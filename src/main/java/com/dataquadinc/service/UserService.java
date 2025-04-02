
package com.dataquadinc.service;

import com.dataquadinc.dto.*;
import com.dataquadinc.exceptions.UserNotFoundException;
import com.dataquadinc.exceptions.ValidationException;
import com.dataquadinc.mapper.UserMapper;
import com.dataquadinc.model.Roles;
import com.dataquadinc.model.UserDetails;
import com.dataquadinc.repository.RolesDao;
import com.dataquadinc.repository.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RolesDao rolesDao;

//    public ResponseEntity<ResponseBean<UserResponse>> registerUser(UserDto userDto) throws ValidationException {
//
//        Map<String,String> errors = new HashMap<>();
//
//
////        if (userDao.findByUserName(userDto.getUserName()) != null) {
////
////            errors.put("userName","userName already exists");
////        }
//
//        if (userDao.findByEmail(userDto.getEmail())!=null) {
//            errors.put("errormessage","email is already in use");
//        }
//        if (userDao.findByUserId(userDto.getUserId())!=null) {
//            errors.put("errorMessage","userId already exists");
//        }
//
//        if( !errors.isEmpty()) {
//            throw new ValidationException(errors);
//        }
//
//
//        // Encrypt the password
//        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        userDto.setConfirmPassword(passwordEncoder.encode(userDto.getConfirmPassword()));
//
//        // Convert DTO to entity
//        UserDetails user = userMapper.toEntity(userDto);
//
//        Set<Roles> roles = userDto.getRoles().stream()
//                .map(role -> {
//                    try {
//                        return rolesDao.findByName(role) // Find Role by its name from RolesDao
//                                .orElseThrow(() -> new ValidationException(Map.of("role","roleNotFound")));
//                    } catch (ValidationException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .collect(Collectors.toSet());
//
//        user.setRoles(roles);
//
//        // Save the user to the database
//        UserDetails dbUser = userDao.save(user);
//
//        UserResponse res=new UserResponse();
//        res.setUserName(dbUser.getUserName());
//        res.setUserId(dbUser.getUserId());
//        res.setEmail(dbUser.getEmail());
//        // Set success to true
//
//
//        ResponseBean<UserResponse> resp = new ResponseBean<UserResponse>();
//        resp.setSuccess(true);
//        resp.setMessage(" Created Sucessfully");
//        resp.setData(res);
//        resp.setError(null);
//
//        return new ResponseEntity<ResponseBean<UserResponse>>(resp,HttpStatus.CREATED);
//
//    }

    public ResponseEntity<ResponseBean<UserResponse>> registerUser(UserDto userDto) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        // Check if email or userId already exists
        if (userDao.findByEmail(userDto.getEmail()) != null) {
            errors.put("errormessage", userDto.getEmail()+" is already in use");
        }
        if (userDao.findByUserId(userDto.getUserId()) != null) {
            errors.put("errorMessage", userDto.getUserId() + " already exists. Please log in");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // Encrypt the password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setConfirmPassword(passwordEncoder.encode(userDto.getConfirmPassword()));

        // Convert DTO to entity
        UserDetails user = userMapper.toEntity(userDto);

        // Map roles to the user
        Set<Roles> roles = userDto.getRoles().stream()
                .map(role -> {
                    try {
                        return rolesDao.findByName(role) // Find Role by its name from RolesDao
                                .orElseThrow(() -> new ValidationException(Map.of("role", "roleNotFound")));
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);

        // Save the user to the database
        UserDetails dbUser = userDao.save(user);

        // Create a response object
        UserResponse res = new UserResponse();
        res.setUserName(dbUser.getUserName());
        res.setUserId(dbUser.getUserId());
        res.setEmail(dbUser.getEmail());

        // Create and send registration success email
        sendRegistrationConfirmationEmail(dbUser.getEmail());

        // Prepare the response bean
        ResponseBean<UserResponse> resp = new ResponseBean<>();
        resp.setSuccess(true);
        resp.setMessage("Created Successfully");
        resp.setData(res);
        resp.setError(null);

        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    // Method to send the registration confirmation email
    private void sendRegistrationConfirmationEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Registration Successful");
        message.setText("Your registration has been successfully completed. Welcome to our platform!");

        try {
            mailSender.send(message);
            System.out.println("Registration confirmation email sent to " + email);
        } catch (Exception e) {
            System.out.println("Error sending registration confirmation email to " + email);
            e.printStackTrace();
            throw new RuntimeException("Error sending registration confirmation email: " + e.getMessage());
        }
    }
    public ResponseEntity<Set<Roles>> getRolesByUserId(String UserId ) {
        UserDetails user = userDao.findByUserId(UserId);
        Set<Roles> roles = user.getRoles();
        return  ResponseEntity.ok(roles);

    }

//    public List<EmployeeWithRole> getRolesId(long id) {
//        List<UserDetails> list = userDao.findByRolesId(id);
//
//        if (list.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return list.stream()
//                .map(e -> new EmployeeWithRole(e.getUserId(), e.getUserName(),e.getRoles()))
//                .collect(Collectors.toList());
//    }
//
//    public ResponseEntity<List<EmployeeWithRole>> getAllEmployeesWithRoles() {
//        List<UserDetails> users = userDao.findAll();
//        if (users.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        List<EmployeeWithRole> employeeRoles = users.stream()
//                .map(user -> new EmployeeWithRole(
//                        user.getUserId(),
//                        user.getUserName(),
//                        user.getRoles()
//                ))
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<>(employeeRoles, HttpStatus.OK);
//    }


    public ResponseEntity<List<EmployeeWithRole>> getAllEmployeesWithRoles() {
        // Fetch all users from the database
        List<UserDetails> users = userDao.findAll();

        // If no users are found, return a No Content response
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Map each UserDetails to an EmployeeWithRole, flattening the roles into a single string
        List<EmployeeWithRole> employeeRoles = users.stream()
                .map(user -> {
                    // Ensure that user.getRoles() returns a valid Set<Roles> and handle empty roles correctly
                    String roleName = Optional.ofNullable(user.getRoles())  // Null check for user roles
                            .flatMap(roles -> roles.stream()
                                    .map(role -> role.getName().name())  // Access enum name as String
                                    .findFirst())  // Get the first role name if present
                            .orElse("No Role");  // Default to "No Role" if no roles are found or empty

                    // Create and return the EmployeeWithRole object
                    return new EmployeeWithRole(
                            user.getUserId(),       // Set user ID
                            user.getUserName(),
                            // Set user name
                            roleName  ,
                            user.getEmail(),
                            user.getDesignation(),
                            user.getJoiningDate(),
                            user.getGender(),
                            user.getDob(),
                            user.getPhoneNumber(),
                            user.getPersonalemail(),
                            user.getStatus()// Set role/ Set role name as a simple string
                    );
                })
                .collect(Collectors.toList());

        // Return the list of EmployeeWithRole objects with an OK response status
        return new ResponseEntity<>(employeeRoles, HttpStatus.OK);
    }

//    public ResponseEntity<ResponseBean<UserResponse>> updateUser(String userId, UserDto userDto) {
//        Map<String, String> errors = new HashMap<>();
//
//        // Check if the user exists
//        UserDetails existingUser = userDao.findByUserId(userId);
//        if (existingUser == null) {
//            ResponseBean<UserResponse> resp = new ResponseBean<>();
//            resp.setSuccess(false);
//            resp.setMessage("User not found");
//            return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
//        }
//
//        if (userDto.getUserName() == null || userDto.getUserName().isEmpty()) {
//            errors.put("userName", "User name is required and cannot be null or empty");
//            ResponseBean<UserResponse> resp = new ResponseBean<>();
//            resp.setSuccess(false);
//            resp.setMessage("Validation failed");
//            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
//
//
//            // Check if personalemail is provided and valid
//            if (userDto.getPersonalemail() == null || userDto.getPersonalemail().isEmpty()) {
//                errors.put("personalemail", "Personal email is required and cannot be null or empty");
//                ResponseBean<UserResponse> resp = new ResponseBean<>();
//                resp.setSuccess(false);
//                resp.setMessage("Validation failed");
//                return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
//            }
//
//        // Update the details (for example, the email, user name, etc.)
//        existingUser.setUserName(userDto.getUserName());
//        existingUser.setEmail(userDto.getEmail());
//        existingUser.setStatus(userDto.getStatus());
//        existingUser.setGender(userDto.getGender());
//        existingUser.setDesignation(userDto.getDesignation());
//        existingUser.setDob(userDto.getDob());
//        existingUser.setPersonalemail(userDto.getPersonalemail());  // Ensure this is not null or empty
//        existingUser.setJoiningDate(userDto.getJoiningDate());
//        existingUser.setPhoneNumber(userDto.getPhoneNumber());
//
//        // If password is provided, encode it and update it
//        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
//            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        }
//
//        // Handle roles update
//        Set<Roles> roles = userDto.getRoles().stream()
//                .map(role -> {
//                    try {
//                        return rolesDao.findByName(role)
//                                .orElseThrow(() -> new ValidationException(Map.of("role", "roleNotFound")));
//                    } catch (ValidationException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .collect(Collectors.toSet());
//        existingUser.setRoles(roles);
//
//        // Save the updated user
//        UserDetails updatedUser = userDao.save(existingUser);
//        System.out.println("Saved UserName: " + updatedUser.getUserName());
//
//        // Prepare response
//        UserResponse userResponse = new UserResponse();
//        userResponse.setUserName(updatedUser.getUserName());
//        userResponse.setUserId(updatedUser.getUserId());
//        userResponse.setEmail(updatedUser.getEmail());
//
//        // Prepare the response bean
//        ResponseBean<UserResponse> responseBean = new ResponseBean<>();
//        responseBean.setSuccess(true);
//        responseBean.setMessage("User updated successfully");
//        responseBean.setData(userResponse);
//        responseBean.setError(null);
//
//        return new ResponseEntity<>(responseBean, HttpStatus.OK);
//    }


    public ResponseEntity<ResponseBean<UserResponse>> updateUser(String userId, UserDto userDto) {
        Map<String, String> errors = new HashMap<>();

        // Check if the user exists
        UserDetails existingUser = userDao.findByUserId(userId);
        if (existingUser == null) {
            ResponseBean<UserResponse> resp = new ResponseBean<>();
            resp.setSuccess(false);
            resp.setMessage("User not found");
            return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
        }

        // Check if userName is provided and valid
        if (userDto.getUserName() == null || userDto.getUserName().isEmpty()) {
            errors.put("userName", "User name is required and cannot be null or empty");
        }

        // Check if personalemail is provided and valid
        if (userDto.getPersonalemail() == null || userDto.getPersonalemail().isEmpty()) {
            errors.put("personalemail", "Personal email is required and cannot be null or empty");
        }

        if (!errors.isEmpty()) {
            ResponseBean<UserResponse> resp = new ResponseBean<>();
            resp.setSuccess(false);
            resp.setMessage("Validation failed");
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        // Update the details (for example, the email, user name, etc.)
        existingUser.setUserName(userDto.getUserName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setStatus(userDto.getStatus());
        existingUser.setGender(userDto.getGender());
        existingUser.setDesignation(userDto.getDesignation());
        existingUser.setDob(userDto.getDob());
        existingUser.setPersonalemail(userDto.getPersonalemail());  // Ensure this is not null or empty
        existingUser.setJoiningDate(userDto.getJoiningDate());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());

        // If password is provided, encode it and update it
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Handle roles update
        Set<Roles> roles = userDto.getRoles().stream()
                .map(role -> {
                    try {
                        return rolesDao.findByName(role)
                                .orElseThrow(() -> new ValidationException(Map.of("role", "roleNotFound")));
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
        existingUser.setRoles(roles);

        // Save the updated user
        UserDetails updatedUser = userDao.save(existingUser);
        System.out.println("Saved UserName: " + updatedUser.getUserName());

        // Prepare response
        UserResponse userResponse = new UserResponse();
        userResponse.setUserName(updatedUser.getUserName());
        userResponse.setUserId(updatedUser.getUserId());
        userResponse.setEmail(updatedUser.getEmail());

        // Prepare the response bean
        ResponseBean<UserResponse> responseBean = new ResponseBean<>();
        responseBean.setSuccess(true);
        responseBean.setMessage("User updated successfully");
        responseBean.setData(userResponse);
        responseBean.setError(null);

        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    public ResponseEntity<ResponseBean<UserResponse>> deleteUser(String userId) {
        // Check if the user exists
        UserDetails user = userDao.findByUserId(userId);
        if (user == null) {
            ResponseBean<UserResponse> response = new ResponseBean<>();
            response.setSuccess(false);
            response.setMessage("User not found");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setUserName(user.getUserName());
        userResponse.setEmail(user.getEmail());

        // Delete the user from the database
        userDao.delete(user);



        // Prepare the response
        ResponseBean<UserResponse> response = new ResponseBean<>();
        response.setSuccess(true);
        response.setMessage("User deleted successfully");
        response.setData(userResponse);
        response.setError(null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public UserDetails getRecruiterById(String userId) {
        UserDetails recruiter = userDao.findByUserId(userId);
        if (recruiter == null) {
            throw new UserNotFoundException("Recruiter not found with ID: " + userId);
        }
        return recruiter;
    }

    // Method to get total submission count across all clients and jobs
    public long getTotalSubmissionsAcrossAllClientsAndJobs() {
        // Calling the query that counts all submissions across all job IDs and clients
        long totalSubmissions = userDao.countAllSubmissionsAcrossAllJobsAndClients();

        System.out.println("Total Submissions across all jobs and clients: " + totalSubmissions);

        return totalSubmissions;
    }


    public List<BdmEmployeeDTO> getAllBdmEmployees() {
        List<UserDetails> users = userDao.findBdmEmployees();  // Get BDM employees

        System.out.println("Total BDM employees found: " + users.size());

        return users.stream().map(user -> {
            String userId = user.getUserId();
            String userName = user.getUserName();

            System.out.println("\n==== Processing BDM: " + userName + " (ID: " + userId + ") ====");

            // Get the user's role
            String roleName = Optional.ofNullable(user.getRoles())
                    .flatMap(roles -> roles.stream()
                            .map(role -> role.getName().name())
                            .findFirst())
                    .orElse("No Role");

            // ✅ Count Clients (based on onboarding)
            long clientCount = userDao.countClientsByUserId(userId);
            System.out.println("Client Count: " + clientCount);

            // ✅ Get client names for this BDM
            List<String> clientNames = userDao.findClientNamesByUserId(userId);
            System.out.println("Client Names for this BDM: " + clientNames);

            // Initialize counters
            long submissionCount = 0;
            long interviewCount = 0;
            long placementCount = 0;
            long requirementsCount = 0; // ✅ New counter for requirements

            // If there are clients associated with this BDM
            if (!clientNames.isEmpty()) {
                for (String clientName : clientNames) {
                    System.out.println("Processing Client: " + clientName);

                    // ✅ Count ALL submissions for this client (across ALL job IDs)
                    submissionCount += userDao.countAllSubmissionsByClientName(clientName); // Updated method for count
                    System.out.println("Total Submission Count: " + submissionCount + " for Client: '" + clientName + "'");

                    // ✅ Count ALL Interviews for this client (across ALL job IDs)
                    interviewCount += userDao.countAllInterviewsByClientName(clientName);
                    System.out.println("Total Interview Count: " + interviewCount + " for Client: '" + clientName + "'");

                    // ✅ Count ALL Placements for this client (across ALL job IDs)
                    placementCount += userDao.countAllPlacementsByClientName(clientName);
                    System.out.println("Total Placement Count: " + placementCount + " for Client: '" + clientName + "'");

                    // ✅ Count ALL Requirements for this client
                    requirementsCount += userDao.countRequirementsByClientName(clientName);
                    System.out.println("Total Requirements Count: " + requirementsCount + " for Client: '" + clientName + "'");
                }
            }

            // Return DTO for BDM employee with all relevant counts
            return new BdmEmployeeDTO(
                    userId,
                    userName,
                    roleName,
                    user.getEmail(),
                    user.getStatus(),
                    clientCount,
                    requirementsCount,  // Moved requirementsCount after clientCount
                    submissionCount,  // Now submissionCount includes the total submissions for the BDM
                    interviewCount,
                    placementCount
            );
        }).collect(Collectors.toList());
    }
}


















