package com.algocd.webportal.services;

import com.algocd.webportal.entities.User;
import com.algocd.webportal.services.models.CreateUserRecord;
import com.algocd.webportal.util.Result;
import jakarta.validation.Valid;

public interface UserService {
    Result<User> createUser(@Valid CreateUserRecord createUserRecord);
}
