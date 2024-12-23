package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;

public interface IAuthenticationService {
    User register(UserRequest request) ;
    String authenticate(UserRequest request) ;

}
