package com.dws.challenge.repository;

import java.math.BigDecimal;

import org.springframework.transaction.annotation.Transactional;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;

public interface AccountsRepository {
	

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();
  
   void transferMoney(String accountFromId, String accountToId, BigDecimal amount,NotificationService notificationService ) throws Exception;

}
