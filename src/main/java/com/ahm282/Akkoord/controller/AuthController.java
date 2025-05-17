package com.ahm282.Akkoord.controller;

import com.ahm282.Akkoord.dto.request.LoginDTO;
import com.ahm282.Akkoord.dto.request.RegisterDTO;
import com.ahm282.Akkoord.dto.response.ApiError;
import com.ahm282.Akkoord.dto.response.AuthResponseDTO;
import com.ahm282.Akkoord.dto.response.UserResponseDTO;
import com.ahm282.Akkoord.exception.ResourceNotFoundException;
import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.Department;
import com.ahm282.Akkoord.model.enums.UserAccessLevel;
import com.ahm282.Akkoord.model.security.UserPrincipal;
import com.ahm282.Akkoord.repository.AppUserRepository;
import com.ahm282.Akkoord.repository.DepartmentRepository;
import com.ahm282.Akkoord.security.jwt.JwtTokenProvider;
import com.ahm282.Akkoord.security.jwt.TokenBlacklist;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final DepartmentRepository departmentRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        AuthResponseDTO responseDTO = new AuthResponseDTO(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getFullName(),
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiError("Email is already taken!"));
        }

        // Create new user
        AppUser user = new AppUser();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        // Set role from request or default to VIEWER
        UserAccessLevel role = (registerRequest.getRole() != null) ?
                UserAccessLevel.valueOf(registerRequest.getRole()) :
                UserAccessLevel.VIEWER;
        user.setRole(role);

        // Handle department if ID is provided
        if (registerRequest.getDepartmentId() != null) {
            Department department = departmentRepository.findById(registerRequest.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            user.setDepartment(department);
        }

        AppUser savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponseDTO(
                        savedUser.getId(),
                        savedUser.getFullName(),
                        savedUser.getEmail(),
                        savedUser.getRole()
                ));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader(value = "Authorization", required = false) String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                Claims claims = jwtTokenProvider.extractAllClaims(token);
                Long expirationTime = claims.getExpiration().getTime();
                
                tokenBlacklist.addToBlacklist(token, expirationTime);
                return ResponseEntity.ok("Logged out successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid authorization header");
    }
}
