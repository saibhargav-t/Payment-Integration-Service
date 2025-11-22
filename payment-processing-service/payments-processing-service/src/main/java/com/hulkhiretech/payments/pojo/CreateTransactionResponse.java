package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class CreateTransactionResponse {

    private String txnReference;
    private String txnStatus;
}
