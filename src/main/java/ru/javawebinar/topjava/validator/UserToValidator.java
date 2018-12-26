package ru.javawebinar.topjava.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;

@Component
public class UserToValidator implements Validator {

    @Autowired
    private UserService service;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserTo.class.isAssignableFrom(clazz) || User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        boolean isNew = target instanceof User ? ((User)target).isNew() : ((UserTo)target).isNew();
        if (!isNew) {
            return;
        }
        String email = target instanceof User ? ((User)target).getEmail() : ((UserTo)target).getEmail();

        try {
            service.getByEmail(email);
            errors.rejectValue("email", "", "User with this email already exists");
        } catch (NotFoundException e) {
        }
    }
}
