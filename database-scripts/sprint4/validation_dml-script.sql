-- MUST have priority 0
INSERT INTO validations.validation_rules (validatorName, isActive, priority)
VALUES ('CHECK1_VALIDATOR_RULE', true, 0);

-- Core rule, after the request is valid 10
INSERT INTO validations.validation_rules (validatorName, isActive, priority)
VALUES ('DUPLICATION_TXN_RULE', true, 10);


-- All field validations priority 20

-- All business validations priority 30
INSERT INTO validations.validation_rules (validatorName, isActive, priority)
VALUES ('PAYMENT_ATTEMPT_THRESHOLD_RULE', true, 30);


-- Validation Rules Param Configuration
INSERT INTO validations.validation_rules_params (validatorName, paramName, paramValue)
VALUES ('PAYMENT_ATTEMPT_THRESHOLD_RULE', 'durationInMins', "2");

INSERT INTO validations.validation_rules_params (validatorName, paramName, paramValue)
VALUES ('PAYMENT_ATTEMPT_THRESHOLD_RULE', 'maxPaymentThreshold', "5");
