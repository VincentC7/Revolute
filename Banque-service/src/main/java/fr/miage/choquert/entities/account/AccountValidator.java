package fr.miage.choquert.entities.account;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.stereotype.Service;

@Service
public class AccountValidator {

    private final Validator validator;

    AccountValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(AccountInput accountInput) {
        Set<ConstraintViolation<AccountInput>> violations = validator.validate(accountInput);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
