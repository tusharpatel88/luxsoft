package com.dws.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.NotificationService;

import lombok.Getter;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();
	private final ExecutorService executor = Executors.newFixedThreadPool(2);

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

	// Define a lock object to synchronize access to shared resources
	private final Object lock = new Object();

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void transferMoney(String accountFromId, String accountToId, BigDecimal amount,
			NotificationService notificationService) throws Exception {
		if (amount.compareTo(new BigDecimal(0)) <= 0) {
			throw new IllegalArgumentException("Amount to transfer must be a positive number");
		}

		try {
			Account accountFrom = getAccount(accountFromId);

			if (accountFrom == null)
				throw new IllegalArgumentException("Account not found: " + accountFromId);

			Account accountTo = getAccount(accountToId);
			if (accountTo == null)
				throw new IllegalArgumentException("Account not found: " + accountToId);

			
			Future<?> transferFromFuture = executor.submit(() -> transferFrom(accountFrom, amount));
			Future<?> transferToFuture = executor.submit(() -> transferTo(accountTo, amount));

			// Wait for both tasks to complete
			transferFromFuture.get();
			transferToFuture.get();
		} catch (Exception e) {
			// Handle exceptions as needed
			throw new RuntimeException("Error during transfer: " + e.getMessage(), e);
		}

	}

	private void transferTo(Account accountTo, BigDecimal amount) {
		synchronized (lock) {
			accountTo.setBalance(accountTo.getBalance().add(amount));
		}

	}

	private void transferFrom(Account accountFrom, BigDecimal amount) {
		synchronized (lock) {

			accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
		}
	}

}
