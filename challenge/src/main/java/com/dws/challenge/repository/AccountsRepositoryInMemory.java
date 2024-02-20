package com.dws.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.NotificationService;

import lombok.Getter;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	 @Transactional(rollbackFor = Exception.class)
	public void transferMoney(String accountFromId, String accountToId, BigDecimal amount,
			NotificationService notificationService) throws Exception {
		if (amount.compareTo(new BigDecimal(0)) <= 0) {
			throw new IllegalArgumentException("Amount to transfer must be a positive number");
		}
		Account accountFrom = getAccount(accountFromId);
		if (accountFrom == null)
			new IllegalArgumentException("Account not found: " + accountFromId);

		Account accountTo = getAccount(accountToId);

		if (accountTo == null)
			new IllegalArgumentException("Account not found: " + accountFromId);

		try {
			// Perform transfer within a transaction
			accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
		} catch (Exception e) {
			throw new Exception("Error while subtracting balance" + e.getMessage());
		}
		try {
			accountTo.setBalance(accountTo.getBalance().add(amount));
		} catch (Exception e) {
			throw new Exception("Error while adding balance" + e.getMessage());
		}

		accounts.put(accountFromId, accountFrom);
		accounts.put(accountToId, accountTo);

		// Notify account holders
		notificationService.notifyAboutTransfer(accountTo,
				"Transfer to account " + accountTo.getAccountId() + ": $" + amount);
		notificationService.notifyAboutTransfer(accountFrom,
				"Transfer from account " + accountFrom.getAccountId() + ": $" + amount);

	}

}
