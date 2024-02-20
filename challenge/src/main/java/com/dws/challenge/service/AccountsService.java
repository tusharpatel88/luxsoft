package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  @Getter
  private final NotificationService notificationService;

 
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
	this.notificationService = new EmailNotificationService();
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) throws Exception {
	this.accountsRepository.transferMoney(accountFromId, accountToId, amount, notificationService);
	
}


 
}
