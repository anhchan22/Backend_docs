package com.qlsv.dkmh.service;

import com.qlsv.dkmh.dto.request.UserRequest;
import com.qlsv.dkmh.dto.response.UserResponse;
import com.qlsv.dkmh.entity.User;
import com.qlsv.dkmh.enums.ErrorCode;
import com.qlsv.dkmh.enums.Role;
import com.qlsv.dkmh.exception.AppException;
import com.qlsv.dkmh.mapper.UserMapper;
import com.qlsv.dkmh.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest request) {
        if(userRepository.existsByMaSV(request.getMaSV()))
            throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toEntity(request);
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.ROLE_USER.name());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @PostAuthorize("returnObject.maSV == authentication.name ")
    public UserResponse getUser(String id) {
        log.info("In method get User with id: {}", id);
        return userMapper.toResponse(
                userRepository.findByMaSV(id).orElseThrow(() -> new AppException(ErrorCode.SV_NOT_FOUND)));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String maSV = context.getAuthentication().getName();

        User user =  userRepository.findByMaSV(maSV).orElseThrow(() -> new AppException(ErrorCode.SV_NOT_FOUND));
        return userMapper.toResponse(user);
    }
}
