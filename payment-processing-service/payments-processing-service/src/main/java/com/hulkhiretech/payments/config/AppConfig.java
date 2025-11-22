package com.hulkhiretech.payments.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.entity.Transaction;
import com.hulkhiretech.payments.util.PaymentMethodEnumConverter;
import com.hulkhiretech.payments.util.PaymentTypeEnumConverter;
import com.hulkhiretech.payments.util.ProviderEnumConverter;
import com.hulkhiretech.payments.util.TransactionStatusEnumConverter;

@Configuration
public class AppConfig {

    @Bean
     ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configure ModelMapper settings
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);

        // Create converter instances
        Converter<String, Integer> paymentMethodEnumConverter = new PaymentMethodEnumConverter();
        Converter<String, Integer> paymentTypeEnumConverter = new PaymentTypeEnumConverter();
        Converter<String, Integer> providerEnumConverter = new ProviderEnumConverter();
        Converter<String, Integer> transactionStatusEnumConverter = new TransactionStatusEnumConverter();

        // Create TypeMap for TransactionDto to Transaction mapping
        TypeMap<TransactionDto, Transaction> dtoToEntityTypeMap = modelMapper.createTypeMap(TransactionDto.class, Transaction.class);

        // Configure field mappings with converters
        dtoToEntityTypeMap.addMappings(mapper -> {
            mapper.using(paymentMethodEnumConverter)
                  .map(TransactionDto::getPaymentMethod, Transaction::setPaymentMethodId);

            mapper.using(providerEnumConverter)
                  .map(TransactionDto::getProvider, Transaction::setProviderId);

            mapper.using(paymentTypeEnumConverter)
                  .map(TransactionDto::getPaymentType, Transaction::setPaymentTypeId);

            mapper.using(transactionStatusEnumConverter)
                  .map(TransactionDto::getTxnStatus, Transaction::setTxnStatusId);
        });

        // Optional: Create reverse mapping from Transaction to TransactionDto
        TypeMap<Transaction, TransactionDto> entityToDtoTypeMap = modelMapper.createTypeMap(Transaction.class, TransactionDto.class);
        
        // Create reverse converters for the opposite direction
        Converter<Integer, String> reversePaymentMethodConverter = ctx -> {
            Integer id = ctx.getSource();
            return com.hulkhiretech.payments.constants.PaymentMethodEnum.getById(id).getName();
        };
        
        Converter<Integer, String> reverseProviderConverter = ctx -> {
            Integer id = ctx.getSource();
            return com.hulkhiretech.payments.constants.ProviderEnum.getById(id).getName();
        };
        
        Converter<Integer, String> reversePaymentTypeConverter = ctx -> {
            Integer id = ctx.getSource();
            return com.hulkhiretech.payments.constants.PaymentTypeEnum.getById(id).getName();
        };
        
        Converter<Integer, String> reverseTransactionStatusConverter = ctx -> {
            Integer id = ctx.getSource();
            return com.hulkhiretech.payments.constants.TransactionStatusEnum.getById(id).getName();
        };

        entityToDtoTypeMap.addMappings(mapper -> {
            mapper.using(reversePaymentMethodConverter)
                  .map(Transaction::getPaymentMethodId, TransactionDto::setPaymentMethod);

            mapper.using(reverseProviderConverter)
                  .map(Transaction::getProviderId, TransactionDto::setProvider);

            mapper.using(reversePaymentTypeConverter)
                  .map(Transaction::getPaymentTypeId, TransactionDto::setPaymentType);

            mapper.using(reverseTransactionStatusConverter)
                  .map(Transaction::getTxnStatusId, TransactionDto::setTxnStatus);
        });

        return modelMapper;
    }

	@Bean
	RestClient restClientConfig() {
		return RestClient.create();
	}
}