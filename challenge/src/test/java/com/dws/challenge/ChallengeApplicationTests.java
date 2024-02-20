package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChallengeApplicationTests {

  @Autowired
  private AccountsService accountsService;



  @Test
  void addAccount() {
    Account account = new Account("Id-1236");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    
    Account account1 = new Account("Id-1237");
    account1.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account1);

    try {
		this.accountsService.transferMoney("Id-1236", "Id-1237", new BigDecimal(200));
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}
    
    Account account2 = accountsService.getAccount("Id-1236");
    Account account3 = accountsService.getAccount("Id-1237");
    assertThat(account2.getBalance()).isEqualTo("800");
    assertThat(account3.getBalance()).isEqualTo("1200");
  }
  
  
}
